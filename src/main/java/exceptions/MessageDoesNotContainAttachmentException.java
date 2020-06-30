package exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public final class MessageDoesNotContainAttachmentException extends RuntimeException {

    public static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
    private static final String message = "Message Does Not Contain Attachment";
    private final String path;

    public MessageDoesNotContainAttachmentException(String path) {
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
