package Controller;

import Entity.Class.User;
import DBAccesser.User.QueryUser;
import DBAccesser.User.InsertUser;
import Helper.Helper;
import Exceptions.RequiredFieldMissingException;
import Exceptions.UserAlreadyExistsException;
import Exceptions.APIException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

//this annotation tells that this class can contain methods which map to URL requests
@RestController
public class UserAPI {

    @Autowired 
    QueryUser queryUserHelper;

    @Autowired
    InsertUser insertUserHelper;

    @Autowired
    Helper helper;

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return new ResponseEntity<Object>(helper.getResponseBody(e), e.getHttpStatus());
    }  
     
    @ExceptionHandler(RequiredFieldMissingException.class)
    public ResponseEntity<Object> handleRequiredFieldMissingException(RequiredFieldMissingException e) {
        return new ResponseEntity<Object>(helper.getResponseBody(e), e.getHttpStatus());
    }

    @PostMapping(value = "/signup", consumes={"application/json"})
    public String signup(@RequestBody HashMap<String, Object> data) {

        String path = "/signup";
        String[] required = {"Username", "EmailID", "Password", "MobileNo"};
        List<String> missing_fields = helper.missingFields(data, required);
        if( missing_fields.size() > 0 ) {
            throw new RequiredFieldMissingException(path, missing_fields);
        }

        // necessary fields
        String username = data.get("Username").toString();
        String emailID = data.get("EmailID").toString();
        String password = data.get("Password").toString();
        String mobileNo = data.get("MobileNo").toString();

        // optional field
        String base64Image = "";
        if(data.get("Picture") != null){
            base64Image = data.get("Picture").toString();
        }

        // check if Username or email exists - return error if it does
        if (queryUserHelper.checkIfUserExists(username, emailID)) {
            throw new UserAlreadyExistsException(path);
        } else {
            //generate unique userID
            long id = helper.generateUniqueID("User", false, false);
            User newUser = new User(id, username, password, emailID, mobileNo, base64Image);
            //insert new entry into User
            insertUserHelper.insertAll(newUser);
            return Long.toString(newUser.getUserID());
        }
    }
}
