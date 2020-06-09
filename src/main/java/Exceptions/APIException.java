package Exceptions;

import org.springframework.http.HttpStatus;

public interface APIException {
	public HttpStatus getHttpStatus();
	public String getMessage();
	public String getPath();
}
