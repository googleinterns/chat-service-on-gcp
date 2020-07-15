package com.gpayinterns.chat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.gpayinterns.chat.DatabaseContract.BlobEntry;


public class DataManager
{
    /**
     * @param dbHelper  the openhelper instance initialized in the activity to access the db
     * @param messageID the messageID whose path is to be know from the db
     * @return          path corresponding to messageID
     */
    public static String getFromDatabase(OpenHelper dbHelper,String messageID)
    {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String[] userColumns =
                {
                        BaseColumns._ID,
                        BlobEntry.COLUMN_MESSAGE_ID,
                        BlobEntry.COLUMN_PATH
                };
        final Cursor cursor = db.query(BlobEntry.TABLE_NAME, userColumns,BlobEntry.COLUMN_MESSAGE_ID+" = ? ", new String[]{messageID},null,null,null);

        String path = null;
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                path = cursor.getString(cursor.getColumnIndex(BlobEntry.COLUMN_PATH));
            }
            cursor.close();
        }
        return path;
    }

    /**
     *
     * @param dbHelper   the openhelper instance initialized in the activity to access the db
     * @param messageID  the messageID whose path is to be stored into the db
     * @param path       the path which is to be stored in the db
     */
    public static void loadToDatabase(OpenHelper dbHelper,String messageID,String path)
    {
        SQLiteDatabase db=dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BlobEntry.COLUMN_MESSAGE_ID,messageID);
        values.put(BlobEntry.COLUMN_PATH, path);

        long index = db.insert(BlobEntry.TABLE_NAME, null, values);
        if(index==-1)//not inserted as messageID already exists in the table
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BlobEntry.COLUMN_MESSAGE_ID,messageID);
            contentValues.put(BlobEntry.COLUMN_PATH,path);

            db.update(BlobEntry.TABLE_NAME,contentValues,BlobEntry.COLUMN_MESSAGE_ID+" = ? ", new String[]{messageID});
        }
    }
}
