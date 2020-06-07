package Entity;

import com.google.cloud.Timestamp;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

@Table(name = "User")
public class User {

  @Column(name = "CreationTS", spannerCommitTimestamp = true) 
  public Timestamp creationTS;
  
  @PrimaryKey
  @Column(name = "UserID")
  public long userID;

  @Column(name = "Username")
  public String username;

  public User() {

  }

  public User(long userID, String username) {
    
    this.userID = userID;
    this.username = username;
  }

  public User(long userID) {

    this.userID = userID;
    this.username = null;
  }

  public User(String username) {

    this.userID = -1;
    this.username = username;
  }

  public void setCreationTS(Timestamp creationTS) {

    this.creationTS = creationTS;
  }

  public void setUserID(long userID) {
    
    this.userID = userID;
  }

  public void setUsername(String username) {

    this.username = username;
  }

  public Timestamp getCreationTS() {
  
    return this.creationTS;
  }

  public long getUserID() {

    return this.userID;
  }
  
  public String getUsername() {

    return this.username;
  }
}