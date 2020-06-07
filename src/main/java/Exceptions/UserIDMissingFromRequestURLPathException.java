package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class UserIDMissingFromRequestURLPathException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    public static String message = "Variable: 'userID' Missing From Request URL Path";
    public String path;

    public UserIDMissingFromRequestURLPathException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}