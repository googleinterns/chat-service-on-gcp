package util;


public class message
{
    public int senderId;
    public int receiverId;
    public boolean received;
    public String text;
    public Long send_time;
    public message()
    {

    }
    public message(int mSenderID,int mReceiverID,boolean mreceived,String mtext,Long msend_time)
    {
        senderId = mSenderID;
        receiverId = mReceiverID;
        received = mreceived;
        text = mtext;
        send_time = msend_time;
    }
}
