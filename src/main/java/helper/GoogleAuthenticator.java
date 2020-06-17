package helper;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
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

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class GoogleAuthenticator {

    @Autowired
    private GoogleUserMapper googleUserMapper;

    @Autowired
    private UserAccessor userAccessor;

    private static final String CLIENT_SECRET_FILE =
        "src/main/resources/client_secret_731468299245-enaig2iil558lcjp15jmejvpphn5vpg5.apps.googleusercontent.com.json";
    private static final String REDIRECT_URI = "";
    private final GoogleTokenResponse tokenResponse;

    public GoogleAuthenticator(String authCode) throws IOException {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(), new FileReader(CLIENT_SECRET_FILE));
        tokenResponse =
            new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    "https://oauth2.googleapis.com/token",
                    clientSecrets.getDetails().getClientId(),
                    clientSecrets.getDetails().getClientSecret(),
                    authCode,
                    REDIRECT_URI)
                    .execute();
    }

    public GoogleCredentials getCredentials() throws IOException {
        String accessTokenString = tokenResponse.getAccessToken();
        AccessToken accessToken = new AccessToken(accessTokenString, null);
        return new GoogleCredentials(accessToken);
    }

    public String getPhoneNumber(String userId) throws IOException {
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
            return null;
        }
        return phoneNumberList.get(0).getValue();
    }

    public User getUser() throws IOException {
        GoogleIdToken idToken = tokenResponse.parseIdToken();
        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String username = googleUserMapper.getUsernameFromEmail(email);
        User existingUser = userAccessor.checkIfUserExists(username, email);
        if(existingUser != null) {
            return existingUser;
        }
        String picture = null;
        Object pictureUrl = payload.get("picture");
        if(pictureUrl != null) {
            picture = googleUserMapper.getPictureFromUrl(pictureUrl.toString());
        }
        String userId = payload.getSubject();
        String mobileNo = getPhoneNumber(userId);
        return new User(username, email, mobileNo, picture);
    }
}
