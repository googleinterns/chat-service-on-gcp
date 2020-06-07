package Entity;

import com.google.cloud.Timestamp;
import java.util.Comparator;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;
import org.springframework.cloud.gcp.data.spanner.core.mapping.NotMapped;

@Table(name = "Chat")
public class Chat {
    
    @Column(name = "CreationTS", spannerCommitTimestamp = true) 
    public Timestamp creationTS;
    
    @PrimaryKey
    @Column(name = "ChatID")
    public long chatID;

    @Column(name = "LastSentMessageID")
    public long lastSentMessageID;

    @NotMapped
    public Timestamp lastSentTime;

    public Chat() {

    }
  
    public Chat(long chatID, long lastSentMessageID) {
      
        this.chatID = chatID;
        this.lastSentMessageID = lastSentMessageID;
    }

    public Chat(long ID, String chatIDOrlastSentMessageID) {
      
        switch(chatIDOrlastSentMessageID) {
            case "ChatID":  this.chatID = ID;
                            this.lastSentMessageID = -1;
                            break;
            case "LastSentMessageID":   this.chatID = -1;
                                        this.lastSentMessageID = ID;
        }
    }
  
    public void setChatID(long chatID) {
      
        this.chatID = chatID;
    }

    public void setLastSentMessageID(long lastSentMessageID) {
      
        this.lastSentMessageID = lastSentMessageID;
    }

    public void setLastSentTime(Timestamp lastSentTime) {
      
        this.lastSentTime = lastSentTime;
    }

    public Timestamp getCreationTS() {
  
        return this.creationTS;
    }
  
    public long getChatID() {
  
        return this.chatID;
    }

    public long getLastSentMessageID() {
  
        return this.lastSentMessageID;
    }

    public Timestamp getLastSentTime() {
  
        return this.lastSentTime;
    }

    public static Comparator<Chat> LastSentTimeDescComparator = new Comparator<Chat>() {

        public int compare(Chat chat1, Chat chat2) {
            
            Timestamp lastSentTime1 = chat1.getLastSentTime();
            Timestamp lastSentTime2 = chat2.getLastSentTime();

            return lastSentTime2.compareTo(lastSentTime1);
        }
    };
}