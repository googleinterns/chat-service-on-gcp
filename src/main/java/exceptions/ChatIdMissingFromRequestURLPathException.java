package exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public final class ChatIdMissingFromRequestURLPathException extends RuntimeException {

    public static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String message = "Variable: 'chatId' Missing From Request URL Path";
    private final String path;

    public ChatIdMissingFromRequestURLPathException(String path) {
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
