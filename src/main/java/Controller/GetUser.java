package Controller;

import Entity.User;
import DBAccesser.User.UserAccessor;
import Exceptions.UserIdDoesNotExistException;
import Exceptions.UserIdMissingFromRequestURLPathException;
import Helper.SuccessResponseGenerator;

import java.util.Map;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;  
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

//this annotation tells that this class can contain methods which map to URL requests
@RestController
public final class GetUser {

    @Autowired 
    private UserAccessor queryUser;

    @GetMapping("/users")
    public void getUserWithoutUserIdPathVariable(HttpServletRequest request) {

        String path = request.getRequestURI();

        throw new UserIdMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userId}")
    public Map<String, Object> getUser(@PathVariable("userId") String userIdString, HttpServletRequest request) {

        String path = request.getRequestURI();
        Map<String, Object> responseBody;

        long userId = Long.parseLong(userIdString);

        //check if UserId is valid
        if (!queryUser.checkIfUserIdExists(userId)) {
            throw new UserIdDoesNotExistException(path);
        } 

        User user = queryUser.getUser(userId);

        return SuccessResponseGenerator.getSuccessResponseForGetUser(user);
    }
}
