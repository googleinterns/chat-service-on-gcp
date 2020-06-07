package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class UsernameMissingFromRequestBodyException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    public static String message = "Key: 'username' Missing From Request Body";
    public String path;

    public UsernameMissingFromRequestBodyException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}