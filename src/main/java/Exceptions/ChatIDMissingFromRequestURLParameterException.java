package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class ChatIDMissingFromRequestURLParameterException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    public static String message = "Parameter: 'chatID' Missing From Request URL";
    public String path;

    public ChatIDMissingFromRequestURLParameterException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}