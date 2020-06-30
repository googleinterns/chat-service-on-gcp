package com.gpayinterns.chat;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gpayinterns.chat.R;

import java.util.List;

import androidx.annotation.NonNull;
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

    public NewMessageRecyclerAdapter(Context context, List<String> messages)
    {
        mContext = context;
        mMessages = messages;
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
