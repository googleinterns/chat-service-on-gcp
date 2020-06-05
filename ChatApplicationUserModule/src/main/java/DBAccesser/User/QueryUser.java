package DBAccesser.User;

import Entity.Class.User;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;

import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

import java.util.List;

@Component
public class QueryUser {
    
    @Autowired
    SpannerTemplate spannerTemplate;

    public boolean checkIfUserExists(String username, String emailID) {

        String SQLStatment = "SELECT Username FROM User WHERE Username=@Username or EmailID=@EmailID";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("Username")
                                .to(username)
                                .bind("EmailID")
                                .to(emailID)
                                .build();
        List<User> resultSet = spannerTemplate.query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true)); //setAllowPartialRead for reading specific columns
 
        return (resultSet.size() > 0);
    }

    public boolean checkIfUserIDExists(long userID) {

        String SQLStatment = "SELECT UserID FROM User WHERE UserID=@userID";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("UserID")
                                .to(userID)
                                .build();
        List<User> resultSet = spannerTemplate.query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return (resultSet.size() > 0);
    }

    public long getUserIDFromUsername(String username) {

        String SQLStatment = "SELECT userID FROM User WHERE Username=@Username";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("Username")
                                .to(username)
                                .build();
        List<User> resultSet = spannerTemplate.query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet.get(0).getUserID();
    }
}