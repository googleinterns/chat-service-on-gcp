package com.example.chat;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import util.Message;

public class ViewMessageActivity extends AppCompatActivity
{

    public static final String CHAT_ID = "CHAT_ID";
    public static final String CONTACT_USERNAME = "CONTACT_USERNAME";
    public static final String LAST_MESSAGE_ID = "LAST_MESSAGE_ID";

    EditText messageEditText;
    MessageRecyclerAdapter messageRecyclerAdapter;
    private RecyclerView recyclerMessages;
    LinearLayoutManager messageLayoutManager;
    private String message_text;
    public int help_count;

    private String currentUser;

    private Timer mTimer;
//    int PICK_IMAGE_MULTIPLE = 1;
//    String imageEncoded;
//    List<String> imagesEncodedList;


    @Override
    protected void onPause()
    {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        mTimer.cancel();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        help_count=0;
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_view_message);


        messageEditText=(EditText)findViewById(R.id.send_message_text);

//        contactId = getIntent().getIntExtra(CONTACT_USERNAME,0);
        getCurrentUser();
        Objects.requireNonNull(getSupportActionBar()).setTitle(getIntent().getStringExtra(CONTACT_USERNAME));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeDisplayContent();

        final Button button = findViewById(R.id.send_message_button);
        button.setOnClickListener(new View.OnClickListener()
        {


            @Override
            public void onClick(View v)
            {
                message_text = messageEditText.getText().toString().trim();
                if(message_text.equals(""))
                {
                    hideSoftKeyboard();
                    return;
                }

//                ContentValues values = new ContentValues();
//                values.put(Messages.COLUMN_SENDER,currentUser);
//                values.put(Messages.COLUMN_RECEIVER,contactId);
//                values.put(Messages.COLUMN_RECEIVED,0);
//                values.put(Messages.COLUMN_TEXT, message_text);
//                values.put(Messages.COLUMN_SEND_TIME,System.currentTimeMillis());
//
//
//                ContentValues lastValues = new ContentValues();
//                lastValues.put(Chat.COLUMN_USER1,Math.min(currentUser,contactId));
//                lastValues.put(Chat.COLUMN_USER2,Math.max(currentUser,contactId));
//                lastValues.put(Chat.COLUMN_LAST_MESSAGE,message_text);
//
//
//
//                new SendMessageServer().execute(values);


                //This code helps in choosing an image
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);

            }
        });
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
        messageRecyclerAdapter = new MessageRecyclerAdapter(this,null);
        messageLayoutManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(messageLayoutManager);
        recyclerMessages.setAdapter(messageRecyclerAdapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        findViewById(R.id.view_message_constraint_layout).requestFocus();
        hideSoftKeyboard();
        ReceiveMessagePeriodically();



    }



    private void ReceiveMessagePeriodically()
    {
        final Handler handler = new Handler();
        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        new ReceiveMessageServer().execute();
                    }
                });
            }
        };
        mTimer.schedule(task, 0, 1000);//runs it every 1 second;
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private void addMessageToScreen()
    {
        hideSoftKeyboard();
        findViewById(R.id.view_message_constraint_layout).requestFocus();
//        new Message()
//        messageRecyclerAdapter.addRow(new Message(currentUser,contactId,false,message_text,System.currentTimeMillis()));
        recyclerMessages.smoothScrollToPosition(recyclerMessages.getAdapter().getItemCount()-1);
        messageEditText.setText("");
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

    private class SendMessageServer extends AsyncTask<ContentValues,Void,Void>
    {

        @Override
        protected Void doInBackground(ContentValues... contentValues)
        {
            // TODO send Message to the server
            return null;
        }
    }



    private class ReceiveMessageServer extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            //TODO receive Message from the server & store it in DB
            Log.d("hello","server_message "+Integer.toString(help_count));
            help_count++;

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            //TODO display it in the recyclerView (notifyDataSetChanged)
        }
    }

    //for picking images(not required now)
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        try {
//            // When an Image is picked
//            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
//                    &&  data!=null) {
//                // Get the Image from data
//                String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//                imagesEncodedList = new ArrayList<String>();
////                if(data.getData()!=null)
//
//
//                //Reference: https://stackoverflow.com/questions/19585815/select-multiple-images-from-android-gallery
//
//                if (data.getClipData() != null) {
//                    ClipData mClipData = data.getClipData();
//                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
//                    for (int i = 0; i < mClipData.getItemCount(); i++) {
//
//                        ClipData.Item item = mClipData.getItemAt(i);
//                        Uri uri = item.getUri();
//                        mArrayUri.add(uri);
//                        // Get the cursor
//                        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
//                        // Move to first row
//                        cursor.moveToFirst();
//
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        imageEncoded  = cursor.getString(columnIndex);
//                        Log.d("here2", "Selected Images " + imageEncoded);
//                        imagesEncodedList.add(imageEncoded);
//                        cursor.close();
//
//                    }
//                    Log.d("here", "Selected Images" + mArrayUri.size());
//                    for (String s : imagesEncodedList)
//                    {
//                        File imgFile = new File(s);
//                        if (imgFile.exists())
//                        {
//
////                            messages.add(new Message(false, s, 3));
////                            messageRecyclerAdapter.notifyItemInserted(messages.size() - 1);
////                            recyclerMessages.smoothScrollToPosition(messages.size() - 1);
//                        }
//                    }
//                }
//            } else {
//                Toast.makeText(this, "You haven't picked Image",
//                        Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
//                    .show();
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }

}