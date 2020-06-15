package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public final class MessageIdDoesNotBelongToChatIdException extends RuntimeException {

    public static final HttpStatus HTTP_STATUS = HttpStatus.FORBIDDEN;
    private static final String message = "MessageId Does Not Belong To ChatId";
    private final String path;

    public MessageIdDoesNotBelongToChatIdException(String path) {
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
