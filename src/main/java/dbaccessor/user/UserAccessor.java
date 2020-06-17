package dbaccessor.user;

import entity.User;
import controller.ListChats;

import helper.UniqueIdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

import java.util.List;
import java.util.Optional;

@Component
public class UserAccessor {
    
    @Autowired
    SpannerTemplate spannerTemplate;

    @Autowired
    private UniqueIdGenerator uniqueIDGenerator;

    /* Inserts new entry in User Table */
    public long insert(User user) {
        long id = uniqueIDGenerator.generateUniqueId("User");
        user.setUserId(id);
        spannerTemplate.insert(user);
        return id;
    }

    /** Gets the UserID of the user having the given username and email-id */
    public Optional<Long> getUserIdFromUsernameAndEmail(String username, String emailID) {
        String SQLStatment = "SELECT Username FROM User WHERE Username=@Username AND EmailID=@EmailID";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("Username")
                                .to(username)
                                .bind("EmailID")
                                .to(emailID)
                                .build();
        List<User> resultSet = spannerTemplate.query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true));
        if(resultSet.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(resultSet.get(0).getUserId());
    }

    /** Checks if a user with the given username or email-id already exists in User table */
    public boolean checkIfUserExists(String username, String emailID) {
        String SQLStatment = "SELECT Username FROM User WHERE Username=@Username OR EmailID=@EmailID";
        Statement statement = Statement.newBuilder(SQLStatment)
                .bind("Username")
                .to(username)
                .bind("EmailID")
                .to(emailID)
                .build();
        return !spannerTemplate
                .query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true))
                .isEmpty();
    }

    /* Checks if there is a row in the User table having the given UserID */
    public boolean checkIfUserIdExists(long id) {
        String SQLStatment = "SELECT UserID FROM User WHERE UserID=@userID";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("UserID")
                                .to(id)
                                .build();
        return !spannerTemplate
                .query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true))
                .isEmpty();
    }

    /* Retrieves UserID of user having the given username */
    public long getUserIdFromUsername(String username) {
        String SQLStatment = "SELECT UserID FROM User WHERE Username=@Username";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("Username")
                                .to(username)
                                .build();
        return spannerTemplate
                .query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true))
                .get(0).getUserId();
    }

    /* Retrieves the UserID of the user having the given username and password
        If no such user exists, returns -1 */
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
        return resultSet.get(0).getUserId();
    }

    /* Retrieves UserID, Username, EmailID, MobileNo and Picture of the user having the given username
        If no such user exists, returns just the UserID as -1 */
    public User getUser(String username) {
        String SQLStatment = "SELECT UserID, Username, EmailID, MobileNo, Picture FROM User WHERE Username=@Username";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("Username")
                                .to(username)
                                .build();
        List<User> resultSet = spannerTemplate.query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
        if(resultSet.isEmpty()) {
            return null;
        }
        return resultSet.get(0);
      
    }
    
    public void insertAll(User user) {
        spannerTemplate.insert(user);
    } 

    public boolean checkIfUsernameExists(String username) {

        String SQLStatment = "SELECT Username FROM User WHERE Username=@username";
        Statement statement = Statement.newBuilder(SQLStatment).bind("username").to(username).build();
        List<User> resultSet = spannerTemplate.query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true));
 
        return (!resultSet.isEmpty());
    }

    public User getUser(long userId) {

        String SQLStatment = "SELECT * FROM User WHERE UserID=@userId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userId").to(userId).build();
        List<User> resultSet = spannerTemplate.query(User.class, statement, null);
 
        return resultSet.get(0);
    }

    public List<ListChats.UsernameChatId> getUsernameChatIdForSecondUsers(long userId) {

        String SQLStatment = "SELECT User.Username as Username, UserChat.ChatID as ChatID FROM User INNER JOIN UserChat ON User.UserID = UserChat.UserID WHERE UserChat.ChatID IN (SELECT ChatID FROM UserChat WHERE UserID = @userId) AND UserChat.UserID != @userId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userId").to(userId).build();
        List<ListChats.UsernameChatId> resultSet = spannerTemplate.query(ListChats.UsernameChatId.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet;
    }
}
