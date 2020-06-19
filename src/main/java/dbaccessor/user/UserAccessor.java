package dbaccessor.user;

import com.google.cloud.spanner.Statement;
import com.google.common.collect.ImmutableList;
import controller.ListChats;
import entity.User;
import helper.UniqueIdGenerator;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import org.springframework.stereotype.Component;

import java.util.OptionalLong;

/**
 * Accessor which performs database accesses for the User entity.
 */
@Component
public class UserAccessor {
    
    @Autowired
    SpannerTemplate spannerTemplate;

    @Autowired
    private UniqueIdGenerator uniqueIDGenerator;

    /*
     * Inserts all attributes of the given User in the DB.
     */ 
    public long insert(User user) {
        long id = uniqueIDGenerator.generateUniqueId("User");
        user.setUserId(id);
        spannerTemplate.insert(user);
        return id;
    }

    /*
     * Inserts all attributes of the given User in the DB.
     * The userId of the User is present within the User object.
     */ 
    public void insertGivenUserId(User user) {
        spannerTemplate.insert(user);
    }
  
  /** Gets the UserID of the user having the given email-id */
    public OptionalLong getUserIdFromEmail(String emailID) {
        String SQLStatment = "SELECT UserID FROM User WHERE EmailID=@EmailID";
        Statement statement = Statement.newBuilder(SQLStatment)
                .bind("EmailID")
                .to(emailID)
                .build();
        List<User> resultSet = spannerTemplate.query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true));
        if(resultSet.isEmpty()) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(resultSet.get(0).getUserId());
    }

    /**
     * Checks if there exists users having given username or email-id
     * If no such user exists, returns empty set
     * Otherwise returns EnumSet of matching fields
     */
    public EnumSet<User.UniqueFields> checkIfUsernameOrEmailIdExists(String username, String emailID) {
        String SQLStatment = "SELECT Username, EmailID FROM User WHERE Username=@Username OR EmailID=@EmailID";
        Statement statement = Statement.newBuilder(SQLStatment)
                                .bind("Username")
                                .to(username)
                                .bind("EmailID")
                                .to(emailID)
                                .build();
        List<User> resultSet = spannerTemplate.query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true));
        EnumSet<User.UniqueFields> matchingFields = EnumSet.noneOf(User.UniqueFields.class);
        for(User user: resultSet) {
            if(user.getUsername().equals(username)) {
                matchingFields.add(User.UniqueFields.USERNAME);
            }
            if(user.getEmailID().equals(emailID)) {
                matchingFields.add(User.UniqueFields.EMAIL);
            }
        }
        return matchingFields;
    }
        
    /**
     * Checks if a User with the given userId already exists.
     */
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

    /**
     * Returns the UserId of the User with the given username.
     */
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

    /**
     * Retrieves the UserID of the user having the given username and password
     * If no such user exists, returns -1
     */
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

    /**
     * Retrieves the user having the given username
     * If no such user exists, returns null
     */
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

    /**
     * Checks if a User with the given username already exists.
     */
    public boolean checkIfUsernameExists(String username) {

        String SQLStatment = "SELECT Username FROM User WHERE Username=@username";
        Statement statement = Statement.newBuilder(SQLStatment).bind("username").to(username).build();
        return !spannerTemplate.query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true)).isEmpty();
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
        return spannerTemplate.query(User.class, statement, null).get(0);
    }

    /**
     * Returns details of Users with whome the given User is engaged in a Chat with.
     * Details include:
     * (1) Username
     * (2) ChatId
     */
    public ImmutableList<ListChats.UsernameChatId> getUsernameChatIdForSecondUsers(long userId) {

        String SQLStatment = "SELECT User.Username as Username, UserChat.ChatID as ChatID FROM User INNER JOIN UserChat ON User.UserID = UserChat.UserID WHERE UserChat.ChatID IN (SELECT ChatID FROM UserChat WHERE UserID = @userId) AND UserChat.UserID != @userId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userId").to(userId).build();
        return ImmutableList.copyOf(spannerTemplate.query(ListChats.UsernameChatId.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true)));
    }
}
