package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class MessageIDMissingFromRequestURLParameterException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    public static String message = "Parameter: 'messageID' Missing From Request URL";
    public String path;

    public MessageIDMissingFromRequestURLParameterException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}