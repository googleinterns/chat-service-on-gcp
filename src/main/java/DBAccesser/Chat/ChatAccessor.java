package DBAccesser.Chat;

import Entity.User;
import Entity.Chat;
import Entity.UserChat;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;
import org.springframework.cloud.gcp.data.spanner.core.SpannerOperations;

@Component
public final class ChatAccessor {
    
    @Autowired
    SpannerOperations spannerOperations;

    @Autowired
    private SpannerTemplate spannerTemplate;

    public void insertLastSentMessageId(Chat chat) {
        //must insert PK even in partial update
        spannerTemplate.update(chat, "ChatID", "LastSentMessageID");
    }

    public boolean insertForCreateChatTransaction(Chat chat, UserChat userChat1, UserChat userChat2) {
        return spannerOperations.performReadWriteTransaction(
            transactionSpannerOperations -> {
                transactionSpannerOperations.upsert(chat, "ChatID", "CreationTS");
                transactionSpannerOperations.insert(userChat1);
                transactionSpannerOperations.insert(userChat2);

                return true;
            }
        );
    }

    public boolean checkIfChatIdExists(long chatId) {

        String SQLStatment = "SELECT ChatID FROM Chat WHERE ChatID=@chatId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("chatId").to(chatId).build();
        List<Chat> resultSet = spannerTemplate.query(Chat.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return (!resultSet.isEmpty());        
    }

    public Chat getChat(long chatId) {

        String SQLStatment = "SELECT * FROM Chat WHERE ChatID=@chatId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("chatId").to(chatId).build();
        List<Chat> resultSet = spannerTemplate.query(Chat.class, statement, null);
 
        return resultSet.get(0);
    }

    public List<Chat> getChatsForUser(User user) {

        String SQLStatment = "SELECT * FROM Chat WHERE ChatID in (SELECT ChatID FROM UserChat WHERE UserID=@userId)";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userId").to(user.getUserId()).build();
        List<Chat> resultSet = spannerTemplate.query(Chat.class, statement, null);
 
        return resultSet;
    }
}
