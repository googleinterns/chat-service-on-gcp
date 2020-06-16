package exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public final class MessageIdMissingFromRequestURLParameterException extends RuntimeException {

    public static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String message = "Parameter: 'messageId' Missing From Request URL";
    private final String path;

    public MessageIdMissingFromRequestURLParameterException(String path) {
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
