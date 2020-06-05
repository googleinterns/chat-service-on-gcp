package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class UsernameDoesNotExistException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    public static String message = "Username Does Not Exist";
    public String path;

    public UsernameDoesNotExistException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}