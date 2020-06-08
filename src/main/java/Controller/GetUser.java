package Controller;

import Entity.User;
import DBAccesser.User.QueryUser;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.UserIDMissingFromRequestURLPathException;
import Helper.SuccessResponseGenerator;

import java.util.Map;
import java.util.LinkedHashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

//this annotation tells that this class can contain methods which map to URL requests
@RestController
public class GetUser {

    @Autowired 
    QueryUser queryUserHelper;

    @GetMapping("/users")
    public void getUserWithoutUserIDPathVariable() {

        String path = "/users";

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userID}")
    public Map<String, Object> getUser(@PathVariable("userID") String userIDString) {

        String path = "/users/"+userIDString;
        Map<String, Object> responseBody;

        //check if UserID is valid
        if (queryUserHelper.checkIfUserIDExists(Long.parseLong(userIDString)) == false) {
            throw new UserIDDoesNotExistException(path);
        } 

        User user = queryUserHelper.getUser(Long.parseLong(userIDString));

        return SuccessResponseGenerator.getSuccessResponseForGetUser(user);
    }
}