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

    public void insert(User user) {
        spannerTemplate.insert(user);
    }

    public boolean checkIfUserExists(String username, String emailID) {
        String SQLStatment = "SELECT Username FROM User WHERE Username=@Username OR EmailID=@EmailID";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("Username")
                                .to(username)
                                .bind("EmailID")
                                .to(emailID)
                                .build();
        List<User> resultSet = spannerTemplate.query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true)); //setAllowPartialRead for reading specific columns
 
        return !resultSet.isEmpty();
    }

    public boolean checkIfUserIDExists(long userID) {
        String SQLStatment = "SELECT UserID FROM User WHERE UserID=@userID";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("UserID")
                                .to(userID)
                                .build();
        List<User> resultSet = spannerTemplate.query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return !resultSet.isEmpty();
    }

    public long getUserIDFromUsername(String username) {
        String SQLStatment = "SELECT UserID FROM User WHERE Username=@Username";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("Username")
                                .to(username)
                                .build();
        List<User> resultSet = spannerTemplate.query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet.get(0).getUserID();
    }

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
