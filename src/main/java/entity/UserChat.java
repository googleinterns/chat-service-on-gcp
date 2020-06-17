package entity;

import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

@Table(name = "UserChat")
public final class UserChat {
    
    @PrimaryKey(keyOrder = 1)
    @Column(name = "UserID")
    private long userId;

    @PrimaryKey(keyOrder = 2)
    @Column(name = "ChatID")
    private long chatId;

    public UserChat() {}
  
    public UserChat(long userId, long chatId) {
      
        this.userId = userId;
        this.chatId = chatId;
    }

    public static UserChat newUserChatWithUserId(long userId) {
        return new UserChat(userId, 0);
    }

    public static UserChat newUserChatWithChatId(long chatId) {
        return new UserChat(0, chatId);
    }
  
    public void setUserId(long userId) {
      
        this.userId = userId;
    }
  
    public void setChatId(long chatId) {
      
        this.chatId = chatId;
    }
  
    public long getUserId() {
  
        return this.userId;
    }
    
    public long getChatId() {
  
        return this.chatId;
    }
}
