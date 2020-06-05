package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class UserIDDoesNotExistException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    public static String message = "UserID Does Not Exist";
    public String path;

    public UserIDDoesNotExistException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}