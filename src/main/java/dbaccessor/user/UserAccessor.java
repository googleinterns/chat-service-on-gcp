package dbaccessor.user;

import entity.User;
import controller.ListChats;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

/**
 * Accessor which performs database accesses for the User entity.
 */
@Component
public final class UserAccessor {
    
    @Autowired
    private SpannerTemplate spannerTemplate;

    //Inserts all attributes of the given User in the DB.
    public void insertAll(User user) {
        spannerTemplate.insert(user);
    } 

    //Checks if a User with the given username already exists.
    public boolean checkIfUsernameExists(String username) {

        String SQLStatment = "SELECT Username FROM User WHERE Username=@username";
        Statement statement = Statement.newBuilder(SQLStatment).bind("username").to(username).build();
        List<User> resultSet = spannerTemplate.query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true));
 
        return (!resultSet.isEmpty());
    }

    /**
     * Returns details of the User with the given UserId.
     * Details include:
     * (1)  UserId
     * (2)  Username
     * (3)  Creation Timestamp 
     */
    public User getUser(long userId) {

        String SQLStatment = "SELECT * FROM User WHERE UserID=@userId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userId").to(userId).build();
        List<User> resultSet = spannerTemplate.query(User.class, statement, null);
 
        return resultSet.get(0);
    }

    /**
     * Checks if a User with the given userId already exists.
     */
    public boolean checkIfUserIdExists(long userId) {

        String SQLStatment = "SELECT UserID FROM User WHERE UserID=@userId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userId").to(userId).build();
        List<User> resultSet = spannerTemplate.query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return (!resultSet.isEmpty());
    }

    /**
     * Returns the UserId of the User with the given username.
     */
    public long getUserIdFromUsername(String username) {

        String SQLStatment = "SELECT UserID FROM User WHERE Username=@username";
        Statement statement = Statement.newBuilder(SQLStatment).bind("username").to(username).build();
        List<User> resultSet = spannerTemplate.query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet.get(0).getUserId();
    }

    /**
     * Returns details of Users with whome the given User is engaged in a Chat with.
     * Details include:
     * (1) Username
     * (2) ChatId
     */
    public List<ListChats.UsernameChatId> getUsernameChatIdForSecondUsers(long userId) {

        String SQLStatment = "SELECT User.Username as Username, UserChat.ChatID as ChatID FROM User INNER JOIN UserChat ON User.UserID = UserChat.UserID WHERE UserChat.ChatID IN (SELECT ChatID FROM UserChat WHERE UserID = @userId) AND UserChat.UserID != @userId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userId").to(userId).build();
        List<ListChats.UsernameChatId> resultSet = spannerTemplate.query(ListChats.UsernameChatId.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet;
    }
}
