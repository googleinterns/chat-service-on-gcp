package dbaccessor.message;

import entity.Message;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Statement.Builder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

/**
 * Accessor which performs database accesses for the Message entity.
 */
@Component
public final class MessageAccessor {
    
    @Autowired
    private SpannerTemplate spannerTemplate;

    /**
     * Inserts all attributes of a Message in the DB.
     */
    public void insertAllForTextMessage(Message message) {
        spannerTemplate.insert(message);
    } 

    /**
     * Updates the Received Timestamp of a Message.
     */
    public void updateReceivedTs(Message message) {
        spannerTemplate.update(message, "MessageID", "ReceivedTS");
    }

    /**
     * Checks if a Message with the given messageId exists.
     */
    public boolean checkIfMessageIdExists(long messageId) {

        String SQLStatment = "SELECT MessageID FROM Message WHERE MessageID=@messageId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("messageId").to(messageId).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return (!resultSet.isEmpty());
    }

    /**
     * Returns details of Message with the given MessageId.
     * Details include:
     * (1)  MessageId
     * (2)  ChatId
     * (3)  SenderId
     * (4)  ContentType
     * (5)  TextContent
     * (6)  Sent Timestamp
     * (7)  Received Timestamp
     * (8)  Creation Timestamp
     */
    public Message getMessage(long messageId) {

        String SQLStatment = "SELECT * FROM Message WHERE MessageID=@messageId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("messageId").to(messageId).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement, null);
 
        return resultSet.get(0);
    }

    /**
     * Returns the Creation Timestamp field of the Message with the given messageId.
     */
    public Timestamp getCreationTsForMessageId(long messageId) {

        String SQLStatment = "SELECT CreationTS FROM Message WHERE MessageID=@messageId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("messageId").to(messageId).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet.get(0).getCreationTs();
    }

    /**
     * Checks if the given Message belongs to the given Chat.
     */
    public boolean checkIfMessageIdBelongsToChatId(long messageId, long chatId) {
        
        String SQLStatment = "SELECT MessageID FROM Message WHERE MessageID=@messageId and ChatID=@chatId";
        Statement statement = Statement.newBuilder(SQLStatment).bind("messageId").to(messageId).bind("chatId").to(chatId).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return (!resultSet.isEmpty()); 
    }

    private List<Message> listCountMessagesOfChatId(Timestamp startCreationTs, Timestamp endCreationTs, int count, long chatId) {

        String SQLStatment = "SELECT * FROM Message WHERE ChatID = @chatId";

        if (startCreationTs != null) {
            SQLStatment += " AND CreationTS >= @startCreationTs";
        }

        if (endCreationTs != null) {
            SQLStatment += " AND CreationTS <= @endCreationTs";
        }
        
        SQLStatment += " ORDER BY CreationTS DESC LIMIT @count";

        Builder builder = Statement.newBuilder(SQLStatment).bind("chatId").to(chatId);

        if (startCreationTs != null) {
            builder.bind("startCreationTs").to(startCreationTs);
        }

        if (endCreationTs != null) {
            builder.bind("endCreationTs").to(endCreationTs);
        }

        Statement statement = builder.bind("count").to(count).build();

        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  null);
 
        return resultSet;
    }

    /**
     * Returns details of <b>count</b> number of Messages of the given Chat within the required time frame.
     */
    public List<Message> listCountMessagesOfChatIdWithinGivenTime(Timestamp startCreationTs, Timestamp endCreationTs, int count, long chatId) {

        return listCountMessagesOfChatId(startCreationTs, endCreationTs, count, chatId);
    }

    /**
     * Returns details of <b>count</b> number of Messages of the given Chat beginning at the start time.
     */
    public List<Message> listCountMessagesOfChatIdFromStartTime(Timestamp startCreationTs, int count, long chatId) {

        return listCountMessagesOfChatId(startCreationTs, null, count, chatId); 
    }

    /**
     * Returns details of <b>count</b> number of Messages of the given Chat before the end time.
     */
    public List<Message> listCountMessagesOfChatIdBeforeEndTime(Timestamp endCreationTs, int count, long chatId) {

        return listCountMessagesOfChatId(null, endCreationTs, count, chatId);
    }

    /**
     * Returns details of <b>count</b> number of latest Messages.
     */
    public List<Message> listLatestCountMessagesOfChatId(int count, long chatId) {

        return listCountMessagesOfChatId(null, null, count, chatId);
    }

    /**
     * Returns details of Messages which were sent last in the Chats which the given User is engaged in.
     * Details include:
     * (1)  ChatId
     * (2)  Creation Timestamp (of the Message)
     */
    public List<Message> getCreationTsOfLastSentMessageIdForChatsOfUser(long userId) {

        String SQLStatment = "SELECT ChatID, CreationTS FROM Message WHERE MessageID IN (SELECT LastSentMessageID FROM Chat WHERE ChatID IN (SELECT ChatID FROM UserChat WHERE UserID = @userId) AND LastSentMessageID IS NOT NULL)";
        Statement statement = Statement.newBuilder(SQLStatment).bind("userId").to(userId).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet;
    }
}