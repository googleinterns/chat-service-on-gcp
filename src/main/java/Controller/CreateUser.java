package Controller;

import Helper.Helper;
import Entity.User;
import DBAccesser.User.QueryUser;
import DBAccesser.User.InsertUser;
import Helper.SuccessResponseGenerator;
import Exceptions.UsernameAlreadyExistsException;
import Exceptions.UserIDDoesNotExistException;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

//this annotation tells that this class can contain methods which map to URL requests
@RestController
public class CreateUser {

    @Autowired 
    QueryUser queryUserHelper;

    @Autowired
    InsertUser insertUserHelper;

    @Autowired
    Helper helper;

    //this annotation tells that when a URL POST request of the given form comes to the server, the following method should be called
    
    @PostMapping("/users")
    //if we use @Request Body String username, the body of the POST request needs to contain the string username i.e. say "simran" as Text 
    public Map<String, String> createUser(@RequestBody Map<String, String> requestBody) {

        String path = "/users";

        //check if username exists - return error if it does
        if (queryUserHelper.checkIfUsernameExists(requestBody.get("username"))) {
            throw new UsernameAlreadyExistsException(path);
        } 

        User newUser = new User(requestBody.get("username"));
        //generate unique userID
        newUser.setUserID(helper.generateUniqueID("User", false, false));
        //insert new entry into User
        insertUserHelper.insertAll(newUser);

        return SuccessResponseGenerator.getSuccessResponseForCreateEntity();
    }
}
