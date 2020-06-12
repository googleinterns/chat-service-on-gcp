package util;


public class Message
{
    public int senderId;
    public int receiverId;
    public boolean received;
    public String text;
    public Long send_time;
    public Message()
    {

    }
    public Message(int mSenderID, int mReceiverID, boolean mReceived, String mText, Long mSendTime)
    {
        senderId = mSenderID;
        receiverId = mReceiverID;
        received = mReceived;
        text = mText;
        send_time = mSendTime;
    }
}
