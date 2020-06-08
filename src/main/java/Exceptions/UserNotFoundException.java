package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;
import Exceptions.APIException;

public class UserNotFoundException extends RuntimeException implements APIException {

    private static final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;
    private String message = "Invalid Username";
    private String path;

    public UserNotFoundException(String path) {
        super();
        this.path = path;
    }

    public HttpStatus getHttpStatus() {
        return this.HTTP_STATUS;
    }

    public String getMessage() {
        return this.message;
    }

    public String getPath() {
        return path;
    }
}
