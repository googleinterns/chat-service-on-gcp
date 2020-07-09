package com.gpayinterns.chat;


public class User
{
    public String username;
    public String chatID;
    public String lastMessageID;
    public String phoneNum;
    public User()
    {
    }
    public User(String mUsername, String mChatID, String mLastMessageID, String mPhoneNum)
    {
        username = mUsername;
        chatID = mChatID;
        lastMessageID = mLastMessageID;
        phoneNum = mPhoneNum;
    }
}
