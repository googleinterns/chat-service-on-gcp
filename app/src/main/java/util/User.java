package util;


public class User
{
    public int userID;
    public String name;
    public String email_id;
    public String last_message;
    public User()
    {
    }
    public User(int mUserID, String mName, String mEmailId, String mLastMessage)
    {
        userID = mUserID;
        name = mName;
        email_id = mEmailId;
        last_message = mLastMessage;
    }
}
