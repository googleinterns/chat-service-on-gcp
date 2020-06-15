package util;


public class User
{
    public String username;
    public String chatID;
    public String lastMessageID;
    public User()
    {
    }
    public User(String mUsername, String mChatID, String mLastMessageID)
    {
        username = mUsername;
        chatID = mChatID;
        lastMessageID = mLastMessageID;
    }
}
