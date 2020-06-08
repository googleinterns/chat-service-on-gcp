package DBAccesser.Message;

import Entity.Message;

import com.google.cloud.Timestamp;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

@Component
public class QueryMessage {
    
    @Autowired
    private SpannerTemplate spannerTemplate;

    public boolean checkIfMessageIDExists(long messageID) {

        String SQLStatment = "SELECT MessageID FROM Message WHERE MessageID=@messageID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("messageID").to(messageID).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        if (resultSet.size()>0) {
            return true;
        } else {
            return false;
        }
    }

    public Message getMessage(long messageID) {

        String SQLStatment = "SELECT * FROM Message WHERE MessageID=@messageID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("messageID").to(messageID).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement, null);
 
        return resultSet.get(0);
    }

    public Timestamp getCreationTSForMessageID(long messageID) {

        String SQLStatment = "SELECT CreationTS FROM Message WHERE MessageID=@messageID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("messageID").to(messageID).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        return resultSet.get(0).getCreationTS();
    }

    public boolean checkIfMessageIDBelongsToChatID(long messageID, long chatID) {
        
        String SQLStatment = "SELECT MessageID FROM Message WHERE MessageID=@messageID and ChatID=@chatID";
        Statement statement = Statement.newBuilder(SQLStatment).bind("messageID").to(messageID).bind("chatID").to(chatID).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  new SpannerQueryOptions().setAllowPartialRead(true));
 
        if (resultSet.size()>0) {
            return true;
        } else {
            return false;
        } 
    }

    public List<Message> listCountMessagesOfChatIDWithinGivenTime(Timestamp startCreationTS, Timestamp endCreationTS, int count, long chatID) {

        String SQLStatment = "SELECT * FROM Message WHERE ChatID = @chatID AND CreationTS >= @startCreationTS AND CreationTS <= @endCreationTS ORDER BY CreationTS DESC LIMIT @count";
        Statement statement = Statement.newBuilder(SQLStatment).bind("chatID").to(chatID).bind("startCreationTS").to(startCreationTS).bind("endCreationTS").to(endCreationTS).bind("count").to(count).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  null);
 
        return resultSet;
    }

    public List<Message> listCountMessagesOfChatIDFromStartTime(Timestamp startCreationTS, int count, long chatID) {

        String SQLStatment = "SELECT * FROM Message WHERE ChatID = @chatID AND CreationTS >= @startCreationTS ORDER BY CreationTS DESC LIMIT @count";
        Statement statement = Statement.newBuilder(SQLStatment).bind("chatID").to(chatID).bind("startCreationTS").to(startCreationTS).bind("count").to(count).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  null);
 
        return resultSet;
    }

    public List<Message> listCountMessagesOfChatIDBeforeEndTime(Timestamp endCreationTS, int count, long chatID) {

        String SQLStatment = "SELECT * FROM Message WHERE ChatID = @chatID AND CreationTS <= @endCreationTS ORDER BY CreationTS DESC LIMIT @count";
        Statement statement = Statement.newBuilder(SQLStatment).bind("chatID").to(chatID).bind("endCreationTS").to(endCreationTS).bind("count").to(count).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  null);
 
        return resultSet;
    }

    public List<Message> listLatestCountMessagesOfChatID(int count, long chatID) {

        String SQLStatment = "SELECT * FROM Message WHERE ChatID = @chatID ORDER BY CreationTS DESC LIMIT @count";
        Statement statement = Statement.newBuilder(SQLStatment).bind("chatID").to(chatID).bind("count").to(count).build();
        List<Message> resultSet = spannerTemplate.query(Message.class, statement,  null);
 
        return resultSet;
    }
}