package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class UserChatIDDoesNotExistException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.FORBIDDEN;
    public static String message = "User is not part of this Chat";
    public String path;

    public UserChatIDDoesNotExistException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}