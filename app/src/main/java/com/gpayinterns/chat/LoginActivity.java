package com.gpayinterns.chat;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.gpayinterns.chat.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

import static com.gpayinterns.chat.ServerHelper.BASE_URL;
import static com.gpayinterns.chat.ServerHelper.LOGIN;
import static com.gpayinterns.chat.ServerHelper.USER_PASSWORD;
import static com.gpayinterns.chat.ServerHelper.USER_USERNAME;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText usernameEditText;
    private EditText passwordEditText;
    private String currentUser;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";
    private static final String EMAIL = "email";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored)
        {
        }

        setContentView(R.layout.activity_login);


        //Views
        usernameEditText = findViewById(R.id.input_email_id);
        passwordEditText = findViewById(R.id.input_password);
        //Buttons
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        findViewById(R.id.new_user_register).setOnClickListener(this);
        findViewById(R.id.forgot_password).setOnClickListener(this);

        googleLogin();
    }

    private void googleLogin()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
    }


    private void getCurrentUser()
    {
        SharedPreferences mPrefs = getSharedPreferences("CHAT_LOGGED_IN_USER", 0);
        currentUser = mPrefs.getString("currentUser","");
        if (!currentUser.equals(""))
        {
            startActivity(new Intent(LoginActivity.this, ViewContactsActivity.class));
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        getCurrentUser();
        super.onResume();
    }




    public boolean validateForm()
    {
        boolean valid = true;

        String username = usernameEditText.getText().toString();
        if (TextUtils.isEmpty(username))
        {
            usernameEditText.setError("Required.");
            valid = false;
        }
        else
        {
            usernameEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password))
        {
            passwordEditText.setError("Required.");
            valid = false;
        }
        else
        {
            passwordEditText.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.new_user_register:
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                break;
            case R.id.login_button:
                try
                {
                    signIn();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
            case R.id.google_sign_in_button:
                signInWithGoogle();
                break;
        }
    }

    private void signInWithGoogle()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void shakeLoginButton()
    {
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
    }

    private void signIn() throws JSONException
    {

        if (!validateForm())
        {
            shakeLoginButton();
            return;
        }

        authenticateFromServer();
    }

    private void setCurrentUser()
    {
        SharedPreferences mPrefs = getSharedPreferences("CHAT_LOGGED_IN_USER", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("currentUser", currentUser).apply();
    }


    private void authenticateFromServer() throws JSONException
    {
        String userName = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        String URL = BASE_URL + LOGIN;


        JSONObject jsonBody = new JSONObject();


        jsonBody.put(USER_USERNAME, userName);
        jsonBody.put(USER_PASSWORD,password);


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
                                currentUser = response.getString("UserId");
                                setCurrentUser();
                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this,ViewContactsActivity.class));
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
                            String message = data.optString("message");

                            shakeLoginButton();
                            Toast.makeText(getBaseContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            usernameEditText.setText("");
                            passwordEditText.setText("");
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



    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        else
        {
            Toast.makeText(getBaseContext(), "Tap back once again to exit the application", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();

    }

    @Override
    public void onStart()
    {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null)
        {

            //TODO login after sending tokenID to the backend server.
            String Email = account.getDisplayName();
            Log.d("userEmail: ",Email);
        }
    }

    @Override
    public void onActivityResult ( int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult (Task < GoogleSignInAccount > completedTask)
    {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("LoginActivity", "Signed In with Google");
        }
        catch (ApiException e)
        {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
}