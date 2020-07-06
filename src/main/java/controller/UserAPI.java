package controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import entity.User;
import dbaccessor.user.UserAccessor;
import exceptions.*;
import googlesignin.GoogleAuthenticator;
import googlesignin.GoogleUser;
import helper.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

@RestController
public class UserAPI {

    @Autowired 
    private UserAccessor userAccessor;

    @Autowired
    private RequestValidator requestValidator;

    @Autowired
    private GoogleAuthenticator googleAuthenticator;

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
        requestValidator.signupRequestValidator(data.keySet(), path);
        // necessary fields
        String username = data.get("Username").toString();
        String emailId = data.get("EmailID").toString();
        String password = data.get("Password").toString();
        String mobileNo = data.get("MobileNo").toString();

        // optional field
        String base64Image = "";
        if(data.get("Picture") != null){
            base64Image = data.get("Picture").toString();
        }

        // Checks if Username or email exists - throws exception if it does
        EnumSet<User.UniqueFields> usedFields = userAccessor.checkIfUsernameOrEmailIdExists(username, emailId);
        if (!usedFields.isEmpty()) {
            throw new UserAlreadyExistsException(path, usedFields);
        }
        User newUser = User.newBuilder()
                .username(username)
                .password(password)
                .emailId(emailId)
                .mobileNo(mobileNo)
                .picture(base64Image)
                .build();
        // Inserts new entry into User table
        long id = userAccessor.insert(newUser);
        return SuccessResponseGenerator.getSuccessResponseForCreateEntity("User", id);
    }

    @PostMapping(value = "/login", consumes={"application/json"})
    public Map<String, Object> login(@RequestBody Map<String, Object> data, HttpServletRequest request) {
        String path = request.getRequestURI();
        requestValidator.loginRequestValidator(data.keySet(), path);
        String username = data.get("Username").toString();
        String password = data.get("Password").toString();
        long id = userAccessor.login(username, User.hashPassword(password));
        if(id == -1) {
            throw new InvalidLoginException(path);
        }
        return SuccessResponseGenerator.getSuccessResponseForLogin(id);
    }

    @GetMapping("/viewUser")
    public ImmutableMap<String, Object> viewUser(@RequestParam(value = "username", required = true) String username, HttpServletRequest request) {
        String path = request.getRequestURI();
        User user = userAccessor.getUser(username);
        if(user == null) {
            throw new UserNotFoundException(path);
        }
        return SuccessResponseGenerator.getSuccessResponseForViewUser(user);
    }

    @GetMapping("/googleSignIn")
    public long googleSignIn(@RequestParam(value = "authCode", required = true) String authCode) throws IOException {
        GoogleUser googleUser = googleAuthenticator.getGoogleUser(authCode);
        return googleUser.getOrCreateUser();
    }

    @GetMapping("/getUsersByMobileNumber/{userId}")
    ImmutableMap<String, ImmutableList<ImmutableMap<String, Object>>> getUsersByMobileNumber(
            @PathVariable("userId") String userIdString,
            @RequestParam(value = "mobileNoPrefix", required = true) String mobileNoPrefix,
            HttpServletRequest request) {
        String path = request.getRequestURI();
        long userId = Long.parseLong(userIdString);
        if (!userAccessor.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        }
        ImmutableList<User> users = userAccessor.getUsersByMobileNumber(userId, mobileNoPrefix);
        return SuccessResponseGenerator.getSuccessResponseForGetUsersByMobileNumber(users);
    }
}
