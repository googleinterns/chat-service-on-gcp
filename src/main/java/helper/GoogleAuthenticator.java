package helper;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;

@Component
public class GoogleAuthenticator {

    private static final String CLIENT_SECRET_FILE =
        "src/main/resources/client_secret_731468299245-enaig2iil558lcjp15jmejvpphn5vpg5.apps.googleusercontent.com.json";
    private static final String REDIRECT_URI = "";
    private final GoogleClientSecrets clientSecrets;

    public GoogleAuthenticator() throws IOException {
        clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(), new FileReader(CLIENT_SECRET_FILE));
    }

    public GoogleUser getGoogleUser(String authCode) throws IOException {
        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        "https://oauth2.googleapis.com/token",
                        clientSecrets.getDetails().getClientId(),
                        clientSecrets.getDetails().getClientSecret(),
                        authCode,
                        REDIRECT_URI)
                        .execute();
        return new GoogleUser(tokenResponse);
    }

}
