package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class MessageIDDoesNotExistException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    public static String message = "MessageID Does Not Exist";
    public String path;

    public MessageIDDoesNotExistException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}