package com.gpayinterns.chat;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gpayinterns.chat.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.gpayinterns.chat.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.gpayinterns.chat.ServerConstants.BASE_URL;
import static com.gpayinterns.chat.ServerConstants.CHATS;
import static com.gpayinterns.chat.ServerConstants.END_MESSAGE;
import static com.gpayinterns.chat.ServerConstants.MESSAGES;
import static com.gpayinterns.chat.ServerConstants.USERS;

public class MessageRecyclerAdapter extends RecyclerView.Adapter <MessageRecyclerAdapter.ViewHolder>
{
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private List<Message> mMessages;
    private int mViewType;
    private String mPath;

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
        holder.mChatID = mMessages.get(position).chatID;
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
        public String mChatID;
        public String mMimeType;
        public ViewHolder(@NonNull final View itemView)
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
                        fileExists(mMessageID);
                        Uri messageURI = getUriForFile(mContext, "com.gpayinterns.fileprovider", new File (mPath));;
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(messageURI, mMimeType);
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
                        ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
                        ImageView done = (ImageView) itemView.findViewById(R.id.done);
                        if(fileExists(mMessageID))
                        {
                            Toast.makeText(mContext, "File is already downloaded", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            progressBar.setVisibility(View.VISIBLE);
                            getAttachmentFromServer(mChatID, mMessageID,mFileName.getText().toString(),progressBar,done);
                        }

                    }
                });

                viewButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(fileExists(mMessageID))
                        {
                            Uri messageURI = getUriForFile(mContext, "com.gpayinterns.fileprovider", new File (mPath));
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(messageURI, mMimeType);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            mContext.startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(mContext, "File not present in storage", Toast.LENGTH_SHORT).show();
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

    /**
     *
     * @param messageID
     * @return true if either the Uri leads to a valid file or the path obtained from cache is valid, else false
     */
    private boolean fileExists(String messageID)
    {
        String path = null;
        OpenHelper dbOpenHelper = new OpenHelper(mContext);
        path = DataManager.getFromDatabase(dbOpenHelper,messageID);
        dbOpenHelper.close();
        if(path == null)
        {
            return false;
        }
        mPath = path;
        File file = new File(path);
        return file.exists();
    }

    private void getAttachmentFromServer(String chatID, final String messageID, final String fileName, final ProgressBar progressBar,final ImageView done)
    {
        SharedPreferences mPrefs= mContext.getSharedPreferences("CHAT_LOGGED_IN_USER", 0);
        String currentUser = mPrefs.getString("currentUser","");

        String URL = BASE_URL + USERS +
                "/" + currentUser + "/" +
                CHATS + "/" + chatID + "/" +
                MESSAGES + "/" + messageID + "/attachments";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        progressBar.setVisibility(View.GONE);
                        Log.d("ResponseMessage:" , response.toString());
                        try
                        {
                            String base64String = response.getString("Blob");
                            storeFile(messageID,base64String,fileName);
                            done.setVisibility(View.VISIBLE);
                            new CountDownTimer(2000, 1000)
                            {
                                @Override
                                public void onTick(long millisUntilFinished)
                                {
                                }
                                @Override
                                public void onFinish()
                                {
                                    done.setVisibility(View.GONE);
                                }
                            }.start();
                        }
                        catch (JSONException | IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        // TODO: Handle error

                    }
                }){
            @Override
            public Priority getPriority()
            {
                return Priority.IMMEDIATE;
            }
        };

        VolleyController.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
    }

    private void storeFile(String messageID,String base, String fileName) throws IOException
    {
        String filePath = mContext.getFilesDir().getAbsolutePath() + "/" + fileName;
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(Base64.decode(base,Base64.NO_WRAP));
        fos.close();

        updateCache(messageID,filePath);
        Log.d("path","file saved to:"+filePath);
    }
    private void updateCache(String messageID, String filePath)
    {
        OpenHelper dbOpenHelper = new OpenHelper(mContext);
        DataManager.loadToDatabase(dbOpenHelper,messageID,filePath);
        dbOpenHelper.close();
    }
}