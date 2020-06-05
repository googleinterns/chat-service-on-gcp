package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class ChatAlreadyExistsException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.CONFLICT;
    public static String message = "Chat Already Exists";
    public String path;

    public ChatAlreadyExistsException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}