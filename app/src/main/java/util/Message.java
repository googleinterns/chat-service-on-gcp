package util;


public class Message
{
    public String messageID;
    public String chatID;
    public boolean received;
    public String text;
    public Long sendTime;

    public Message()
    {

    }
    public Message(String mMessageID,String mChatID, boolean mReceived, String mText, Long mSendTime)
    {
        messageID = mMessageID;
        chatID = mChatID;
        received = mReceived;
        text = mText;
        sendTime = mSendTime;
    }
}
