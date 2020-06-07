package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class MessageIDDoesNotBelongToChatIDException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.FORBIDDEN;
    public static String message = "MessageID Does Not Belong To ChatID";
    public String path;

    public MessageIDDoesNotBelongToChatIDException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}