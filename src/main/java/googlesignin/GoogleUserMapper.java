package googlesignin;

import dbaccessor.user.UserAccessor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

public final class GoogleUserMapper {

    @Autowired
    private static UserAccessor userAccessor;

    private GoogleUserMapper() { }

    /**
     * Extracts username from email.
     * Ex: let email be xyz@gmail.com
     * then, extract "xyz".
     * If this username is not already present in User table,
     * returns this username.
     * Else appends it with some random string of length 3
     * and repeats this until an unused username is found
     * returns that username.
     */
    public static String getUsernameFromEmail(String email) {
        int usernameEndsAt = email.indexOf('@');
        if(usernameEndsAt == -1) {
            throw new IllegalArgumentException();
        }
        String baseUsername = email.substring(0, usernameEndsAt);
        String username = baseUsername;
        while(userAccessor.checkIfUsernameExists(username)) {
            username = baseUsername + RandomStringUtils.randomAlphanumeric(3);
        }
        return username;
    }

    public static String getPictureFromUrl(String pictureUrl) throws IOException {
        URL imageUrl = new URL(pictureUrl);
        URLConnection connection = imageUrl.openConnection();
        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = 0;
        while((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
            baos.write(buffer, 0, read);
        }
        baos.flush();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(baos.toByteArray());
    }
}
