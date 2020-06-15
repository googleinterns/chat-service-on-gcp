package util;


public class Message
{
    public String chatID;
    public boolean received;
    public String text;
    public Long sendTime;
    public Message()
    {

    }
    public Message(String mChatID, boolean mReceived, String mText, Long mSendTime)
    {
        chatID = mChatID;
        received = mReceived;
        text = mText;
        sendTime = mSendTime;
    }
}
