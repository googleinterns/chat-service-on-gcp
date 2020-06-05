package Controller;

import Entity.Class.User;
import DBAccesser.User.QueryUser;
import DBAccesser.User.InsertUser;
import Helper.Helper;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;

//this annotation tells that this class can contain methods which map to URL requests
@RestController
public class UserAPI {

    @Autowired 
    QueryUser queryUserHelper;

    @Autowired
    InsertUser insertUserHelper;

    @Autowired
    Helper helper;

    @PostMapping(value = "/signup", consumes={"application/json"})
    public String signup(@RequestBody HashMap<String, Object> data) {

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
            return "Username or Email-ID already exists";
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
