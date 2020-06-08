package Entity.Class;

import java.sql.Timestamp;
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
  private byte[] Picture;

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
    if(base64Image.length() == 0){
      this.Picture = base64Image.getBytes();
    } else {
      this.Picture = null;
    }
  }

  public User(
              String username, 
              String password, 
              String emailID, 
              String mobileNo
            ) {

    this.Username = username;
    this.Password = password;
    this.EmailID = emailID;
    this.MobileNo = mobileNo;
    this.Picture = null;
  }

  public long getUserID(){
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

  public byte[] getPicture(){
    return this.Picture;
  }

  public void setUserID(long id){
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

  public void setPicture(byte[] picture){
    this.Picture = picture;
  }

}