package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class ChatIDDoesNotExistException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    public static String message = "ChatID Does Not Exist";
    public String path;

    public ChatIDDoesNotExistException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}