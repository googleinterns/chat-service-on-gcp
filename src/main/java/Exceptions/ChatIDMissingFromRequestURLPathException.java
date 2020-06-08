package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class ChatIDMissingFromRequestURLPathException extends RuntimeException {

    public static HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String message = "Variable: 'chatID' Missing From Request URL Path";
    private final String path;

    public ChatIDMissingFromRequestURLPathException(String path) {
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
