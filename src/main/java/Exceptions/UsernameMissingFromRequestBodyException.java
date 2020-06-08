package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class UsernameMissingFromRequestBodyException extends RuntimeException {

    public static HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String message = "Key: 'username' Missing From Request Body";
    private final String path;

    public UsernameMissingFromRequestBodyException(String path) {
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
