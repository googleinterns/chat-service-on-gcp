package exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends RuntimeException implements APIException {

    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
    private static final String MESSAGE = "Invalid Username";
    private final String path;

    public UserNotFoundException(String path) {
        super();
        this.path = path;
    }

    public HttpStatus getHttpStatus() {
        return this.HTTP_STATUS;
    }

    public String getMessage() {
        return this.MESSAGE;
    }

    public String getPath() {
        return path;
    }
}
