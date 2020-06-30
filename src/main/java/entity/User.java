package entity;

import com.google.cloud.Timestamp;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
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
      this.password = hashPassword(password);
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
      user.UserId = this.userId;
      user.Username = this.username;
      user.Password = this.password;
      user.EmailId = this.emailId;
      user.MobileNo = this.mobileNo;
      user.Picture = this.picture;
      return user;
    }
  }

  @Column(name = "CreationTS", spannerCommitTimestamp = true) 
  private Timestamp CreationTS;
  
  @PrimaryKey
  @Column(name = "UserID")
  private long UserId;

  @Column(name = "Username")
  private String Username;

  @Column(name = "Password")
  private String Password;

  @Column(name = "EmailID")
  private String EmailId;

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

  public static String hashPassword(String password) {
    HashFunction hashFunction = Hashing.farmHashFingerprint64();
    return hashFunction.hashUnencodedChars(password).toString();
  }

  public long getUserId(){
    return this.UserId;
  }

  public String getUsername(){
    return this.Username;
  }

  public String getEmailId(){
    return this.EmailId;
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
    this.UserId = id;
  }

  public void setUsername(String username){
    this.Username = username;
  }

  public void setEmailId(String emailId){
    this.EmailId = emailId;
  }

  public void setPassword(String password){
    this.Password = hashPassword(password);
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
