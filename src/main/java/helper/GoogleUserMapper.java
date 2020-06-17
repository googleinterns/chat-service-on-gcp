package helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

public class GoogleUserMapper {
    public String getUsernameFromEmail(String email) {
        int usernameEndsAt = email.indexOf('@');
        if(usernameEndsAt == -1) {
            return null;
        }
        return email.substring(0, usernameEndsAt);
    }

    public String getPictureFromUrl(String pictureUrl) throws IOException {
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
