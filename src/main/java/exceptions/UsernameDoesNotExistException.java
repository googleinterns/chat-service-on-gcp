package exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public final class UsernameDoesNotExistException extends RuntimeException {

    public static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
    private static final String message = "Username Does Not Exist";
    private final String path;

    public UsernameDoesNotExistException(String path) {
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
