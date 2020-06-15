package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public final class InvalidCountValueInRequestURLParameterException extends RuntimeException {

    public static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String message = "Invalid Value Of Parameter: count in Request URL";
    private final String path;

    public InvalidCountValueInRequestURLParameterException(String path) {
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
