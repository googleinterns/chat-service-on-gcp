package com.gpayinterns.chat;

import android.provider.BaseColumns;

public final class DatabaseContract
{
    private DatabaseContract() {}

    public static final class userEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "User";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL_ID = "email_id";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_LAST_MESSAGE = "last_message";
        public static final String COLUMN_SERVER_USER_ID = "server_user_id";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ( "+
                        _ID + " INTEGER PRIMARY KEY , " +
                        COLUMN_NAME+" TEXT NOT NULL , "+
                        COLUMN_EMAIL_ID+" TEXT UNIQUE NOT NULL , "+
                        COLUMN_PASSWORD+" TEXT , "+
                        COLUMN_LAST_MESSAGE+" TEXT , " +
                        COLUMN_SERVER_USER_ID+" TEXT UNIQUE ) ";

        public static final String INDEX1 = TABLE_NAME  + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX "+INDEX1+" ON "+TABLE_NAME
                        +"( " + COLUMN_EMAIL_ID+" )";
    }
    public static final class messageEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "Message";
        public static final String COLUMN_SENDER = "sender";
        public static final String COLUMN_RECEIVER = "receiver";
        public static final String COLUMN_RECEIVED = "received";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_SEND_TIME = "send_time";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ( "+
                        _ID + " INTEGER PRIMARY KEY , " +
                        COLUMN_SENDER + " INTEGER NOT NULL , "+
                        COLUMN_RECEIVER + " INTEGER NOT NULL , "+
                        COLUMN_RECEIVED + " INTEGER NOT NULL , "+
                        COLUMN_TEXT + " TEXT NOT NULL , "+
                        COLUMN_SEND_TIME + " INTEGER NOT NULL ) ";

        public static final String INDEX1 = TABLE_NAME  + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX "+INDEX1+" ON "+TABLE_NAME
                        +"( " + COLUMN_SENDER+" )";

        public static final String INDEX2 = TABLE_NAME  + "_index2";
        public static final String SQL_CREATE_INDEX2 =
                "CREATE INDEX "+INDEX2+" ON "+TABLE_NAME
                        +"( " + COLUMN_RECEIVER+" )";

        public static final String INDEX3 = TABLE_NAME  + "_index3";
        public static final String SQL_CREATE_INDEX3 =
                "CREATE INDEX "+INDEX3+" ON "+TABLE_NAME
                        +"( " + COLUMN_SEND_TIME+" )";

        public static final String INDEX4 = TABLE_NAME  + "_index4";
        public static final String SQL_CREATE_INDEX4 =
                "CREATE INDEX "+INDEX4+" ON "+TABLE_NAME
                        +"( " + COLUMN_SENDER+" , "+ COLUMN_RECEIVER +" )";

    }
    public static final class chatEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "Chat";
        public static final String COLUMN_USER1 = "user1";
        public static final String COLUMN_USER2 = "user2";
        public static final String COLUMN_LAST_MESSAGE = "last_message";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ( "+
                        _ID + " INTEGER PRIMARY KEY , " +
                        COLUMN_USER1+" INTEGER NOT NULL , "+
                        COLUMN_USER2+" INTEGER NOT NULL , "+
                        COLUMN_LAST_MESSAGE+" TEXT  , " +
                        " UNIQUE ( user1 , user2 )  ) ";

        //Sql Indices
        public static final String INDEX1 = TABLE_NAME  + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX "+INDEX1+" ON "+TABLE_NAME
                        +"( " + COLUMN_USER1+" )";

        public static final String INDEX2 = TABLE_NAME  + "_index2";
        public static final String SQL_CREATE_INDEX2 =
                "CREATE INDEX "+INDEX2+" ON "+TABLE_NAME
                        +"( " + COLUMN_USER2+" )";

        public static final String INDEX3 = TABLE_NAME  + "_index3";
        public static final String SQL_CREATE_INDEX3 =
                "CREATE INDEX "+INDEX3+" ON "+TABLE_NAME
                        +"( " + COLUMN_USER1+" , " + COLUMN_USER2+ " )";
    }
}
