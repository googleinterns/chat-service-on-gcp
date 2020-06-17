package helper;

import exceptions.APIException;

import java.util.LinkedHashMap;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ErrorResponseBody {
	public Map<String, Object> getResponseBody(APIException e) {
        Map<String, Object> response_body = new LinkedHashMap<>();
        response_body.put("timestamp", LocalDateTime.now());
        response_body.put("status", e.getHttpStatus().value());
        response_body.put("error", e.getHttpStatus().toString());
        response_body.put("message", e.getMessage());
        response_body.put("path", e.getPath());
        return response_body;
    }
}
