package exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public final class UserIdMissingFromRequestURLParameterException extends RuntimeException {

    public static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String message = "Parameter: 'userId' Missing From Request URL";
    private final String path;

    public UserIdMissingFromRequestURLParameterException(String path) {
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
