package DBAccesser.Chat;

import Entity.Class.User;
import Entity.Class.Chat;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

@Component
public class QueryChat {
    
    @Autowired
    SpannerTemplate spannerTemplate;

    public boolean checkIfChatIDExists(long chatID) {

        String SQLStatment = "SELECT ChatID FROM Chat WHERE ChatID=@chatID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("chatID").to(chatID).build();
        List<Chat> resultSet = spannerTemplate.query(Chat.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        if (resultSet.size()>0) {
            return true;
        } else {
            return false;
        }        
    }

    public Chat getChat(long chatID) {

        String SQLStatment = "SELECT * FROM Chat WHERE ChatID=@chatID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("chatID").to(chatID).build();
        List<Chat> resultSet = spannerTemplate.query(Chat.class, statement, null);
 
        return resultSet.get(0);
    }

    public List<Chat> getChatsForUser(User user) {

        String SQLStatment = "SELECT * FROM Chat WHERE ChatID in (SELECT ChatID FROM UserChat WHERE UserID=@userID)";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userID").to(user.getUserID()).build();
        List<Chat> resultSet = spannerTemplate.query(Chat.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet;
    }
}