package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;
import Exceptions.APIException;
import java.util.List;

public class UserRequiredFieldMissingException extends RuntimeException implements APIException {

    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private String message;
    private final String path;

    public UserRequiredFieldMissingException(String path, List<String> missing) {
        super();
        this.path = path;
        this.message = String.join(", ", missing) + " fields missing.";
    }

    public HttpStatus getHttpStatus() {
        return this.HTTP_STATUS;
    }

    public String getMessage() {
        return this.message;
    }

    public String getPath() {
        return this.path;
    }
}
