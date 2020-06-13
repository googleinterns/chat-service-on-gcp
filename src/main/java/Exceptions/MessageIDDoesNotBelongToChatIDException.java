package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public final class MessageIDDoesNotBelongToChatIDException extends RuntimeException {

    public static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;
    private static final String message = "MessageID Does Not Belong To ChatID";
    private final String path;

    public MessageIDDoesNotBelongToChatIDException(String path) {
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
