package Exceptions;

import org.springframework.http.HttpStatus;

public abstract interface APIException {
	public HttpStatus getHttpStatus();
	public String getMessage();
	public String getPath();
}