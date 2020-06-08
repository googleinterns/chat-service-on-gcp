package DBAccesser.Message;

import Entity.Message;

import com.google.cloud.Timestamp;
import com.google.cloud.spanner.Statement.Builder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

@Component
public class MessageAccessor {
    
    @Autowired
    private SpannerTemplate spannerTemplate;

    //called to insert all fields in message - its usage till now is to insert all fields (with ReceivedTS = null) when createChat is called
    public void insertAllForTextMessage(Message message) {
        spannerTemplate.insert(message);
    } 

    //called to update the Received TS of a message after ListChats is called by the Receiver
    public void insertReceivedTS(Message message) {
        spannerTemplate.update(message, "MessageID", "ReceivedTS");
    }

    //to check if message with passed messageID exists
    public boolean checkIfMessageIDExists(long messageID) {

        String SQLStatment = "SELECT MessageID FROM Message WHERE MessageID=@messageID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("messageID").to(messageID).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return (!resultSet.isEmpty());
    }

    //to get the message whose ID = passed messageID 
    public Message getMessage(long messageID) {

        String SQLStatment = "SELECT * FROM Message WHERE MessageID=@messageID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("messageID").to(messageID).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement, null);
 
        return resultSet.get(0);
    }

    //to get only the CreationTS field of the message with the passed messageID
    public Timestamp getCreationTSForMessageID(long messageID) {

        String SQLStatment = "SELECT CreationTS FROM Message WHERE MessageID=@messageID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("messageID").to(messageID).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet.get(0).getCreationTS();
    }

    //to check if given message belongs to given chat
    public boolean checkIfMessageIDBelongsToChatID(long messageID, long chatID) {
        
        String SQLStatment = "SELECT MessageID FROM Message WHERE MessageID=@messageID and ChatID=@chatID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("messageID").to(messageID).bind("chatID").to(chatID).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return (!resultSet.isEmpty()); 
    }

    private List<Message> listCountMessagesOfChatID(Timestamp startCreationTS, Timestamp endCreationTS, int count, long chatID) {

        String SQLStatment = "SELECT * FROM Message WHERE ChatID = @chatID";

        if (startCreationTS != null) {
            SQLStatment += " AND CreationTS >= @startCreationTS";
        }

        if (endCreationTS != null) {
            SQLStatment += " AND CreationTS <= @endCreationTS";
        }
        
        SQLStatment += " ORDER BY CreationTS DESC LIMIT @count";

        Builder builder = Statement.newBuilder(SQLStatment).bind("chatID").to(chatID);

        if (startCreationTS != null) {
            builder.bind("startCreationTS").to(startCreationTS);
        }

        if (endCreationTS != null) {
            builder.bind("endCreationTS").to(endCreationTS);
        }

        Statement statement = builder.bind("count").to(count).build();

        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  null);
 
        return resultSet;
    }

    public List<Message> listCountMessagesOfChatIDWithinGivenTime(Timestamp startCreationTS, Timestamp endCreationTS, int count, long chatID) {

        return listCountMessagesOfChatID(startCreationTS, endCreationTS, count, chatID);
    }

    public List<Message> listCountMessagesOfChatIDFromStartTime(Timestamp startCreationTS, int count, long chatID) {

        return listCountMessagesOfChatID(startCreationTS, null, count, chatID); 
    }

    public List<Message> listCountMessagesOfChatIDBeforeEndTime(Timestamp endCreationTS, int count, long chatID) {

        return listCountMessagesOfChatID(null, endCreationTS, count, chatID);
    }

    public List<Message> listLatestCountMessagesOfChatID(int count, long chatID) {

        return listCountMessagesOfChatID(null, null, count, chatID);
    }
}
