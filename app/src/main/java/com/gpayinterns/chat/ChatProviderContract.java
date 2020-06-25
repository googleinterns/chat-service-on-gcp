package com.gpayinterns.chat;

import android.net.Uri;
import android.provider.BaseColumns;

public final class ChatProviderContract
{
    private ChatProviderContract() {};

    public static final String AUTHORITY = "com.gpayinterns.chat.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    protected interface MessagesColumns
    {
        public static final String COLUMN_SENDER = "sender";
        public static final String COLUMN_RECEIVER = "receiver";
        public static final String COLUMN_RECEIVED = "received";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_SEND_TIME = "send_time";
    }

    protected interface UsersColumns
    {
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL_ID = "email_id";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_LAST_MESSAGE = "last_message";
        public static final String COLUMN_SERVER_USER_ID = "server_user_id";
    }
    protected interface ChatColumns
    {
        public static final String COLUMN_USER1 = "user1";
        public static final String COLUMN_USER2 = "user2";
        public static final String COLUMN_LAST_MESSAGE = "last_message";
    }

    public static final class Messages implements BaseColumns,MessagesColumns
    {
        public static final String PATH = "messages";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,PATH);
    }

    public static final class Users implements BaseColumns,UsersColumns
    {
        public static final String PATH = "users";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,PATH);
    }

    public static final class Chat implements BaseColumns,ChatColumns
    {
        public static final String PATH = "Chat";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI,PATH);
    }
}
