package com.gpayinterns.chat;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

import static com.gpayinterns.chat.ServerHelper.BASE_URL;
import static com.gpayinterns.chat.ServerHelper.SIGNUP;
import static com.gpayinterns.chat.ServerHelper.USER_EMAIL;
import static com.gpayinterns.chat.ServerHelper.USER_MOBILE;
import static com.gpayinterns.chat.ServerHelper.USER_PASSWORD;
import static com.gpayinterns.chat.ServerHelper.USER_USERNAME;


public class RegistrationActivity extends AppCompatActivity
{
    //Edittexts
    private EditText usernameEditText;
    private EditText phoneNumEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private long mLastClickTime = 0;
    private static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_registration);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        usernameEditText=findViewById(R.id.name_input);
        phoneNumEditText=findViewById(R.id.phone_num_input_register);
        emailEditText=findViewById(R.id.email_input_register);
        passwordEditText =findViewById(R.id.password_input_register);
        confirmPasswordEditText =findViewById(R.id.confirm_password_input);

        //Button
        Button registrationButton = (Button) findViewById(R.id.register_button);

        registrationButton.setOnClickListener(new View.OnClickListener() //add new User to firebase after someone clicks on this
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
                    //wait for 5 seconds before sending another registration request
                    if (System.currentTimeMillis() - mLastClickTime < 5000)
                    {
                        return;
                    }
                    mLastClickTime = System.currentTimeMillis();

                    addUserServer();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onPause()
    {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        active=false;
        super.onPause();
    }


    @Override
    protected void onResume()
    {
        active=true;
        super.onResume();
    }

    private void addUserServer() throws JSONException
    {
        String userName = usernameEditText.getText().toString();
        String emailID = emailEditText.getText().toString();
        String phoneNum = phoneNumEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String URL = BASE_URL + SIGNUP;

        JSONObject jsonBody = new JSONObject();
        jsonBody.put(USER_USERNAME, userName);
        jsonBody.put(USER_EMAIL, emailID);
        jsonBody.put(USER_MOBILE,phoneNum);
        jsonBody.put(USER_PASSWORD,password);

        final Long mRequestStartTime = System.currentTimeMillis();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                 (Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("ResponseMessage: " , response.toString());
                        long totalRequestTime = System.currentTimeMillis() - mRequestStartTime;
                        Log.d("successLatencyTime: ",Long.toString(totalRequestTime));
                        try
                        {
                            String message = response.getString("message");
                            if(message.equals("Success"))
                            {
                                String userID = response.getString("UserId");
                                storeInSharedPreferences(userID);
                                Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_LONG).show();
                                if(active)
                                {
                                    startActivity(new Intent(RegistrationActivity.this,ViewContactsActivity.class));
                                    finish();
                                }
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
                            if(active)
                            {
                                usernameEditText.setText("");
                                emailEditText.setText("");
                                passwordEditText.setText("");
                                confirmPasswordEditText.setText("");
                                emailEditText.requestFocus();
                            }
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

    private void storeInSharedPreferences(String userID)
    {
        SharedPreferences mPrefs = getSharedPreferences("CHAT_LOGGED_IN_USER", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("currentUser", userID).apply();
    }

    public boolean validateForm()
    {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirm_password = confirmPasswordEditText.getText().toString();
        String username = usernameEditText.getText().toString();
        String phoneNum = phoneNumEditText.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            emailEditText.setFocusable(true);
            Toast.makeText(getApplicationContext(), "Email ID should not be empty", Toast.LENGTH_SHORT).show();
            emailEditText.requestFocus();
            emailEditText.setText("");
            return false;
        }
        else if(inValidEmailId(email))
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
        if(TextUtils.isEmpty(username))
        {
            usernameEditText.setFocusable(true);
            usernameEditText.requestFocus();
            Toast.makeText(getApplicationContext(), "Enter a Username", Toast.LENGTH_SHORT).show();
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
        if(TextUtils.isEmpty(phoneNum))
        {
            phoneNumEditText.setFocusable(true);
            phoneNumEditText.requestFocus();
            Toast.makeText(getApplicationContext(), "Enter a phone number", Toast.LENGTH_SHORT).show();
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

    public static boolean inValidEmailId(String email)
    {
        return !Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }
}
