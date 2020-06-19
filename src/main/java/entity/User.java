package entity;

import com.google.cloud.Timestamp;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;


@Table(name = "User")
public class User {

  public static class Builder {

    private Timestamp creationTs;
    private long userId;
    private String username;
    private String password;
    private String emailId;
    private String mobileNo;
    private String picture;

    private Builder() {}

    public Builder creationTs(Timestamp creationTs) {
      this.creationTs = creationTs;
      return this;
    }

    public Builder userId(long userId) {
      this.userId = userId;
      return this;
    }

    public Builder username(String username) {
      this.username = username;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public Builder emailId(String emailId) {
      this.emailId = emailId;
      return this;
    }

    public Builder mobileNo(String mobileNo) {
      this.mobileNo = mobileNo;
      return this;
    }

    public Builder picture(String picture) {
      this.picture = picture;
      return this;
    }

    public User build() {
      User user = new User();
      user.CreationTS = this.creationTs;
      user.UserID = this.userId;
      user.Username = this.username;
      user.Password = this.password;
      user.EmailID = this.emailId;
      user.MobileNo = this.mobileNo;
      user.Picture = this.picture;
      return user;
    }
  }

  @Column(name = "CreationTS", spannerCommitTimestamp = true) 
  private Timestamp CreationTS;
  
  @PrimaryKey
  @Column(name = "UserID")
  private long UserID;

  @Column(name = "Username")
  private String Username;

  @Column(name = "Password")
  private String Password;

  @Column(name = "EmailID")
  private String EmailID;

  @Column(name = "MobileNo")
  private String MobileNo;

  @Column(name = "Picture")
  private String Picture;

  public enum UniqueFields {
    USERNAME, EMAIL
  }
  
  private User() {

  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public long getUserId(){
    return this.UserID;
  }

  public String getUsername(){
    return this.Username;
  }

  public String getEmailID(){
    return this.EmailID;
  }

  public String getPassword(){
    return this.Password;
  }

  public String getMobileNumber(){
    return this.MobileNo;
  }

  public String getPicture(){
    return this.Picture;
  }
  
  public Timestamp getCreationTs() {
    return this.CreationTS;
  }

  public void setUserId(long id){
    this.UserID = id;
  }

  public void setUsername(String username){
    this.Username = username;
  }

  public void setEmailID(String emailID){
    this.EmailID = emailID;
  }

  public void setPassword(String password){
    this.Password = password;
  }

  public void setMobileNumber(String mobileNo){
    this.MobileNo = mobileNo;
  }

  public void setPicture(String picture){
    this.Picture = picture;
  }
  
  public void setCreationTs(Timestamp creationTs) {
    this.CreationTS = creationTs;
  }
}
