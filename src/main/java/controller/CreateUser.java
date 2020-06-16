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

@RestController
public final class CreateUser {

    @Autowired 
    private UserAccessor queryUser;

    @Autowired
    private UserAccessor insertUser;

    @Autowired
    private UniqueIdGenerator uniqueIdGenerator;
    
    @PostMapping("/users")
    public Map<String, Object> createUser(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {

        String path = request.getRequestURI();

        /*
         * Checks if request body is as required
         */
        if (!requestBody.containsKey("username")) {
            throw new UsernameMissingFromRequestBodyException(path);
        }

        String username  = requestBody.get("username");

        /*
         * Checks if username exists
         */
        if (queryUser.checkIfUsernameExists(username)) {
            throw new UsernameAlreadyExistsException(path);
        } 

        User newUser = new User(username);

        /*
         * Generates unique userId
         */
        newUser.setUserId(uniqueIdGenerator.generateUniqueId("User"));
        
        /*
         * Inserts new entry into User
         */
        insertUser.insertAll(newUser);

        return SuccessResponseGenerator.getSuccessResponseForCreateEntity("User", newUser.getUserId());
    }
}
