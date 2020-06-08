package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class ChatAlreadyExistsException extends RuntimeException {

    public static HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;
    private static final String message = "Chat Already Exists";
    private final String path;

    public ChatAlreadyExistsException(String path) {
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