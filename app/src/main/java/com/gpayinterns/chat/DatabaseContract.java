package com.gpayinterns.chat;

import android.provider.BaseColumns;

public final class DatabaseContract
{
    private DatabaseContract() {}

    public static final class BlobEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "Message";
        public static final String COLUMN_MESSAGE_ID = "message_id";
        public static final String COLUMN_PATH = "path";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " ( "+
                        _ID + " INTEGER PRIMARY KEY , " +
                        COLUMN_MESSAGE_ID+" TEXT UNIQUE NOT NULL , "+
                        COLUMN_PATH+" TEXT NOT NULL ) ";
    }
}
