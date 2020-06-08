package ExceptionHandler;

import Exceptions.*;

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
        responseBody.put("status", UsernameAlreadyExistsException.HTTP_STATUS.value());
        responseBody.put("error", UsernameAlreadyExistsException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UsernameAlreadyExistsException.HTTP_STATUS);
    }  
     
    @ExceptionHandler(UsernameDoesNotExistException.class)
    public ResponseEntity<Object> handleUsernameDoesNotExistException(UsernameDoesNotExistException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", UsernameDoesNotExistException.HTTP_STATUS.value());
        responseBody.put("error", UsernameDoesNotExistException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UsernameDoesNotExistException.HTTP_STATUS);
    } 
    
    @ExceptionHandler(ChatAlreadyExistsException.class)
    public ResponseEntity<Object> handleChatAlreadyExistsException(ChatAlreadyExistsException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", ChatAlreadyExistsException.HTTP_STATUS.value());
        responseBody.put("error", ChatAlreadyExistsException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, ChatAlreadyExistsException.HTTP_STATUS);
    }

    @ExceptionHandler(UserIDDoesNotExistException.class)
    public ResponseEntity<Object> handleUserIDDoesNotExistException(UserIDDoesNotExistException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", UserIDDoesNotExistException.HTTP_STATUS.value());
        responseBody.put("error", UserIDDoesNotExistException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UserIDDoesNotExistException.HTTP_STATUS);
    }

    @ExceptionHandler(ChatIDDoesNotExistException.class)
    public ResponseEntity<Object> handleChatIDDoesNotExistException(ChatIDDoesNotExistException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", ChatIDDoesNotExistException.HTTP_STATUS.value());
        responseBody.put("error", ChatIDDoesNotExistException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, ChatIDDoesNotExistException.HTTP_STATUS);
    }  

    @ExceptionHandler(MessageIDDoesNotExistException.class)
    public ResponseEntity<Object> handleMessageIDDoesNotExistException(MessageIDDoesNotExistException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", MessageIDDoesNotExistException.HTTP_STATUS.value());
        responseBody.put("error", MessageIDDoesNotExistException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, MessageIDDoesNotExistException.HTTP_STATUS);
    }  
    
    @ExceptionHandler(UserChatIDDoesNotExistException.class)
    public ResponseEntity<Object> handleUserChatIDDoesNotExistException(UserChatIDDoesNotExistException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", UserChatIDDoesNotExistException.HTTP_STATUS.value());
        responseBody.put("error", UserChatIDDoesNotExistException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UserChatIDDoesNotExistException.HTTP_STATUS);
    }  

    @ExceptionHandler(MessageIDDoesNotBelongToChatIDException.class)
    public ResponseEntity<Object> handleMessageIDDoesNotBelongToChatIDException(MessageIDDoesNotBelongToChatIDException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", MessageIDDoesNotBelongToChatIDException.HTTP_STATUS.value());
        responseBody.put("error", MessageIDDoesNotBelongToChatIDException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, MessageIDDoesNotBelongToChatIDException.HTTP_STATUS);
    }

    @ExceptionHandler(UsernameMissingFromRequestBodyException.class)
    public ResponseEntity<Object> handleUsernameMissingFromRequestBodyException(UsernameMissingFromRequestBodyException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", UsernameMissingFromRequestBodyException.HTTP_STATUS.value());
        responseBody.put("error", UsernameMissingFromRequestBodyException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UsernameMissingFromRequestBodyException.HTTP_STATUS);
    }

    @ExceptionHandler(UserIDMissingFromRequestURLPathException.class)
    public ResponseEntity<Object> handleUserIDMissingFromRequestURLPathException(UserIDMissingFromRequestURLPathException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", UserIDMissingFromRequestURLPathException.HTTP_STATUS.value());
        responseBody.put("error", UserIDMissingFromRequestURLPathException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UserIDMissingFromRequestURLPathException.HTTP_STATUS);
    }

    @ExceptionHandler(UserIDMissingFromRequestURLParameterException.class)
    public ResponseEntity<Object> handleUserIDMissingFromRequestURLParameterException(UserIDMissingFromRequestURLParameterException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", UserIDMissingFromRequestURLParameterException.HTTP_STATUS.value());
        responseBody.put("error", UserIDMissingFromRequestURLParameterException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UserIDMissingFromRequestURLParameterException.HTTP_STATUS);
    }

    @ExceptionHandler(ChatIDMissingFromRequestURLParameterException.class)
    public ResponseEntity<Object> handleChatIDMissingFromRequestURLParameterException(ChatIDMissingFromRequestURLParameterException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", ChatIDMissingFromRequestURLParameterException.HTTP_STATUS.value());
        responseBody.put("error", ChatIDMissingFromRequestURLParameterException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, ChatIDMissingFromRequestURLParameterException.HTTP_STATUS);
    }

    @ExceptionHandler(TextContentMissingFromRequestBodyException.class)
    public ResponseEntity<Object> handleTextContentMissingFromRequestBodyException(TextContentMissingFromRequestBodyException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", TextContentMissingFromRequestBodyException.HTTP_STATUS.value());
        responseBody.put("error", TextContentMissingFromRequestBodyException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, TextContentMissingFromRequestBodyException.HTTP_STATUS);
    }

    @ExceptionHandler(ContentTypeMissingFromRequestBodyException.class)
    public ResponseEntity<Object> handleContentTypeMissingFromRequestBodyException(ContentTypeMissingFromRequestBodyException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", ContentTypeMissingFromRequestBodyException.HTTP_STATUS.value());
        responseBody.put("error", ContentTypeMissingFromRequestBodyException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, ContentTypeMissingFromRequestBodyException.HTTP_STATUS);
    }

    @ExceptionHandler(ChatIDMissingFromRequestURLPathException.class)
    public ResponseEntity<Object> handleChatIDMissingFromRequestURLPathException(ChatIDMissingFromRequestURLPathException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", ChatIDMissingFromRequestURLPathException.HTTP_STATUS.value());
        responseBody.put("error", ChatIDMissingFromRequestURLPathException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, ChatIDMissingFromRequestURLPathException.HTTP_STATUS);
    }

    @ExceptionHandler(MessageIDMissingFromRequestURLParameterException.class)
    public ResponseEntity<Object> handleMessageIDMissingFromRequestURLParameterException(MessageIDMissingFromRequestURLParameterException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", MessageIDMissingFromRequestURLParameterException.HTTP_STATUS.value());
        responseBody.put("error", MessageIDMissingFromRequestURLParameterException.HTTP_STATUS.toString());
        responseBody.put("message", e.getMessage());
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, MessageIDMissingFromRequestURLParameterException.HTTP_STATUS);
    }
} 
