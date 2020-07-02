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
    public Bitmap image;
    public Uri uri;
    public String fileName;
    public String mimeType;
    public String fileSize;

    public Message()
    {

    }
    public Message(String mMessageID,String mChatID, boolean mReceived,
                   String mText, String mSendTime, Bitmap mImage,
                   Uri mUri, String mFileName, String mMimeType,String mFileSize)
    {
        messageID = mMessageID;
        chatID = mChatID;
        received = mReceived;
        text = mText;
        sendTime = mSendTime;
        image = mImage;
        uri = mUri;
        fileName = mFileName;
        mimeType = mMimeType;
        fileSize = mFileSize;
    }
}
