package entity;

import com.google.cloud.Timestamp;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

import java.util.Base64;

@Table(name = "User")
public class User {

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

  public enum  UniqueFields {
    USERNAME, EMAIL
  }

  public User() {

  }

  public User(
              long id, 
              String username, 
              String password, 
              String emailID, 
              String mobileNo, 
              String base64Image
            ) {
    
    this.UserID = id;
    this.Username = username;
    this.Password = password;
    this.EmailID = emailID;
    this.MobileNo = mobileNo;
    if(base64Image.length() != 0){
      this.Picture = base64Image;
    } else {
      this.Picture = null;
    }
  }

  public User(
          String username,
          String password,
          String emailID,
          String mobileNo,
          String base64Image
  ) {
    this(-1, username, password, emailID, mobileNo, base64Image);
  }

  public User(long userId, String username) {
    this.UserID = userId;
    this.Username = username;
  }

  public User(long userId) {
    this.UserID = userId;
    this.Username = null;
  }

  public User(String username) {
    this.UserID = 0;
    this.Username = username;
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
