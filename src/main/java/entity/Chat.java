package entity;

import com.google.cloud.Timestamp;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;
import org.springframework.cloud.gcp.data.spanner.core.mapping.NotMapped;

@Table(name = "Chat")
public final class Chat {
    
    @Column(name = "CreationTS", spannerCommitTimestamp = true) 
    private Timestamp creationTs;
    
    @PrimaryKey
    @Column(name = "ChatID")
    private long chatId;

    @Column(name = "LastSentMessageID")
    private long lastSentMessageId;

    @NotMapped
    private Timestamp lastSentTime;

    public Chat() {}
  
    public Chat(long chatId, long lastSentMessageId) {
      
        this.chatId = chatId;
        this.lastSentMessageId = lastSentMessageId;
    }

    public static Chat newChatWithChatId(long chatId) {
        return new Chat(chatId, 0);
    }

    public static Chat newChatWithLastSentMessageId(long lastSentMessageId) {
        return new Chat(0, lastSentMessageId);
    }
  
    public void setChatId(long chatId) {
      
        this.chatId = chatId;
    }

    public void setLastSentMessageId(long lastSentMessageId) {
      
        this.lastSentMessageId = lastSentMessageId;
    }

    public void setLastSentTime(Timestamp lastSentTime) {
      
        this.lastSentTime = lastSentTime;
    }

    public Timestamp getCreationTs() {
  
        return this.creationTs;
    }
  
    public long getChatId() {
  
        return this.chatId;
    }

    public long getLastSentMessageId() {
  
        return this.lastSentMessageId;
    }

    public Timestamp getLastSentTime() {
  
        return this.lastSentTime;
    }
}
