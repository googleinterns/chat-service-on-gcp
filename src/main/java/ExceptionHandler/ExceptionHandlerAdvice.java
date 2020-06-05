package ExceptionHandler;

import Exceptions.UsernameAlreadyExistsException;
import Exceptions.UsernameDoesNotExistException;
import Exceptions.ChatAlreadyExistsException;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.ChatIDDoesNotExistException;
import Exceptions.MessageIDDoesNotExistException;

import java.util.Map;
import java.util.LinkedHashMap;
import java.time.LocalDateTime;
import java.lang.RuntimeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice { 

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", UsernameAlreadyExistsException.httpStatus.value());
        responseBody.put("error", UsernameAlreadyExistsException.httpStatus.toString());
        responseBody.put("message", UsernameAlreadyExistsException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UsernameAlreadyExistsException.httpStatus);
    }  
     
    @ExceptionHandler(UsernameDoesNotExistException.class)
    public ResponseEntity<Object> handleUsernameDoesNotExistException(UsernameDoesNotExistException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", UsernameDoesNotExistException.httpStatus.value());
        responseBody.put("error", UsernameDoesNotExistException.httpStatus.toString());
        responseBody.put("message", UsernameDoesNotExistException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UsernameDoesNotExistException.httpStatus);
    } 
    
    @ExceptionHandler(ChatAlreadyExistsException.class)
    public ResponseEntity<Object> handleChatAlreadyExistsException(ChatAlreadyExistsException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", ChatAlreadyExistsException.httpStatus.value());
        responseBody.put("error", ChatAlreadyExistsException.httpStatus.toString());
        responseBody.put("message", ChatAlreadyExistsException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, ChatAlreadyExistsException.httpStatus);
    }

    @ExceptionHandler(UserIDDoesNotExistException.class)
    public ResponseEntity<Object> handleUserIDDoesNotExistException(UserIDDoesNotExistException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", UserIDDoesNotExistException.httpStatus.value());
        responseBody.put("error", UserIDDoesNotExistException.httpStatus.toString());
        responseBody.put("message", UserIDDoesNotExistException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UserIDDoesNotExistException.httpStatus);
    }

    @ExceptionHandler(ChatIDDoesNotExistException.class)
    public ResponseEntity<Object> handleChatIDDoesNotExistException(ChatIDDoesNotExistException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", ChatIDDoesNotExistException.httpStatus.value());
        responseBody.put("error", ChatIDDoesNotExistException.httpStatus.toString());
        responseBody.put("message", ChatIDDoesNotExistException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, ChatIDDoesNotExistException.httpStatus);
    }  

    @ExceptionHandler(MessageIDDoesNotExistException.class)
    public ResponseEntity<Object> handleMessageIDDoesNotExistException(MessageIDDoesNotExistException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", MessageIDDoesNotExistException.httpStatus.value());
        responseBody.put("error", MessageIDDoesNotExistException.httpStatus.toString());
        responseBody.put("message", MessageIDDoesNotExistException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, MessageIDDoesNotExistException.httpStatus);
    }  
} 