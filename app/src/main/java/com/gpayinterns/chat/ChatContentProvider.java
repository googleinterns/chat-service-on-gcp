package com.gpayinterns.chat;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ChatContentProvider extends android.content.ContentProvider
{
    private OpenHelper mDbOpenHelper;

    public static UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static int MESSAGES = 0;
    public static int USERS = 1;
    public static int CHAT = 2;
    static
    {
        sURIMatcher.addURI(ChatProviderContract.AUTHORITY, ChatProviderContract.Messages.PATH, MESSAGES);
        sURIMatcher.addURI(ChatProviderContract.AUTHORITY, ChatProviderContract.Users.PATH,USERS);
        sURIMatcher.addURI(ChatProviderContract.AUTHORITY, ChatProviderContract.Chat.PATH,CHAT);

    }

    public ChatContentProvider()
    {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri)
    {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        long rowId = -1;
        Uri rowUri = null;
        int uriMatch = sURIMatcher.match(uri);

        if(uriMatch==MESSAGES)
        {
            rowId = db.insert(DatabaseContract.messageEntry.TABLE_NAME,null,values);
            if(rowId==-1)
                return null;
            rowUri = ContentUris.withAppendedId(ChatProviderContract.Messages.CONTENT_URI,rowId);
        }
        else if(uriMatch==USERS)
        {
            rowId = db.insert(DatabaseContract.userEntry.TABLE_NAME,null,values);
            if(rowId==-1)
                return null;
            rowUri = ContentUris.withAppendedId(ChatProviderContract.Users.CONTENT_URI,rowId);
        }
        else if(uriMatch==CHAT)
        {
            rowId = db.insert(DatabaseContract.chatEntry.TABLE_NAME,null,values);
            if(rowId==-1)
                return null;
            rowUri = ContentUris.withAppendedId(ChatProviderContract.Chat.CONTENT_URI,rowId);
        }

        return rowUri;

    }

    @Override
    public boolean onCreate()
    {
        mDbOpenHelper = new OpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        Cursor cursor = null;
        SQLiteDatabase db =mDbOpenHelper.getReadableDatabase();
        int uriMatch = sURIMatcher.match(uri);
        if(uriMatch==MESSAGES)
        {
            cursor=db.query(DatabaseContract.messageEntry.TABLE_NAME,projection,selection,selectionArgs,
                    null,null,sortOrder);
        }
        else if(uriMatch==USERS)
        {
            cursor=db.query(DatabaseContract.userEntry.TABLE_NAME,projection,selection,selectionArgs,
                    null,null,sortOrder);
        }
        else if(uriMatch==CHAT)
        {
            cursor=db.query(DatabaseContract.chatEntry.TABLE_NAME,projection,selection,selectionArgs,
                    null,null,sortOrder);
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs)
    {

        int uriMatch = sURIMatcher.match(uri);
        SQLiteDatabase db =mDbOpenHelper.getReadableDatabase();
        if(uriMatch==MESSAGES)
        {
            return db.update(DatabaseContract.messageEntry.TABLE_NAME,values,selection,selectionArgs);
        }
        else if(uriMatch==USERS)
        {
            return db.update(DatabaseContract.userEntry.TABLE_NAME,values,selection,selectionArgs);
        }
        else if(uriMatch==CHAT)
        {
            return db.update(DatabaseContract.chatEntry.TABLE_NAME,values,selection,selectionArgs);
        }
        return 0;
    }
}
