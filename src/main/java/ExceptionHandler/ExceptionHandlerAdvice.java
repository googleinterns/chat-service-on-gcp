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
    
    @ExceptionHandler(UserChatIDDoesNotExistException.class)
    public ResponseEntity<Object> handleUserChatIDDoesNotExistException(UserChatIDDoesNotExistException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", UserChatIDDoesNotExistException.httpStatus.value());
        responseBody.put("error", UserChatIDDoesNotExistException.httpStatus.toString());
        responseBody.put("message", UserChatIDDoesNotExistException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UserChatIDDoesNotExistException.httpStatus);
    }  

    @ExceptionHandler(MessageIDDoesNotBelongToChatIDException.class)
    public ResponseEntity<Object> handleMessageIDDoesNotBelongToChatIDException(MessageIDDoesNotBelongToChatIDException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", MessageIDDoesNotBelongToChatIDException.httpStatus.value());
        responseBody.put("error", MessageIDDoesNotBelongToChatIDException.httpStatus.toString());
        responseBody.put("message", MessageIDDoesNotBelongToChatIDException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, MessageIDDoesNotBelongToChatIDException.httpStatus);
    }

    @ExceptionHandler(UsernameMissingFromRequestBodyException.class)
    public ResponseEntity<Object> handleUsernameMissingFromRequestBodyException(UsernameMissingFromRequestBodyException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", UsernameMissingFromRequestBodyException.httpStatus.value());
        responseBody.put("error", UsernameMissingFromRequestBodyException.httpStatus.toString());
        responseBody.put("message", UsernameMissingFromRequestBodyException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UsernameMissingFromRequestBodyException.httpStatus);
    }

    @ExceptionHandler(UserIDMissingFromRequestURLPathException.class)
    public ResponseEntity<Object> handleUserIDMissingFromRequestURLPathException(UserIDMissingFromRequestURLPathException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", UserIDMissingFromRequestURLPathException.httpStatus.value());
        responseBody.put("error", UserIDMissingFromRequestURLPathException.httpStatus.toString());
        responseBody.put("message", UserIDMissingFromRequestURLPathException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UserIDMissingFromRequestURLPathException.httpStatus);
    }

    @ExceptionHandler(UserIDMissingFromRequestURLParameterException.class)
    public ResponseEntity<Object> handleUserIDMissingFromRequestURLParameterException(UserIDMissingFromRequestURLParameterException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", UserIDMissingFromRequestURLParameterException.httpStatus.value());
        responseBody.put("error", UserIDMissingFromRequestURLParameterException.httpStatus.toString());
        responseBody.put("message", UserIDMissingFromRequestURLParameterException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, UserIDMissingFromRequestURLParameterException.httpStatus);
    }

    @ExceptionHandler(ChatIDMissingFromRequestURLParameterException.class)
    public ResponseEntity<Object> handleChatIDMissingFromRequestURLParameterException(ChatIDMissingFromRequestURLParameterException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", ChatIDMissingFromRequestURLParameterException.httpStatus.value());
        responseBody.put("error", ChatIDMissingFromRequestURLParameterException.httpStatus.toString());
        responseBody.put("message", ChatIDMissingFromRequestURLParameterException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, ChatIDMissingFromRequestURLParameterException.httpStatus);
    }

    @ExceptionHandler(TextContentMissingFromRequestBodyException.class)
    public ResponseEntity<Object> handleTextContentMissingFromRequestBodyException(TextContentMissingFromRequestBodyException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", TextContentMissingFromRequestBodyException.httpStatus.value());
        responseBody.put("error", TextContentMissingFromRequestBodyException.httpStatus.toString());
        responseBody.put("message", TextContentMissingFromRequestBodyException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, TextContentMissingFromRequestBodyException.httpStatus);
    }

    @ExceptionHandler(ContentTypeMissingFromRequestBodyException.class)
    public ResponseEntity<Object> handleContentTypeMissingFromRequestBodyException(ContentTypeMissingFromRequestBodyException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", ContentTypeMissingFromRequestBodyException.httpStatus.value());
        responseBody.put("error", ContentTypeMissingFromRequestBodyException.httpStatus.toString());
        responseBody.put("message", ContentTypeMissingFromRequestBodyException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, ContentTypeMissingFromRequestBodyException.httpStatus);
    }

    @ExceptionHandler(ChatIDMissingFromRequestURLPathException.class)
    public ResponseEntity<Object> handleChatIDMissingFromRequestURLPathException(ChatIDMissingFromRequestURLPathException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", ChatIDMissingFromRequestURLPathException.httpStatus.value());
        responseBody.put("error", ChatIDMissingFromRequestURLPathException.httpStatus.toString());
        responseBody.put("message", ChatIDMissingFromRequestURLPathException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, ChatIDMissingFromRequestURLPathException.httpStatus);
    }

    @ExceptionHandler(MessageIDMissingFromRequestURLParameterException.class)
    public ResponseEntity<Object> handleMessageIDMissingFromRequestURLParameterException(MessageIDMissingFromRequestURLParameterException e) {

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", MessageIDMissingFromRequestURLParameterException.httpStatus.value());
        responseBody.put("error", MessageIDMissingFromRequestURLParameterException.httpStatus.toString());
        responseBody.put("message", MessageIDMissingFromRequestURLParameterException.message);
        responseBody.put("path", e.getPath());

        return new ResponseEntity<Object>(responseBody, MessageIDMissingFromRequestURLParameterException.httpStatus);
    }
} 