package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class MessageIDDoesNotExistException extends RuntimeException {

    public static HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
    private static final String message = "MessageID Does Not Exist";
    private final String path;

    public MessageIDDoesNotExistException(String path) {
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
