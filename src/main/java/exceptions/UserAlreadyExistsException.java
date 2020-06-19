package exceptions;

import java.lang.RuntimeException;
import java.util.EnumSet;

import entity.User;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends RuntimeException implements APIException {

    private static final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;
    private String message;
    private final String path;

    public UserAlreadyExistsException(String path, EnumSet<User.UniqueFields> usedFields) {
        super();
        this.path = path;
        message = "";
        if(usedFields.contains(User.UniqueFields.USERNAME)) {
            message += "Username ";
        }
        if(usedFields.contains(User.UniqueFields.EMAIL)) {
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
