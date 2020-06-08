package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends RuntimeException {

    public static HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;
    private static final String message = "Username Already Exists";
    private final String path;

    public UsernameAlreadyExistsException(String path) {
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