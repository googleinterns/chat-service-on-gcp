package Entity;

import com.google.cloud.Timestamp;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;
import org.springframework.cloud.gcp.data.spanner.core.mapping.NotMapped;

@Table(name = "Chat")
public final class Chat {
    
    @Column(name = "CreationTS", spannerCommitTimestamp = true) 
    private Timestamp creationTS;
    
    @PrimaryKey
    @Column(name = "ChatID")
    private long chatID;

    @Column(name = "LastSentMessageID")
    private long lastSentMessageID;

    @NotMapped
    private Timestamp lastSentTime;

    public Chat() {}
  
    public Chat(long chatID, long lastSentMessageID) {
      
        this.chatID = chatID;
        this.lastSentMessageID = lastSentMessageID;
    }

    public static Chat newChatWithChatID(long chatID) {
        return new Chat(chatID, 0);
    }

    public static Chat newChatWithLastSentMessageID(long lastSentMessageID) {
        return new Chat(0, lastSentMessageID);
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
}
