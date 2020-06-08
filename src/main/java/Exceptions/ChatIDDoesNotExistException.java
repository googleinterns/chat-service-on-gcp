package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class ChatIDDoesNotExistException extends RuntimeException {

    public static HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
    private static final String message = "ChatID Does Not Exist";
    private final String path;

    public ChatIDDoesNotExistException(String path) {
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
