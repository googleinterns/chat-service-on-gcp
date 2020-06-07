package Controller;

import Entity.User;
import DBAccesser.User.QueryUser;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.UserIDMissingFromRequestURLParameterException;
import Helper.SuccessResponseGenerator;

import java.util.Map;
import java.util.LinkedHashMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

//this annotation tells that this class can contain methods which map to URL requests
@RestController
public class GetUser {

    @Autowired 
    QueryUser queryUserHelper;

    @GetMapping("/users")
    // the URL should be of the form : /users/?userID=5
    public Map<String, Object> getUser(@RequestParam(value = "userID", required = false) String userIDString) {

        String path = "/users/?userID="+userIDString;
        Map<String, Object> responseBody;

        //check if URL parameter has been provided
        if (userIDString == null) {
            throw new UserIDMissingFromRequestURLParameterException(path);
        }
        
        //check if UserID is valid
        if (queryUserHelper.checkIfUserIDExists(Long.parseLong(userIDString)) == false) {
            throw new UserIDDoesNotExistException(path);
        } 

        User user = queryUserHelper.getUser(Long.parseLong(userIDString));

        return SuccessResponseGenerator.getSuccessResponseForGetUser(user);
    }
}