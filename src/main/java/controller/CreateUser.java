package controller;

import helper.UniqueIdGenerator;
import entity.User;
import dbaccessor.user.UserAccessor;
import helper.SuccessResponseGenerator;
import exceptions.UsernameAlreadyExistsException;
import exceptions.UsernameMissingFromRequestBodyException;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controller which responds to client requests to create (and register) a new user.
 * Sends the UserId of the newly created user in response.
 */
@RestController
public final class CreateUser {

    @Autowired 
    private UserAccessor queryUser;

    @Autowired
    private UserAccessor insertUser;

    @Autowired
    private UniqueIdGenerator uniqueIdGenerator;
    
    /**
     * Creates a new User with the given username.
     * Returns UserId of the new User.
     */
    @PostMapping("/users")
    public Map<String, Object> createUser(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {

        String path = request.getRequestURI();

        if (!requestBody.containsKey("username")) {
            throw new UsernameMissingFromRequestBodyException(path);
        }

        String username  = requestBody.get("username");

        if (queryUser.checkIfUsernameExists(username)) {
            throw new UsernameAlreadyExistsException(path);
        } 

        User newUser = new User(username);

        newUser.setUserId(uniqueIdGenerator.generateUniqueId("User"));
        
        insertUser.insertAll(newUser);

        return SuccessResponseGenerator.getSuccessResponseForCreateEntity("User", newUser.getUserId());
    }
}
