package googlesignin;

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
import com.google.common.collect.ImmutableList;
import dbaccessor.user.UserAccessor;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public final class GoogleUser {

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
        ImmutableList<PhoneNumber> phoneNumberList = ImmutableList.copyOf(profile.getPhoneNumbers());
        if(phoneNumberList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(phoneNumberList.get(0).getValue());
    }

    /**
     * Retrieves the email-id of signed-in user
     * Checks if an entry with the same email-id exists
     * If it exists, then return the UserID of existing user
     * Else create a new entry in the User table
     * And return the UserID of the newly created user
     */
    public long getOrCreateUser() throws IOException {
        GoogleIdToken idToken = tokenResponse.parseIdToken();
        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        OptionalLong existingUser = userAccessor.getUserIdFromEmail(email);
        if(existingUser.isPresent()) {
            return existingUser.getAsLong();
        }
        String username = GoogleUserMapper.getUsernameFromEmail(email);
        String picture = null;
        Object pictureUrl = payload.get("picture");
        if(pictureUrl != null) {
            picture = GoogleUserMapper.getPictureFromUrl(pictureUrl.toString());
        }
        String userId = payload.getSubject();
        Optional<String> mobileNo = getPhoneNumber(userId);
        User newUser = mobileNo.isPresent()
                                ?
                        User.newBuilder()
                            .username(username)
                            .emailId(email)
                            .mobileNo(mobileNo.get())
                            .picture(picture)
                            .build()
                                :
                        User.newBuilder()
                            .username(username)
                            .emailId(email)
                            .picture(picture)
                            .build();
        return userAccessor.insert(newUser);
    }
}
