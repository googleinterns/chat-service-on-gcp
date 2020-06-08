package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class UserIDMissingFromRequestURLParameterException extends RuntimeException {

    public static HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String message = "Parameter: 'userID' Missing From Request URL";
    private final String path;

    public UserIDMissingFromRequestURLParameterException(String path) {
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