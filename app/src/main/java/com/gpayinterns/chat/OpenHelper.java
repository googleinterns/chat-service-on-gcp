package com.gpayinterns.chat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class OpenHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "Chat.db";
    private static final int DATABASE_VERSION = 1;
    private static OpenHelper instance;

    public OpenHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DatabaseContract.userEntry.SQL_CREATE_TABLE);
        db.execSQL(DatabaseContract.messageEntry.SQL_CREATE_TABLE);
        db.execSQL(DatabaseContract.chatEntry.SQL_CREATE_TABLE);

        db.execSQL(DatabaseContract.userEntry.SQL_CREATE_INDEX1);

        db.execSQL(DatabaseContract.messageEntry.SQL_CREATE_INDEX1);
        db.execSQL(DatabaseContract.messageEntry.SQL_CREATE_INDEX2);
        db.execSQL(DatabaseContract.messageEntry.SQL_CREATE_INDEX3);
        db.execSQL(DatabaseContract.messageEntry.SQL_CREATE_INDEX4);

        db.execSQL(DatabaseContract.chatEntry.SQL_CREATE_INDEX1);
        db.execSQL(DatabaseContract.chatEntry.SQL_CREATE_INDEX2);
        db.execSQL(DatabaseContract.chatEntry.SQL_CREATE_INDEX3);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}