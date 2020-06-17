package com.gpayinterns.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.ClientError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gpayinterns.chat.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import util.Message;

public class ViewMessageActivity extends AppCompatActivity
{

    public static final String CHAT_ID = "CHAT_ID";
    public static final String CONTACT_USERNAME = "CONTACT_USERNAME";
    public static final String LAST_MESSAGE_ID = "LAST_MESSAGE_ID";

    private List<Message> messages = new ArrayList<Message>();
    Set<String> messageIDSet = new HashSet<String>();//This helps to prevent duplicate messages.

    EditText messageEditText;
    MessageRecyclerAdapter messageRecyclerAdapter;
    private RecyclerView recyclerMessages;
    LinearLayoutManager messageLayoutManager;

    private String currentUser;
    private String chatID;
    private String lastMessageID;

    private Timer mTimer;


    @Override
    protected void onPause()
    {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        if(mTimer != null)
            mTimer.cancel();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_view_message);


        getCurrentUser();
        messageEditText=(EditText)findViewById(R.id.send_message_text);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getIntent().getStringExtra(CONTACT_USERNAME));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeDisplayContent();

        final Button button = findViewById(R.id.send_message_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String messageText = messageEditText.getText().toString().trim();
                if(messageText.equals(""))
                {
                    hideSoftKeyboard();
                    return;
                }
                try
                {
                    sendMessageToServer(messageText);
                    messageEditText.setText("");
                    hideSoftKeyboard();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendMessageToServer(String messageText) throws JSONException
    {
        String URL = "https://gcp-chat-service.an.r.appspot.com/users/"
                + currentUser + "/chats/"+chatID+"/messages";


        JSONObject jsonBody = new JSONObject();

        Log.d("messageText sent to server: ",messageText);

        jsonBody.put("contentType", "text");
        jsonBody.put("textContent",messageText);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>()
                {


                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("ResponseMessage: " , response.toString());
                        try
                        {
                            String message = response.getString("message");
                            if(message.equals("Success"))
                            {
                                String lastMessageID = response.getString("MessageId");
//                                addMessageToScreen(lastMessageID);
                            }
                        }
                        catch (JSONException e)
                        {
                            Toast.makeText(getApplicationContext(), "Parse Error", Toast.LENGTH_SHORT).show();
                            Log.d("JsonError: ",e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("errorMessage",error.toString());
                        if (error instanceof TimeoutError || error instanceof NoConnectionError)
                        {
                            Toast.makeText(getApplicationContext(), "Network timeout", Toast.LENGTH_LONG).show();
                        }
                        else if(error instanceof ClientError)
                        {
                            String responseBody = null;
                            responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            JSONObject data = null;
                            try
                            {
                                data = new JSONObject(responseBody);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }

                            assert data != null;
                            String message = data.optString("Message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        {
            @Override
            public Priority getPriority()
            {
                return Priority.IMMEDIATE;
            }
        };
        VolleyController.getInstance(this).addToRequestQueueWithRetry(jsonObjectRequest);
    }

    private void getCurrentUser()
    {
        SharedPreferences mPrefs= getSharedPreferences("CHAT_LOGGED_IN_USER", 0);
        currentUser = mPrefs.getString("currentUser","");
    }



    private void initializeDisplayContent()
    {
        recyclerMessages = (RecyclerView) findViewById(R.id.message_recyclerView);
        messageLayoutManager = new LinearLayoutManager(this);
        messageRecyclerAdapter = new MessageRecyclerAdapter(this,messages);
        messageLayoutManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(messageLayoutManager);
        recyclerMessages.setAdapter(messageRecyclerAdapter);
        recyclerMessages.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(-1))//-1 implies up
                {
                    //User has hit top of view
                    if(!messages.isEmpty())
                        receivePreviousMessagesFromServer();
                }
            }
        });
    }

    private void receivePreviousMessagesFromServer()
    {
        String URL = "https://gcp-chat-service.an.r.appspot.com/users/"+ currentUser +"/chats/"+
                chatID + "/messages?endMessageId="+messages.get(0).messageID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("ResponseMessage: " , response.toString());
                        try
                        {
                            JSONArray messageList = response.getJSONArray("payload");
                            List <Message> newMessages = new ArrayList<Message>();
                            for(int i=0;i<messageList.length();i++)
                            {
                                JSONObject message = (JSONObject) messageList.get(i);

                                String messageID = message.getString("MessageId");
                                String chatID = message.getString("ChatId");
                                boolean received = !message.getBoolean("SentByCurrentUser");
                                String text = message.getString("TextContent");
                                if(!messageIDSet.contains(messageID))
                                {
                                    newMessages.add(new Message(messageID,chatID,received,text,(long)0));
                                    messageIDSet.add(messageID);
                                }
                            }
                            if(newMessages.size()>0)
                            {
                                messageRecyclerAdapter.addMessagesToFront(newMessages);
                            }
                        }
                        catch (JSONException e)
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

        VolleyController.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        findViewById(R.id.view_message_constraint_layout).requestFocus();
        chatID = getIntent().getStringExtra(CHAT_ID);
        lastMessageID = getIntent().getStringExtra(LAST_MESSAGE_ID);

        messageIDSet.clear();
        messages.clear();

        hideSoftKeyboard();
        firstReceiveMessageFromServer();

    }



    private void ReceiveMessagePeriodically()
    {
        final Handler handler = new Handler();
        mTimer = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        receiveMessageFromServer();
                    }
                });
            }
        };
        mTimer.schedule(task, 0, 5000);//runs it every 5 seconds;
    }

    private void firstReceiveMessageFromServer()
    {
        String URL = "https://gcp-chat-service.an.r.appspot.com/users/"+ currentUser +"/chats/"+
                chatID + "/messages?endMessageId="+lastMessageID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("ResponseMessage: " , response.toString());
                        try
                        {
                            JSONArray messageList = response.getJSONArray("payload");
                            List <Message> newMessages = new ArrayList<Message>();
                            for(int i=0;i<messageList.length();i++)
                            {
                                JSONObject message = (JSONObject) messageList.get(i);

                                String messageID = message.getString("MessageId");
                                String chatID = message.getString("ChatId");
                                boolean received = !message.getBoolean("SentByCurrentUser");
                                String text = message.getString("TextContent");
                                if(!messageIDSet.contains(messageID))
                                {
                                    newMessages.add(new Message(messageID,chatID,received,text,(long)0));
                                    messageIDSet.add(messageID);
                                }
                            }
                            if(newMessages.size()>0)
                            {
                                lastMessageID = newMessages.get(newMessages.size()-1).messageID;
                                messageRecyclerAdapter.addMessages(newMessages);
                                recyclerMessages.smoothScrollToPosition(recyclerMessages.getAdapter().getItemCount()-1);
                            }
                            ReceiveMessagePeriodically();
                        }
                        catch (JSONException e)
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

        VolleyController.getInstance(this).addToRequestQueueWithRetry(jsonObjectRequest);

    }

    private void receiveMessageFromServer()
    {
        Log.d("currentUser",currentUser);
        Log.d("chatID:",chatID);
        Log.d("lastMessageID:",lastMessageID);



        String URL = "https://gcp-chat-service.an.r.appspot.com/users/"+ currentUser +"/chats/"+
                chatID + "/messages?startMessageId="+lastMessageID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("ResponseMessage: " , response.toString());
                        try
                        {
                            JSONArray messageList = response.getJSONArray("payload");
                            List <Message> newMessages = new ArrayList<Message>();
                            for(int i=0;i<messageList.length();i++)
                            {
                                JSONObject message = (JSONObject) messageList.get(i);

                                String messageID = message.getString("MessageId");
                                String chatID = message.getString("ChatId");
                                boolean received = !message.getBoolean("SentByCurrentUser");
                                String text = message.getString("TextContent");
                                if(!messageIDSet.contains(messageID))
                                {
                                    newMessages.add(new Message(messageID,chatID,received,text,(long)0));
                                    messageIDSet.add(messageID);
                                }

                                if(i==messageList.length()-1)
                                {
                                    lastMessageID = messageID;
                                }
                            }
                            if(newMessages.size()>0)
                            {
                                lastMessageID = newMessages.get(newMessages.size()-1).messageID;
                                messageRecyclerAdapter.addMessages(newMessages);
                                recyclerMessages.smoothScrollToPosition(recyclerMessages.getAdapter().getItemCount()-1);
                            }
                        }
                        catch (JSONException e)
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
                return Priority.LOW;
            }
        };

        VolleyController.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private void addMessageToScreen(String messageID)
    {
        messageIDSet.add(messageID);
        lastMessageID = messageID;
        findViewById(R.id.view_message_constraint_layout).requestFocus();
        List <Message> newMessage = new ArrayList<Message>();
        newMessage.add(new Message(messageID,chatID,false,messageEditText.getText().toString(),(long)0));
        Log.d("here",Integer.toString(newMessage.size()));
        messageRecyclerAdapter.addMessages(newMessage);
        recyclerMessages.smoothScrollToPosition(recyclerMessages.getAdapter().getItemCount()-1);

    }


    private void hideSoftKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}