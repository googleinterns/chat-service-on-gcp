package exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public final class EmptyMessageException extends RuntimeException {

    public static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String message = "Empty Message Cannot Be Sent";
    private final String path;

    public EmptyMessageException(String path) {
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
