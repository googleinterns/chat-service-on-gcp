package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class UserChatIDDoesNotExistException extends RuntimeException {

    public static HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;
    private static final String message = "User is not part of this Chat";
    private final String path;

    public UserChatIDDoesNotExistException(String path) {
        super();
        this.path = path;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
