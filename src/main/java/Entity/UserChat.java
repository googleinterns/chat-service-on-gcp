package Entity;

import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

@Table(name = "UserChat")
public class UserChat {
    
    @PrimaryKey(keyOrder = 1) //in the case of composite pk - key order must show how the pk is defined in the DDL of the table
    @Column(name = "UserID")
    public long userID;

    @PrimaryKey(keyOrder = 2)
    @Column(name = "ChatID")
    public long chatID;

    public UserChat() {

    }
  
    public UserChat(long userID, long chatID) {
      
        this.userID = userID;
        this.chatID = chatID;
    }
  
    public UserChat(long ID, String userIDOrChatID) {
  
        switch(userIDOrChatID) {
            case "UserID":  this.userID = ID;
                            this.chatID = -1;
                            break;
            case "ChatID":  this.userID = -1;
                            this.chatID = ID;
        }
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