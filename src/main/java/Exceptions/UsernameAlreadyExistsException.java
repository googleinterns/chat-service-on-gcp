package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends RuntimeException {

    public static HttpStatus httpStatus = HttpStatus.CONFLICT;
    public static String message = "Username Already Exists";
    public String path;

    public UsernameAlreadyExistsException(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}