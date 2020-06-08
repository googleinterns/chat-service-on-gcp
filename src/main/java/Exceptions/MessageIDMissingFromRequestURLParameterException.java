package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class MessageIDMissingFromRequestURLParameterException extends RuntimeException {

    public static HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String message = "Parameter: 'messageID' Missing From Request URL";
    private final String path;

    public MessageIDMissingFromRequestURLParameterException(String path) {
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