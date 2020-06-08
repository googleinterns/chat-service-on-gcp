package Controller;

import Entity.User;
import DBAccesser.User.QueryUser;
import Exceptions.UserIDDoesNotExistException;
import Exceptions.UserIDMissingFromRequestURLPathException;
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
    private QueryUser queryUser;

    @GetMapping("/users")
    public void getUserWithoutUserIDPathVariable(HttpServletRequest request) {

        String path = request.getRequestUri();

        throw new UserIDMissingFromRequestURLPathException(path);
    }

    @GetMapping("/users/{userID}")
    public Map<String, Object> getUser(@PathVariable("userID") String userIDString, HttpServletRequest request) {

        String path = request.getRequestUri();
        Map<String, Object> responseBody;

        //check if UserID is valid
        if (queryUser.checkIfUserIDExists(Long.parseLong(userIDString)) == false) {
            throw new UserIDDoesNotExistException(path);
        } 

        User user = queryUser.getUser(Long.parseLong(userIDString));

        return SuccessResponseGenerator.getSuccessResponseForGetUser(user);
    }
}