package Entity;

import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

@Table(name = "UserChat")
public final class UserChat {
    
    @PrimaryKey(keyOrder = 1) //in the case of composite pk - key order must show how the pk is defined in the DDL of the table
    @Column(name = "UserID")
    private long userID;

    @PrimaryKey(keyOrder = 2)
    @Column(name = "ChatID")
    private long chatID;

    public UserChat() {}
  
    public UserChat(long userID, long chatID) {
      
        this.userID = userID;
        this.chatID = chatID;
    }

    public static UserChat newUserChatWithUserID(long userID) {
        return new UserChat(userID, 0);
    }

    public static UserChat newUserChatWithChatID(long chatID) {
        return new UserChat(0, chatID);
    }
  
    public void setUserID(long userID) {
      
        this.userID = userID;
    }
  
    public void setChatID(long chatID) {
      
        this.chatID = chatID;
    }
  
    public long getUserID() {
  
        return this.userID;
    }
    
    public long getChatID() {
  
        return this.chatID;
    }
}