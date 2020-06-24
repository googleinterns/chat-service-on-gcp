package com.gpayinterns.chat;


import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    //mViewType 2: left side image
    //mViewType 3: right side image

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

        if(mMessages.get(position).image!=null)//image
        {
            Log.d("position","b"+position);
            mViewType += 2;
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
        switch(viewType)
        {
            case 0:
                itemView = mLayoutInflater.inflate(R.layout.item_recieve_message_list,parent,false);
                break;
            case 1:
                itemView = mLayoutInflater.inflate(R.layout.item_send_message_list,parent,false);
                break;
            case 2:
                itemView = mLayoutInflater.inflate(R.layout.item_recieve_image_list,parent,false);
                break;
            case 3:
                itemView = mLayoutInflater.inflate(R.layout.item_send_image_list,parent,false);
                break;
            default:
                itemView = null;
        }
        assert itemView != null;
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Log.d("position","a"+position);
        if(mViewType<=1)//text
        {
            holder.mMessage.setText(mMessages.get(position).text);
        }
        else//image
        {
            holder.mImage.setImageBitmap(mMessages.get(position).image);
        }
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
        public final ImageView mImage;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            if(mViewType%2==0)//received
            {
                mMessage = (TextView) itemView.findViewById(R.id.receive_message_text);
                mImage = (ImageView) itemView.findViewById(R.id.receive_message_image);
                mTime = (TextView) itemView.findViewById(R.id.time_receive_message_text);
            }
            else
            {
                mMessage = (TextView) itemView.findViewById(R.id.send_message_text);
                mImage = (ImageView) itemView.findViewById(R.id.send_message_image);
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
        return "      "+DateFormat.format("hh:mm a", Long.parseLong(dateInMilliseconds)).toString();
    }
}