package Controller;

import Helper.Helper;
import Entity.Class.User;
import DBAccesser.User.QueryUser;
import DBAccesser.User.InsertUser;
import Exceptions.UsernameAlreadyExistsException;
import Exceptions.UserIDDoesNotExistException;

import java.util.Map;
import java.time.LocalDateTime;
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

    //this annotation tells that when a URL GET request of the given form comes to the server, the following method should be called
    
    @PostMapping("/users")
    //if we use @Request Body String username, the body of the POST request needs to contain the string username i.e. say "simran" as Text 
    public Map<String, String> createUser(@RequestBody Map<String, String> requestBody) {

        String path = "/users";
        
        Map<String, String> responseBody;
        
        //check if username exists - return error if it does
        if (queryUserHelper.checkIfUsernameExists(requestBody.get("username"))) {
            throw new UsernameAlreadyExistsException(path);
        } else {
            User newUser = new User(requestBody.get("username"));
            //generate unique userID
            newUser.setUserID(helper.generateUniqueID("User", false, false));
            //insert new entry into User
            insertUserHelper.insertAll(newUser);

            responseBody = new LinkedHashMap<String, String>();
            responseBody.put("message", "Created");

            return responseBody;
        }
        
        /*
        //test without Spanner
        if (requestBody.get("username").equals("simran")) {
            throw new UsernameAlreadyExistsException(path);
        }
        User newUser = new User(requestBody.get("username"));

        Map<String, String> responseBody = new LinkedHashMap<String, String>();
        responseBody.put("message", "Created");
        responseBody.put("responseUsername", newUser.getUsername());

        return responseBody;
        */
    }

    @GetMapping("/users")
    // the URL should be of the form : /users/?userID=5
    public Map<String, Object> getUser(@RequestParam("userID") String userIDString) {

        String path = "/users/?userID="+userIDString;
        Map<String, Object> responseBody;
        
        if (queryUserHelper.checkIfUserIDExists(Long.parseLong(userIDString)) == false) {
            throw new UserIDDoesNotExistException(path);
        } else {
            User user = queryUserHelper.getUser(Long.parseLong(userIDString));
            responseBody = new LinkedHashMap<String, Object>();
            responseBody.put("UserID", user.getUserID());
            responseBody.put("Username", user.getUsername());
            responseBody.put("CreationTS", user.getCreationTS());
        }

        return responseBody;
        
        /*
        //test without Spanner
        if (Long.parseLong(userIDString) == 5) {
            throw new UserIDDoesNotExistException(path);
        } else {
            User user = new User(Long.parseLong(userIDString), "simran");
            responseBody = new LinkedHashMap<String, Object>();
            responseBody.put("UserID", user.getUserID());
            responseBody.put("Username", user.getUsername());
        }
        
        return responseBody;
        */
    }
}
