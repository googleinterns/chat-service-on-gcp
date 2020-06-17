package helper;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import dbaccessor.user.UserAccessor;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class GoogleUser {

    @Autowired
    private GoogleUserMapper googleUserMapper;

    @Autowired
    private UserAccessor userAccessor;

    private final GoogleTokenResponse tokenResponse;

    public GoogleUser(GoogleTokenResponse tokenResponse) {
        this.tokenResponse = tokenResponse;
    }

    private GoogleCredentials getCredentials() throws IOException {
        String accessTokenString = tokenResponse.getAccessToken();
        AccessToken accessToken = new AccessToken(accessTokenString, null);
        return new GoogleCredentials(accessToken);
    }

    public Optional<String> getPhoneNumber(String userId) throws IOException {
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(getCredentials());
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        PeopleService peopleService =
                new PeopleService.Builder(httpTransport, jsonFactory, requestInitializer).build();
        Person profile = peopleService.people().get("people/" + userId)
                .setPersonFields("phoneNumbers")
                .execute();
        List<PhoneNumber> phoneNumberList = profile.getPhoneNumbers();
        if(phoneNumberList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(phoneNumberList.get(0).getValue());
    }

    /** Retrieves the username and email-id of signed-in user
     * Checks if an entry with the same username and email-id exists
     * If it exists, then return the UserID of existing user
     * Else create a new entry in the User table
     * And return the UserID of the newly created user */
    public long getOrCreateUser() throws IOException {
        GoogleIdToken idToken = tokenResponse.parseIdToken();
        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String username = googleUserMapper.getUsernameFromEmail(email);
        Optional<Long> existingUser = userAccessor.getUserIdFromUsernameAndEmail(username, email);
        if(existingUser.isPresent()) {
            return existingUser.get();
        }
        String picture = null;
        Object pictureUrl = payload.get("picture");
        if(pictureUrl != null) {
            picture = googleUserMapper.getPictureFromUrl(pictureUrl.toString());
        }
        String userId = payload.getSubject();
        Optional<String> mobileNo = getPhoneNumber(userId);
        User newUser;
        if(mobileNo.isPresent()) {
            newUser = new User(username, email, mobileNo.get(), picture);
        } else {
            newUser = new User(username, email, picture);
        }
        long id = userAccessor.insert(newUser);
        return id;
    }
}
