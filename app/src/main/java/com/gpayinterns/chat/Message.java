package com.gpayinterns.chat;


public class Message
{
    public String messageID;
    public String chatID;
    public boolean received;
    public String text;
    public String sendTime;

    public Message()
    {

    }
    public Message(String mMessageID,String mChatID, boolean mReceived, String mText, String mSendTime)
    {
        messageID = mMessageID;
        chatID = mChatID;
        received = mReceived;
        text = mText;
        sendTime = mSendTime;
    }
}
