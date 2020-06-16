package exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public final class UserChatIdDoesNotExistException extends RuntimeException {

    public static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;
    private static final String message = "User is not part of this Chat";
    private final String path;

    public UserChatIdDoesNotExistException(String path) {
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
