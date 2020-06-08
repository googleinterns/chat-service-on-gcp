package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;
import Exceptions.APIException;

public class InvalidLoginException extends RuntimeException implements APIException {

    private HttpStatus http_status = HttpStatus.CONFLICT;
    private String message = "Invalid Username or Password";
    private String path;

    public InvalidLoginException(String path) {
        super();
        this.path = path;
    }

    public HttpStatus getHttpStatus() {
        return this.http_status;
    }

    public String getMessage() {
        return this.message;
    }

    public String getPath() {
        return path;
    }
}
