package DBAccesser.User;

import Entity.User;
import Entity.Chat;
import Controller.ListChats;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

@Component
public final class UserAccessor {
    
    @Autowired
    private SpannerTemplate spannerTemplate;

    public void insertAll(User user) {
        spannerTemplate.insert(user);
    } 

    public boolean checkIfUsernameExists(String username) {

        String SQLStatment = "SELECT Username FROM User WHERE Username=@username";
        Statement statement = Statement.newBuilder(SQLStatment).bind("username").to(username).build();
        List<User> resultSet = spannerTemplate.query(User.class, statement, new SpannerQueryOptions().setAllowPartialRead(true));
 
        return (!resultSet.isEmpty());
    }

    public User getUser(long userID) {

        String SQLStatment = "SELECT * FROM User WHERE UserID=@userID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userID").to(userID).build();
        List<User> resultSet = spannerTemplate.query(User.class, statement, null);
 
        return resultSet.get(0);
    }

    public boolean checkIfUserIDExists(long userID) {

        String SQLStatment = "SELECT UserID FROM User WHERE UserID=@userID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userID").to(userID).build();
        List<User> resultSet = spannerTemplate.query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return (!resultSet.isEmpty());
    }

    public long getUserIDFromUsername(String username) {

        String SQLStatment = "SELECT UserID FROM User WHERE Username=@username";
        Statement statement = Statement.newBuilder(SQLStatment).bind("username").to(username).build();
        List<User> resultSet = spannerTemplate.query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet.get(0).getUserID();
    }

    // public List<User> getSecondUserForChat(User firstUser, Chat chat) {

    //     String SQLStatment = "SELECT * FROM User WHERE UserID IN (SELECT UserID FROM UserChat WHERE ChatID=@chatID AND UserID!=@userID)";
    //     Statement statement = Statement.newBuilder(SQLStatment).bind("chatID").to(chat.getChatID()).bind("userID").to(firstUser.getUserID()).build();
    //     List<User> resultSet = spannerTemplate.query(User.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
    //     return resultSet;
    // }

    public List<ListChats.UsernameChatID> getUsernameChatIDForSecondUsers(long userID) {

        String SQLStatment = "SELECT User.Username as Username, UserChat.ChatID as ChatID FROM User INNER JOIN UserChat ON User.UserID = UserChat.UserID WHERE UserChat.ChatID IN (SELECT ChatID FROM UserChat WHERE UserID = @userID) AND UserChat.UserID != @userID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userID").to(userID).build();
        List<ListChats.UsernameChatID> resultSet = spannerTemplate.query(ListChats.UsernameChatID.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet;
    }
}
