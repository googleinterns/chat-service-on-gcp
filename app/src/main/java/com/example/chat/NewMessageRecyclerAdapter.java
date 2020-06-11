package com.example.chat;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class NewMessageRecyclerAdapter extends RecyclerView.Adapter <NewMessageRecyclerAdapter.ViewHolder>
{
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private List<String> mMessages;



    @Override
    public int getItemViewType(int position)
    {
        return 1;
    }

    public NewMessageRecyclerAdapter(Context Context, List<String> Messages)
    {
        mContext = Context;
        mMessages=Messages;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View itemView = mLayoutInflater.inflate(R.layout.item_send_message_list,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.mMessage.setText(mMessages.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView mMessage;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mMessage = (TextView) itemView.findViewById(R.id.send_message_text);
        }
    }


}