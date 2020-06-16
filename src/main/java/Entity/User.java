package Entity;

import com.google.cloud.Timestamp;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

@Table(name = "User")
public final class User {

  @Column(name = "CreationTS", spannerCommitTimestamp = true) 
  public Timestamp creationTs;
  
  @PrimaryKey
  @Column(name = "UserID")
  private long userId;

  @Column(name = "Username")
  private String username;

  public User() {}

  public User(long userId, String username) {
    
    this.userId = userId;
    this.username = username;
  }

  public User(long userId) {

    this.userId = userId;
    this.username = null;
  }

  public User(String username) {

    this.userId = 0;
    this.username = username;
  }

  public void setCreationTs(Timestamp creationTs) {

    this.creationTs = creationTs;
  }

  public void setUserId(long userId) {
    
    this.userId = userId;
  }

  public void setUsername(String username) {

    this.username = username;
  }

  public Timestamp getCreationTs() {
  
    return this.creationTs;
  }

  public long getUserId() {

    return this.userId;
  }
  
  public String getUsername() {

    return this.username;
  }
}
