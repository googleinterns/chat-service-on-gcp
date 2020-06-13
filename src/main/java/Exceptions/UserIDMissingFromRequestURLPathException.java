package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public final class UserIDMissingFromRequestURLPathException extends RuntimeException {

    public static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String message = "Variable: 'userID' Missing From Request URL Path";
    private final String path;

    public UserIDMissingFromRequestURLPathException(String path) {
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
