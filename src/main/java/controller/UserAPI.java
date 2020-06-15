package controller;

import entity.User;
import dbaccessor.user.UserAccessor;
import helper.*;
import exceptions.UserRequiredFieldMissingException;
import exceptions.UserAlreadyExistsException;
import exceptions.InvalidLoginException;
import exceptions.UserNotFoundException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserAPI {

    @Autowired 
    private UserAccessor userAccessor;

    @Autowired
    private UniqueIDGenerator uniqueIDGenerator;

    @Autowired
    private RequestValidator requestValidator;

    @Autowired
    private ErrorResponseBody errorResponseBody;

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return new ResponseEntity<Object>(errorResponseBody.getResponseBody(e), e.getHttpStatus());
    }  
     
    @ExceptionHandler(UserRequiredFieldMissingException.class)
    public ResponseEntity<Object> handleRequiredFieldMissingException(UserRequiredFieldMissingException e) {
        return new ResponseEntity<Object>(errorResponseBody.getResponseBody(e), e.getHttpStatus());
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<Object> handleInvalidLoginException(InvalidLoginException e) {
        return new ResponseEntity<Object>(errorResponseBody.getResponseBody(e), e.getHttpStatus());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<Object>(errorResponseBody.getResponseBody(e), e.getHttpStatus());
    }

    @PostMapping(value = "/signup", consumes={"application/json"})
    public Map<String, Object> signup(@RequestBody Map<String, Object> data, HttpServletRequest request) {

        String path = request.getRequestURI();
        requestValidator.signupRequestValidator(data, path);
        /* necessary fields */
        String username = data.get("Username").toString();
        String emailID = data.get("EmailID").toString();
        String password = data.get("Password").toString();
        String mobileNo = data.get("MobileNo").toString();

        /* optional field */
        String base64Image = "";
        if(data.get("Picture") != null){
            base64Image = data.get("Picture").toString();
        }

        /* Checks if Username or email exists - throws exception if it does */
        if (userAccessor.checkIfUserExists(username, emailID)) {
            throw new UserAlreadyExistsException(path);
        }
        /* Generates unique userID */
        long id = uniqueIDGenerator.generateUniqueID("User");
        User newUser = new User(id, username, password, emailID, mobileNo, base64Image);
        /* Inserts new entry into User table */
        userAccessor.insert(newUser);
        return SuccessResponseGenerator.getSuccessResponseForCreateEntity("User", id);
    }

    @PostMapping(value = "/login", consumes={"application/json"})
    public Map<String, Object> login(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        String path = request.getRequestURI();
        requestValidator.loginRequestValidator(data, path);
        String username = data.get("Username").toString();
        String password = data.get("Password").toString();
        long id = userAccessor.login(username, password);
        if(id == -1) {
            throw new InvalidLoginException(path);
        }
        return SuccessResponseGenerator.getSuccessResponseForLogin(id);
    }

    @GetMapping("/viewUser")
    public Map<String, Object> viewUser(@RequestParam(value = "username", required = true) String username, HttpServletRequest request) {
        String path = request.getRequestURI();
        User user = userAccessor.getUser(username);
        if(user == null) {
            throw new UserNotFoundException(path);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("UserID", user.getUserID());
        response.put("Username", username);
        response.put("EmailID", user.getEmailID());
        response.put("MobileNo", user.getMobileNumber());
        if(user.getPicture() != null) {
            response.put("Picture", user.getPicture());
        }
        return response;
    }
}
