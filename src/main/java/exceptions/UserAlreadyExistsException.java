package exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends RuntimeException implements APIException {

    private static final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;
    private String message;
    private final String path;

    public UserAlreadyExistsException(String path, int bitMask) {
        super();
        this.path = path;
        message = "";
        if((bitMask & 1) > 0) {
            message += "Username ";
        }
        if((bitMask & 2) > 0) {
            message += "EmailId ";
        }
        message += "already exists.";
    }

    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }

    public String getMessage() {
        return this.message;
    }

    public String getPath() {
        return path;
    }
}
