package Controller;

import Helper.UniqueIDGenerator;
import Entity.User;
import DBAccesser.User.UserAccessor;
import Helper.SuccessResponseGenerator;
import Exceptions.UsernameAlreadyExistsException;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.UsernameMissingFromRequestBodyException;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;  
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

//this annotation tells that this class can contain methods which map to URL requests
@RestController
public final class CreateUser {

    @Autowired 
    private UserAccessor queryUser;

    @Autowired
    private UserAccessor insertUser;

    @Autowired
    private UniqueIDGenerator uniqueIDGenerator;
    
    @PostMapping("/users")
    public Map<String, Object> createUser(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {

        String path = request.getRequestURI();

        //check if request body is as required
        if (!requestBody.containsKey("username")) {
            throw new UsernameMissingFromRequestBodyException(path);
        }

        String username  = requestBody.get("username");

        //check if username exists - return error if it does
        if (queryUser.checkIfUsernameExists(username)) {
            throw new UsernameAlreadyExistsException(path);
        } 

        User newUser = new User(username);
        //generate unique userID
        newUser.setUserID(uniqueIDGenerator.generateUniqueID("User", false, false));
        //insert new entry into User
        insertUser.insertAll(newUser);

        return SuccessResponseGenerator.getSuccessResponseForCreateEntity("User", newUser.getUserID());
    }
}
