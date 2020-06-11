package com.example.chat;


import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chat.DatabaseContract.messageEntry;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import util.message;

public class MessageRecyclerAdapter extends RecyclerView.Adapter <MessageRecyclerAdapter.ViewHolder>
{
    private final Context mContext;
    private final LayoutInflater mlayoutInflater;
    private Cursor mCursor;
    private int viewType;
    private int mReceivedPos;
    private int mTextPos;
    private int mSendTimePos;

    //viewType 0: left side text
    //viewType 1: right side text

    @Override
    public int getItemViewType(int position)
    {
        populateColumnPositions();
        if(mCursor.isClosed())
            return 0;
        mCursor.moveToPosition(position);
        if(mCursor.getInt(mReceivedPos)==1)//message is received
        {
            viewType =0;
        }
        else
        {
            viewType =1;
        }
        return viewType;
    }

    public MessageRecyclerAdapter(Context Context, Cursor cursor)
    {
        mContext = Context;
        mCursor = cursor;
        mlayoutInflater = LayoutInflater.from(mContext);
        populateColumnPositions();
    }

    private void populateColumnPositions()
    {
        if(mCursor==null)
            return;
        //Get column indices from mCursor
        mReceivedPos = mCursor.getColumnIndex(messageEntry.COLUMN_RECEIVED);
        mTextPos = mCursor.getColumnIndex(messageEntry.COLUMN_TEXT);
        mSendTimePos = mCursor.getColumnIndex(messageEntry.COLUMN_SEND_TIME);

    }

    public void changeCursor(Cursor cursor)
    {
        if(mCursor != null)
            mCursor.close();
        mCursor=cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }

    public void addRow(message m)
    {
        MatrixCursor matrixCursor = new MatrixCursor(new String[] { messageEntry.COLUMN_RECEIVED, messageEntry.COLUMN_TEXT,messageEntry.COLUMN_SEND_TIME });
        if(m.received)
        {
            matrixCursor.addRow(new Object[]{1, m.text, m.send_time});
        }
        else
        {
            matrixCursor.addRow(new Object[]{0, m.text, m.send_time});
        }
        mCursor = new MergeCursor(new Cursor[] {  mCursor,matrixCursor });

        populateColumnPositions();
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View itemView;
        if(viewType==0)
        {
            itemView = mlayoutInflater.inflate(R.layout.item_recieve_message_list,parent,false);
        }
        else
        {
            itemView = mlayoutInflater.inflate(R.layout.item_send_message_list,parent,false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        if(mCursor.isClosed())
            return ;
        mCursor.moveToPosition(position);
        String text = mCursor.getString(mTextPos);
        if(viewType<2)
        {
            holder.mMessage.setText(text);
        }
        else//image
        {
            File imgFile = new File(text);
            if(imgFile.exists())
            {

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.mImage.setImageBitmap(myBitmap);
            }
        }
    }


    @Override
    public int getItemCount()
    {
        if(mCursor==null)
            return 0;
        else
            return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView mMessage;
        public final ImageView mImage;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            if(viewType==0)
                mMessage = (TextView) itemView.findViewById(R.id.receive_message_text);
            else
                mMessage = (TextView) itemView.findViewById(R.id.send_message_text);


            if(viewType==2)
                mImage = (ImageView)  itemView.findViewById(R.id.receive_message_image);
            else
                mImage = (ImageView) itemView.findViewById(R.id.send_message_image);
        }
    }
}
