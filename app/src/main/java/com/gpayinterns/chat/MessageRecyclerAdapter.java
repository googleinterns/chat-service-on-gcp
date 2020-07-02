package com.gpayinterns.chat;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gpayinterns.chat.R;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gpayinterns.chat.Message;

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
    //mViewType 4: left side any richText except image
    //mViewType 5: right side any richText except image

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
            mViewType += 2;
        }
        else if(mMessages.get(position).mimeType!=null)//richText
        {
            mViewType += 4;
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
            case 4:
                itemView = mLayoutInflater.inflate(R.layout.item_recieve_richtext_list,parent,false);
                break;
            case 5:
                itemView = mLayoutInflater.inflate(R.layout.item_send_richtext_list,parent,false);
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
        holder.mMessageID = mMessages.get(position).messageID;
        holder.mMessageURI = mMessages.get(position).uri;
        holder.mMimeType = mMessages.get(position).mimeType;
        if(mViewType<=1)//text
        {
            holder.mMessage.setText(mMessages.get(position).text);
        }
        else if(mViewType<=3)//image
        {
            holder.mImage.setImageBitmap(mMessages.get(position).image);
        }
        else//richText
        {
            holder.mFileName.setText(mMessages.get(position).fileName);
            holder.mFileSize.setText(mMessages.get(position).fileSize);
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
        public final TextView mFileName;
        public final TextView mFileSize;
        public String mMessageID;
        public Uri mMessageURI;
        public String mMimeType;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            if(mViewType%2==0)//received
            {
                mMessage = (TextView) itemView.findViewById(R.id.receive_message_text);
                mImage = (ImageView) itemView.findViewById(R.id.receive_message_image);
                mTime = (TextView) itemView.findViewById(R.id.time_receive_message_text);
                mFileName = (TextView) itemView.findViewById(R.id.receive_file_name);
                mFileSize = (TextView) itemView.findViewById(R.id.receive_file_size);
            }
            else
            {
                mMessage = (TextView) itemView.findViewById(R.id.send_message_text);
                mImage = (ImageView) itemView.findViewById(R.id.send_message_image);
                mTime = (TextView) itemView.findViewById(R.id.time_send_message_text);
                mFileName = (TextView) itemView.findViewById(R.id.send_file_name);
                mFileSize = (TextView) itemView.findViewById(R.id.send_file_size);
            }
            if(mViewType==2 || mViewType==3)//image
            {
                itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(mMessageURI, mMimeType);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        mContext.startActivity(intent);
                    }
                });
            }
            else if(mViewType==4 || mViewType==5)//richText
            {
                Button downloadButton = (Button) itemView.findViewById(R.id.download_button);
                Button viewButton = (Button) itemView.findViewById(R.id.view_button);

                downloadButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        /**
                         * TODO
                         * download via getAttachment API
                         * convert it into bitmap
                         * set image in the message
                         * store into device storage
                         * set uriType in message
                          **/
                    }
                });

                viewButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(!uriExists(mMessageURI))
                        {
                            Toast.makeText(mContext, "File is not present in storage", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(mMessageURI, mMimeType);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            mContext.startActivity(intent);
                        }
                    }
                });
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

    private boolean uriExists(Uri uri)
    {
        if(uri == null)
        {
            return false;
        }
        File file = new File(Objects.requireNonNull(uri.getPath()));
        return file.exists();
    }
}