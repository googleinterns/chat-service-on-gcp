package com.example.chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewMessageActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText messageEditText;
    NewMessageRecyclerAdapter messageRecyclerAdapter;
    private RecyclerView recyclerMessages;
    LinearLayoutManager messageLayoutManager;
    private volatile Integer contactId;
    private volatile int currentUser;
    private List<String> messages = new ArrayList<String>();
    private static final int CONTACT_PICKER_RESULT = 1;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;



    @Override
    protected void onPause()
    {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        messageEditText=(EditText)findViewById(R.id.send_new_message_text);
        findViewById(R.id.send_new_message_button).setOnClickListener(this);
        findViewById(R.id.view_contacts_button).setOnClickListener(this);


        initializeDisplayContent();
        getCurrentUser();
        contactId=null;

    }


    private void initializeDisplayContent()
    {
        recyclerMessages = (RecyclerView) findViewById(R.id.new_message_recyclerView);
        messageLayoutManager = new LinearLayoutManager(this);
        messageRecyclerAdapter = new NewMessageRecyclerAdapter(this,messages);
        recyclerMessages.setLayoutManager(messageLayoutManager);
        recyclerMessages.setAdapter(messageRecyclerAdapter);
    }


    private void getCurrentUser()
    {
        SharedPreferences mPrefs= getSharedPreferences("CHAT_LOGGED_IN_USER", 0);
        currentUser = mPrefs.getInt("currentUser",-1);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.view_contacts_button)
        {
            ((EditText)findViewById(R.id.new_message_username)).setText("");
            requestContactPermission();
        }
        else if(v.getId() == R.id.send_new_message_button)
        {
            sendNewMessage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if(requestCode == CONTACT_PICKER_RESULT)
            {
                Cursor cursor = null;
                String email = "", name = "";
                try
                {
                    assert data != null;
                    Uri result = data.getData();
                    Log.v("NewMessageActivity", "Got a contact result: " + result.toString());

                    String id = ((Uri) result).getLastPathSegment();

                    cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,  null, ContactsContract.CommonDataKinds.Email.CONTACT_ID+" =? ", new String[] {id}, null);

                    assert cursor != null;
                    int nameId = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

                    int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

                    if (cursor.moveToFirst())
                    {
                        email = cursor.getString(emailIdx);
                        name = cursor.getString(nameId);
                        Log.v("NewMessageActivity", "Got email: " + email);
                    }
                    else
                    {
                        Log.w("NewMessageActivity", "No results");
                    }
                }
                catch (Exception e)
                {
                    Log.e("NewMessageActivity", "Failed to get email data", e);
                }
                finally
                {
                    if (cursor != null)
                    {
                        cursor.close();
                    }
                    EditText emailEntry = (EditText) findViewById(R.id.new_message_username);
                    Log.d("NewMessageActivity",email);
                    Log.d("NewMessageActivity",name);
                    emailEntry.setText(email);
                    if (email.length() == 0 && name.length() == 0)
                    {
                        Toast.makeText(this, "No Email found for selected contact",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        else
        {
            Log.d("NewMessageActivity", "Warning: Activity result not ok");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendNewMessage()
    {
        String message_text1 = messageEditText.getText().toString().trim();
        if(message_text1.equals(""))
        {
            HideSoftKeyboard();
            return;
        }
        getContactId();
        if(contactId==null)
        {
            Toast.makeText(this, "Enter a valid username", Toast.LENGTH_LONG).show();
            return ;
        }

        String message_text=messageEditText.getText().toString();



        messages.add(message_text);
        messageRecyclerAdapter.notifyItemInserted(messages.size()-1);
        recyclerMessages.smoothScrollToPosition(messages.size()-1);
        messageEditText.setText("");
        HideSoftKeyboard();
    }

    private void getContactId()
    {
        //TODO call an API with which I can get "server userID" corresponding to the "username" provided & then add it to the cache


    }


    private void HideSoftKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void requestContactPermission()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_CONTACTS))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Read Contacts permission");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage("Please enable access to contacts.");
                builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        requestPermissions(
                                new String[]
                                        {android.Manifest.permission.READ_CONTACTS}
                                , PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                });
                builder.show();
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        else
        {
            getContacts();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if(requestCode == PERMISSIONS_REQUEST_READ_CONTACTS)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getContacts();
            }
            else
            {
                Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void getContacts()
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }
}