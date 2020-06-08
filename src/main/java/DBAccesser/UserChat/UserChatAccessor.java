package DBAccesser.UserChat;

import Entity.UserChat;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

@Component
public class UserChatAccessor {
    
    @Autowired
    private SpannerTemplate spannerTemplate;

    public void insertAll(UserChat userChat) {
        spannerTemplate.insert(userChat);
    }
    
    public List<UserChat> getChatIDIfChatExistsBetweenUserIDs(long userID1, long userID2) {

        String SQLStatment = "SELECT ChatID FROM UserChat WHERE UserID=@userID2 AND ChatID IN (SELECT ChatID FROM UserChat WHERE UserID=@userID1)";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userID2").to(userID2).bind("userID1").to(userID1).build();
        List<UserChat> resultSet = spannerTemplate.query(UserChat.class, statement, new SpannerQueryOptions().setAllowPartialRead(true));
    
        return resultSet;
    }

    public boolean checkIfUserChatIDExists(long userID, long chatID) {

        String SQLStatment = "SELECT * FROM UserChat WHERE UserID=@userID AND ChatID=@chatID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userID").to(userID).bind("chatID").to(chatID).build();
        List<UserChat> resultSet = spannerTemplate.query(UserChat.class, statement, null);
    
        return (!resultSet.isEmpty());
    }
}
