package controller;

import entity.User;
import dbaccessor.user.UserAccessor;
import exceptions.UserIdDoesNotExistException;
import exceptions.UserIdMissingFromRequestURLPathException;
import helper.SuccessResponseGenerator;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller which responds to client requests to get the details of the user itself.
 * The response contains:
 * (1)  UserId
 * (2)  Username
 * (3)  CreationTs
 */
@RestController
public final class GetUser {

    @Autowired 
    private UserAccessor queryUser;

    /**
     * Responds to requests with missing userId URL Path Variable.
     * Throws an exception for the same. 
     */
    @GetMapping("/users")
    public void getUserWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    /**
     * Responds to complete requests.
     * Returns details of the requested User.
     */
    @GetMapping("/users/{userId}")
    public Map<String, Object> getUser(@PathVariable("userId") String userIdString, HttpServletRequest request) {

        String path = request.getRequestURI();
        Map<String, Object> responseBody;

        long userId = Long.parseLong(userIdString);

        /*
         * Checks if the passed userId is valid
         */
        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        } 

        User user = queryUser.getUser(userId);

        return SuccessResponseGenerator.getSuccessResponseForGetUser(user);
    }
}
