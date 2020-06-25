package dbaccessor.chat;

import entity.User;
import entity.Chat;
import entity.UserChat;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;
import org.springframework.cloud.gcp.data.spanner.core.SpannerOperations;

/**
 * Accessor which performs database accesses for the Chat entity.
 */
@Component
public final class ChatAccessor {
    
    @Autowired
    SpannerOperations spannerOperations;

    @Autowired
    private SpannerTemplate spannerTemplate;

    /**
     * Updates the LastSentMessageId attribute in a Chat object .
     */
    public void updateLastSentMessageId(Chat chat) {
        spannerTemplate.update(chat, "ChatID", "LastSentMessageID");
    }

    /**
     * Completes all DB insertions for the CreateChat API in a single transaction.
     */
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

    /**
     * Checks if a Chat with the given chatId exists.
     */
    public boolean checkIfChatIdExists(long chatId) {

        String sqlStatment = "SELECT ChatID FROM Chat WHERE ChatID=@chatId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("chatId").to(chatId).build();
        List<Chat> resultSet = spannerTemplate.query(Chat.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return !resultSet.isEmpty();        
    }

    /**
     * Returns details of the Chat with the given ChatId.
     * Details include:
     * <ol>
     * <li> ChatId </li>
     * <li> LastSentMessageId </li>
     * <li> Creation Timestamp </li>
     * </ol>
     */
    public Chat getChat(long chatId) {

        String sqlStatment = "SELECT * FROM Chat WHERE ChatID=@chatId";
        Statement statement = Statement.newBuilder(sqlStatment).bind("chatId").to(chatId).build();
        List<Chat> resultSet = spannerTemplate.query(Chat.class, statement, null);
 
        return resultSet.get(0);
    }

    /**
     * Returns details of all Chats which the given User id engaged in.
     * Details include:
     * <ol>
     * <li> ChatId </li>
     * <li> LastSentMessageId </li>
     * <li> Creation Timestamp </li>
     * </ol>
     */
    public List<Chat> getChatsForUser(User user) {

        String sqlStatment = "SELECT * FROM Chat WHERE ChatID in (SELECT ChatID FROM UserChat WHERE UserID=@userId)";
        Statement statement = Statement.newBuilder(sqlStatment).bind("userId").to(user.getUserId()).build();
        List<Chat> resultSet = spannerTemplate.query(Chat.class, statement, null);
 
        return resultSet;
    }
}
