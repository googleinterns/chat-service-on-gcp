package Exceptions;

import java.lang.RuntimeException;
import org.springframework.http.HttpStatus;
import Exceptions.APIException;
import java.util.List;

public class RequiredFieldMissingException extends RuntimeException implements APIException {

    private HttpStatus http_status = HttpStatus.BAD_REQUEST;
    private String message;
    private String path;

    public RequiredFieldMissingException(String path, List<String> missing) {
        super();
        this.path = path;
        this.message = String.join(", ", missing) + " fields missing.";
    }

    public HttpStatus getHttpStatus() {
        return this.http_status;
    }

    public String getMessage() {
        return this.message;
    }

    public String getPath() {
        return this.path;
    }
}
