package com.gpayinterns.chat;


import android.graphics.Bitmap;
import android.net.Uri;

public class Message
{
    public String messageID;
    public String chatID;
    public boolean received;
    public String text;
    public String sendTime;
    public String fileName;
    public String mimeType;
    public String fileSize;

    public Message()
    {

    }
    public Message(String mMessageID,String mChatID, boolean mReceived,
                   String mText, String mSendTime, String mFileName,
                   String mMimeType,String mFileSize)
    {
        messageID = mMessageID;
        chatID = mChatID;
        received = mReceived;
        text = mText;
        sendTime = mSendTime;
        fileName = mFileName;
        mimeType = mMimeType;
        fileSize = mFileSize;
    }
}
