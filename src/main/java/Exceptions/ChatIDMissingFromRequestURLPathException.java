package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class ChatIDMissingFromRequestURLPathException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    public static String message = "Variable: 'chatID' Missing From Request URL Path";
    public String path;

    public ChatIDMissingFromRequestURLPathException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}