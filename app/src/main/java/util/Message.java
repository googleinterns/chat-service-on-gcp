package util;


import android.graphics.Bitmap;

public class Message
{
    public String messageID;
    public String chatID;
    public boolean received;
    public String text;
    public String sendTime;
    public Bitmap image;

    public Message()
    {

    }
    public Message(String mMessageID,String mChatID, boolean mReceived, String mText, String mSendTime,Bitmap mImage)
    {
        messageID = mMessageID;
        chatID = mChatID;
        received = mReceived;
        text = mText;
        sendTime = mSendTime;
        image = mImage;
    }
}
