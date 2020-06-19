package com.gpayinterns.chat;


import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gpayinterns.chat.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import util.Message;

public class MessageRecyclerAdapter extends RecyclerView.Adapter <MessageRecyclerAdapter.ViewHolder>
{
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<Message> mMessages;
    private int mViewType;

    //mViewType 0: left side text
    //mViewType 1: right side text

    @Override
    public int getItemViewType(int position)
    {
        if(mMessages.get(position).received)//Message is received
        {
            mViewType = 0;
        }
        else
        {
            mViewType = 1;
        }
        return mViewType;
    }

    public MessageRecyclerAdapter(Context Context, List <Message> messages)
    {
        mContext = Context;
        mMessages = messages;
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View itemView;
        if(viewType==0)
        {
            itemView = mLayoutInflater.inflate(R.layout.item_recieve_message_list,parent,false);
        }
        else
        {
            itemView = mLayoutInflater.inflate(R.layout.item_send_message_list,parent,false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.mMessage.setText(mMessages.get(position).text);
        holder.mTime.setText(convertDate(mMessages.get(position).sendTime));
    }


    @Override
    public int getItemCount()
    {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView mMessage;
        public final TextView mTime;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            if(mViewType==0)
            {
                mMessage = (TextView) itemView.findViewById(R.id.receive_message_text);
                mTime = (TextView) itemView.findViewById(R.id.time_receive_message_text);
            }
            else
            {
                mMessage = (TextView) itemView.findViewById(R.id.send_message_text);
                mTime = (TextView) itemView.findViewById(R.id.time_send_message_text);
            }
        }
    }

    public void addMessages(List<Message> newMessages)
    {
        int positionStart = mMessages.size();
        if(newMessages.size()>1)
        {
            mMessages.addAll(newMessages);
            notifyItemRangeInserted(positionStart, newMessages.size());
        }
        else
        {
            mMessages.addAll(newMessages);
            notifyItemInserted(positionStart);
        }
    }

    public void addMessagesToFront(List<Message> newMessages)
    {
        if(newMessages.size()>1)
        {
            for(int i=newMessages.size()-1;i>=0;i--)
            {
                mMessages.add(0,newMessages.get(i));
            }
            notifyItemRangeInserted(0, newMessages.size());
        }
        else
        {
            mMessages.add(0,newMessages.get(0));
            notifyItemInserted(0);
        }
    }

    public static String convertDate(String dateInMilliseconds)
    {
        return "      "+DateFormat.format("hh:mm", Long.parseLong(dateInMilliseconds)).toString();
    }
}