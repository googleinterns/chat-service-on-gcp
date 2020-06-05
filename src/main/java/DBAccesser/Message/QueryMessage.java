package DBAccesser.Message;

import Entity.Class.Message;

import java.sql.Timestamp;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import java.util.List;
import com.google.cloud.spanner.Statement;
import org.springframework.cloud.gcp.data.spanner.core.SpannerQueryOptions;

@Component
public class QueryMessage {
    
    @Autowired
    SpannerTemplate spannerTemplate;

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
}