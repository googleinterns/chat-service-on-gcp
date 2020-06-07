package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class TextContentMissingFromRequestBodyException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    public static String message = "Key: 'textContent' Missing From Request Body";
    public String path;

    public TextContentMissingFromRequestBodyException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}