package dbaccessor.userchat;

import entity.UserChat;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

@Component
public final class UserChatAccessor {
    
    @Autowired
    private SpannerTemplate spannerTemplate;

    public void insertAll(UserChat userChat) {
        spannerTemplate.insert(userChat);
    }
    
    public List<UserChat> getChatIdIfChatExistsBetweenUserIds(long userId1, long userId2) {

        String SQLStatment = "SELECT ChatID FROM UserChat WHERE UserID=@userId2 AND ChatID IN (SELECT ChatID FROM UserChat WHERE UserID=@userId1)";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userId2").to(userId2).bind("userId1").to(userId1).build();
        List<UserChat> resultSet = spannerTemplate.query(UserChat.class, statement, new SpannerQueryOptions().setAllowPartialRead(true));
    
        return resultSet;
    }

    public boolean checkIfUserChatIdExists(long userId, long chatId) {

        String SQLStatment = "SELECT * FROM UserChat WHERE UserID=@userId AND ChatID=@chatId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userId").to(userId).bind("chatId").to(chatId).build();
        List<UserChat> resultSet = spannerTemplate.query(UserChat.class, statement, null);
    
        return (!resultSet.isEmpty());
    }
}