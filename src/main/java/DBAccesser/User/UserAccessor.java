package DBAccesser.User;

import Entity.User;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;

import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Component
public class UserAccessor {
    
    @Autowired
    SpannerTemplate spannerTemplate;

    // Insert new entry in User Table
    public void insert(User user) {
        spannerTemplate.insert(user);
    }

    // Check if a user with the given username or email-id already exists in User table
    public boolean checkIfUserExists(String username, String emailID) {
        String SQLStatment = "SELECT Username FROM User WHERE Username=@Username OR EmailID=@EmailID";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("Username")
                                .to(username)
                                .bind("EmailID")
                                .to(emailID)
                                .build();
        return !spannerTemplate
                .query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true)) //setAllowPartialRead for reading specific columns
                .isEmpty();
    }

    // Check if there is a row in the User table having the given UserID
    public boolean checkIfUserIDExists(long id) {
        String SQLStatment = "SELECT UserID FROM User WHERE UserID=@userID";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("UserID")
                                .to(id)
                                .build();
        return !spannerTemplate
                .query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true))
                .isEmpty();
    }

    // Retrieve UserID of user having the given username
    public long getUserIDFromUsername(String username) {
        String SQLStatment = "SELECT UserID FROM User WHERE Username=@Username";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("Username")
                                .to(username)
                                .build();
        return spannerTemplate
                .query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true))
                .get(0).getUserID();
    }

    // Retrieve the UserID of the user having the given username and password
    // If no such user exists, return -1
    public long login(String username, String password) {
        String SQLStatment = "SELECT UserID from User WHERE Username=@username AND Password=@password";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("username")
                                .to(username)
                                .bind("password")
                                .to(password)
                                .build();
        List<User> resultSet = spannerTemplate.query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
        if(resultSet.isEmpty()) {
            return -1;
        }
        return resultSet.get(0).getUserID();
    }

    // Retrieve UserID, Username, EmailID, MobileNo and Picture of the user having the given username
    // If no such user exists, return just the UserID as -1
    public Map<String, Object> getUser(String username) {
        String SQLStatment = "SELECT UserID, Username, EmailID, MobileNo, Picture FROM User WHERE Username=@Username";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("Username")
                                .to(username)
                                .build();
        List<User> resultSet = spannerTemplate.query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
        Map<String, Object> response = new HashMap<String, Object>();
        if(resultSet.isEmpty()) {
            response.put("UserID", -1);
            return response;
        }
        User user = resultSet.get(0);
        response.put("UserID", user.getUserID());
        response.put("Username", username);
        response.put("EmailID", user.getEmailID());
        response.put("MobileNo", user.getMobileNumber());
        if(user.getPicture() != null) {
            response.put("Picture", user.getPicture());
        }
        return response;
    }
}
