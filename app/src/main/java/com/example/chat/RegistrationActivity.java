package com.example.chat;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.example.chat.DatabaseContract.chatEntry;
import com.example.chat.DatabaseContract.userEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;


import static util.BCrypt.hashPassword;


public class RegistrationActivity extends AppCompatActivity
{

    //Edittexts
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private volatile boolean newEntryInserted;
    private EditText confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_registration);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        nameEditText=findViewById(R.id.name_input);
        emailEditText=findViewById(R.id.email_input_register);
        passwordEditText =findViewById(R.id.password_input_register);
        confirmPasswordEditText =findViewById(R.id.confirm_password_input);
        newEntryInserted=false;


        //Button
        //Buttons
        Button registrationButton = (Button) findViewById(R.id.register_button);

        registrationButton.setOnClickListener(new View.OnClickListener() //add new user to firebase after someone clicks on this
        {
            @Override
            public void onClick(View view)
            {
                if(!validateForm())
                {
                    return ;
                }
                try
                {
                    addUserServer(emailEditText.getText().toString());
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
//        enableStrictMode();
    }
    private void enableStrictMode()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onPause()
    {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onPause();
    }


    private void addUserServer(String userName) throws JSONException
    {
        String URL = "https://gcp-chat-service.an.r.appspot.com/users";


        JSONObject jsonBody = new JSONObject();

        Log.d("username sent to server: ",userName);

        jsonBody.put("username", userName);


        final Long mRequestStartTime = System.currentTimeMillis();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                 (Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>()
                {


                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("hereResponseMessage: " , response.toString());
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTime;
                        Log.d("successLatencyTime: ",Long.toString(totalRequestTime));
                        try
                        {
                            String message = response.getString("message");
                            if(message.equals("Success"))
                            {
                                String userID = response.getString("UserID");

                                String name = nameEditText.getText().toString();
                                ContentValues values = new ContentValues();
                                values.put(userEntry.COLUMN_NAME, name);
                                values.put(userEntry.COLUMN_EMAIL_ID, emailEditText.getText().toString());
                                values.put(userEntry.COLUMN_LAST_MESSAGE, "");
                                values.put(userEntry.COLUMN_PASSWORD, hashPassword(passwordEditText.getText().toString()));
                                values.put(userEntry.COLUMN_SERVER_USER_ID, userID);


                                new addUserDb().execute(values);
                                //TODO chats were previously updated over here.
//                                if(newEntryInserted)
//                                {
//                                    new UpdateChats().execute(emailEditText.getText().toString());
//                                }
                                Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_LONG).show();
                                finish();
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
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTime;
                        Log.d("failureLatencyTime: ",Long.toString(totalRequestTime));


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
                            String message = data.optString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            emailEditText.setText("");
                            passwordEditText.setText("");
                            confirmPasswordEditText.setText("");
                            emailEditText.requestFocus();
                        }
                    }

                }) {
            @Override
            public Priority getPriority()
            {
                return Priority.IMMEDIATE;
            }
        };
        VolleyController.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public boolean validateForm()
    {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirm_password= confirmPasswordEditText.getText().toString();


        if (TextUtils.isEmpty(email))
        {
            emailEditText.setFocusable(true);
            Toast.makeText(getApplicationContext(), "Email ID should not be empty", Toast.LENGTH_SHORT).show();
            emailEditText.requestFocus();
            emailEditText.setText("");
            return false;
        }
        else if(LoginActivity.inValidEmailId(email))
        {
            emailEditText.setFocusable(true);
            Toast.makeText(getApplicationContext(), "Invalid Email ID", Toast.LENGTH_SHORT).show();
            emailEditText.setText("");
            emailEditText.requestFocus();
            return false;
        }


        if (TextUtils.isEmpty(password))
        {
            passwordEditText.setFocusable(true);
            Toast.makeText(getApplicationContext(), "Password should not be empty", Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            nameEditText.setFocusable(true);
            nameEditText.requestFocus();
            Toast.makeText(getApplicationContext(), "Name should not be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.equals(confirm_password))
        {
            passwordEditText.setFocusable(true);
            Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
            passwordEditText.setText("");
            confirmPasswordEditText.setText("");
            passwordEditText.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }




    private class addUserDb extends AsyncTask<ContentValues,Void,Void>
    {
        @Override
        protected Void doInBackground(ContentValues... contentValues)
        {
            Uri uri = getContentResolver().insert(ChatProviderContract.Users.CONTENT_URI,contentValues[0]);
            if(uri==null)
                newEntryInserted=false;
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }
    }

    private class UpdateChats extends AsyncTask<String,Void,Void>
    {
        @Override
        protected Void doInBackground(String... email)
        {
            Uri uri = ChatProviderContract.Users.CONTENT_URI;
            String emailId=email[0];

            if(!newEntryInserted)
                return null;

            Cursor cursor1 = getContentResolver().query(uri,new String[]{"_id"}," email_id = ? ",new String[]{emailId},null);
            if(cursor1==null)
                return null;
            cursor1.moveToFirst();
            int userId1=cursor1.getInt(cursor1.getColumnIndex("_id"));
            Log.d("userID1",""+userId1);
            cursor1.close();

            String[] user_columns =
                    {
                            BaseColumns._ID,
                            userEntry.COLUMN_NAME,
                            userEntry.COLUMN_EMAIL_ID,
                            userEntry.COLUMN_LAST_MESSAGE
                    };
            String selection = userEntry._ID + " != ? ";
            String selectionArgs[]=
                    {
                            Integer.toString(userId1)
                    };
            Cursor cursor = getContentResolver().query(uri,user_columns,selection,selectionArgs,null);

            if(cursor==null)
                return null;


            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                int userId2=cursor.getInt(cursor.getColumnIndex("_id"));
                ContentValues values = new ContentValues();
                values.put(chatEntry.COLUMN_USER1,userId2);
                values.put(chatEntry.COLUMN_USER2,userId1);
                values.put(chatEntry.COLUMN_LAST_MESSAGE,"");

                getContentResolver().insert(ChatProviderContract.Chat.CONTENT_URI,values);
            }

            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }
    }


}
