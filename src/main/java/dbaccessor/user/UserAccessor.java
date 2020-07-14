package dbaccessor.user;

import com.google.cloud.spanner.Statement;
import com.google.common.collect.ImmutableList;
import controller.ListChats;
import entity.User;
import helper.UniqueIdGenerator;

import java.util.EnumSet;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;

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
public OptionalLong getUserIdFromEmail(String emailId) {
        String sqlStatment = "SELECT UserID FROM User WHERE EmailID=@emailId";
        Statement statement = Statement.newBuilder(sqlStatment)
                .bind("emailId")
                .to(emailId)
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
public EnumSet<User.UniqueFields> checkIfUsernameOrEmailIdExists(String username, String emailId) {
        String sqlStatment = "SELECT Username, EmailID FROM User WHERE Username=@Username UNION DISTINCT SELECT Username, EmailID FROM User WHERE EmailID=@emailId";
        Statement statement = Statement.newBuilder(sqlStatment)
                                .bind("Username")
                                .to(username)
                                .bind("emailId")
                                .to(emailId)
                                .build();
        List<User> resultSet = spannerTemplate.query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true));
        EnumSet<User.UniqueFields> matchingFields = EnumSet.noneOf(User.UniqueFields.class);
        for(User user: resultSet) {
            if(user.getUsername().equals(username)) {
                matchingFields.add(User.UniqueFields.USERNAME);
            }
            if(user.getEmailId().equals(emailId)) {
                matchingFields.add(User.UniqueFields.EMAIL);
            }
        }
        return matchingFields;
    }
        
    /**
     * Checks if a User with the given userId already exists.
     */
    public boolean checkIfUserIdExists(long id) {
        String sqlStatment = "SELECT UserID FROM User WHERE UserID=@userId";
        Statement statement = Statement.newBuilder(sqlStatment)
                                .bind("userId")
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
        String sqlStatment = "SELECT UserID FROM User WHERE Username=@Username";
        Statement statement = Statement.newBuilder(sqlStatment)
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
        String sqlStatment = "SELECT UserID from User@{FORCE_INDEX=UsersByUsernamePassword} WHERE Username=@username AND Password=@password";
        Statement statement = Statement.newBuilder(sqlStatment)
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
        String sqlStatment = "SELECT UserID, Username, EmailID, MobileNo, Picture FROM User@{FORCE_INDEX=UsersByUsernamePassword} WHERE Username=@Username";
        Statement statement = Statement.newBuilder(sqlStatment)
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

        String sqlStatment = "SELECT Username FROM User WHERE Username=@username";
        Statement statement = Statement.newBuilder(sqlStatment).bind("username").to(username).build();
        return !spannerTemplate.query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true)).isEmpty();
    }

    /**
     * Returns details of the User with the given UserId.
     * Details include:
     * <ol>
     * <li> UserId </li>
     * <li> Username </li>
     * <li> Creation Timestamp </li>
     * </ol>
     */
    public User getUser(long userId) {

        String sqlStatment = "SELECT * FROM User WHERE UserID=@userId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("userId").to(userId).build();
        return spannerTemplate.query(User.class, statement, null).get(0);
    }

    /**
     * Returns details of Users with whome the given User is engaged in a Chat with.
     * Details include:
     * <ol>
     * <li> Username </li>
     * <li> ChatId </li>
     * </ol>
     */
    public ImmutableList<ListChats.UsernameMobileNoChatId> getUsernameMobileNoChatIdForSecondUsers(long userId, ImmutableList<Long> listOfChatIdOfUser) {

        String sqlStatment = "SELECT User.Username as Username, User.MobileNo as MobileNo, UserChat.ChatID as ChatID FROM User INNER JOIN UserChat@{FORCE_INDEX=UserChatByChatID} ON User.UserID = UserChat.UserID WHERE UserChat.ChatID IN UNNEST (@listOfChatIdOfUser) AND UserChat.UserID != @userId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("userId").to(userId).bind("listOfChatIdOfUser").toInt64Array(listOfChatIdOfUser).build();
        return ImmutableList.copyOf(spannerTemplate.query(ListChats.UsernameMobileNoChatId.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true)));
    }

    /**
     * Given the userId of a user A and a string mobileNoPrefix, returns all users such that either of the following is true:
     *  1. There is a Chat between user A and this user and the mobile number of this user starts with the string mobileNoPrefix.
     *  2. MobileNo of this user is equal to mobileNoPrefix.
     */
    public ImmutableList<User> getUsersByMobileNumber(long userId, String mobileNoPrefix) {
        String sqlStatement =
                "SELECT UserID, Username, EmailID, MobileNo, Picture " +
                "FROM User " +
                "WHERE MobileNo LIKE @mobileNoPrefix " +
                "AND UserID " +
                "IN (" +
                    "SELECT UserID " +
                    "FROM UserChat@{FORCE_INDEX=UserChatByChatID} " +
                    "WHERE ChatID " +
                        "IN (" +
                            "SELECT ChatID FROM UserChat@{FORCE_INDEX=UserChatByUserID} WHERE UserID=@userId" +
                        ") " +
                    "EXCEPT DISTINCT " +
                    "SELECT @userId" +
                ") " +
                "UNION DISTINCT " +
                "SELECT UserID, Username, EmailID, MobileNo, Picture " +
                "FROM User@{FORCE_INDEX=UsersByMobileNo} " +
                "WHERE MobileNo=@mobileNo";
        Statement statement = Statement
                .newBuilder(sqlStatement)
                .bind("userId")
                .to(userId)
                .bind("mobileNoPrefix")
                .to(mobileNoPrefix + "%")
                .bind("mobileNo")
                .to(mobileNoPrefix)
                .build();
        return ImmutableList.copyOf(spannerTemplate.query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true)));
    }
}
