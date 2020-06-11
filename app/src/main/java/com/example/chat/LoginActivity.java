package com.example.chat;


import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chat.ChatProviderContract.Users;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static util.BCrypt.checkPassword;
import static util.BCrypt.hashPassword;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText mEmailField;
    private EditText mPasswordField;
    private volatile String stored_hash;
    private volatile boolean received;
    private int current_user;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";
    private static final String EMAIL = "email";
    private CallbackManager callbackManager;
    private volatile boolean addedToDb;
    private volatile boolean rowInserted;
    private LoginButton fbLoginButton;
    private static int RC_FB_SIGN_IN;

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
        mEmailField = findViewById(R.id.input_email_id);
        mPasswordField = findViewById(R.id.input_password);
        //Buttons
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        findViewById(R.id.new_user_register).setOnClickListener(this);
        findViewById(R.id.forgot_password).setOnClickListener(this);
        findViewById(R.id.facebook_login_linear_layout).setOnClickListener(this);

        googleLogin();
        facebookLogin();

//        enableStrictMode();
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

    private void facebookLogin()
    {
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        fbLoginButton.setPermissions(Arrays.asList(EMAIL));
        RC_FB_SIGN_IN = fbLoginButton.getRequestCode();
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                // App code

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback()
                        {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response)
                            {

                                try
                                {
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                    Log.d("here1", email);
                                    Log.d("here1", name);
                                    //check if the email already exists else create a new account
                                    addedToDb = false;
                                    ContentValues values = new ContentValues();
                                    values.put(DatabaseContract.userEntry.COLUMN_NAME, name);
                                    values.put(DatabaseContract.userEntry.COLUMN_EMAIL_ID, email);
                                    values.put(DatabaseContract.userEntry.COLUMN_LAST_MESSAGE, "");
                                    values.put(DatabaseContract.userEntry.COLUMN_PASSWORD, "");
                                    new AddUserDb().execute(values);
                                    while (!addedToDb) ;
                                    if (rowInserted)
                                    {
                                        new UpdateChats().execute(email);
                                    }
                                    setCurrentUser();
                                    Log.d("here current_user:", Integer.toString(current_user));
                                    if (current_user >= 0)
                                    {
                                        startActivity(new Intent(LoginActivity.this, ViewContactsActivity.class));
                                    }
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel()
            {
                // App code
            }

            @Override
            public void onError(FacebookException exception)
            {
                // App code
            }
        });
    }

    private void enableStrictMode()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);
    }

    private void getCurrentUser()
    {
        SharedPreferences mPrefs = getSharedPreferences("CHAT_LOGGED_IN_USER", 0);
        current_user = mPrefs.getInt("currentUser", -1);
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
        checkCurrentUser();
        super.onResume();
    }

    private void checkCurrentUser()
    {
        if (current_user >= 0)
        {
            startActivity(new Intent(LoginActivity.this, ViewContactsActivity.class));
        }
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


    public boolean validateForm()
    {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email))
        {
            mEmailField.setError("Required.");
            valid = false;
        }
        else if (inValidEmailId(email))
        {
            mEmailField.setError("Invalid Email.");
            valid = false;
        }
        else
        {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password))
        {
            mPasswordField.setError("Required.");
            valid = false;
        }
        else
        {
            mPasswordField.setError(null);
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
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.google_sign_in_button:
                signInWithGoogle();
                break;
            case R.id.facebook_login_linear_layout:
                fbLoginButton.performClick();
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

    private void signIn(final String email, String password)
    {

        if (!validateForm())
        {
            shakeLoginButton();
            return;
        }

        if (authenticate(email, password))
        {
            setCurrentUser();//correct
            startActivity(new Intent(LoginActivity.this, ViewContactsActivity.class));
        }
        else
        {
            shakeLoginButton();
            Toast.makeText(getBaseContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
        }

        mEmailField.getText().clear();
        mPasswordField.getText().clear();
    }

    private void setCurrentUser()
    {
        SharedPreferences mPrefs = getSharedPreferences("CHAT_LOGGED_IN_USER", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putInt("currentUser", current_user).apply();
    }

    private boolean authenticate(String email, String password)
    {
        received = false;
        new ReceivePasswordDb().execute(email);
        while (!received) ;
        if (stored_hash == null || stored_hash.equals(""))
            return false;

        return checkPassword(password, stored_hash);

    }

    private class ReceivePasswordDb extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... emailID)
        {
            String whereClause = Users.COLUMN_EMAIL_ID + " = ? ";
            Cursor cursor = getContentResolver().query(Users.CONTENT_URI, new String[]{Users.COLUMN_PASSWORD, Users._ID}, whereClause, emailID, null);
            if (cursor == null || cursor.getCount() == 0)
            {
                if (!cursor.isClosed())
                    cursor.close();
                received = true;
                return null;
            }
            cursor.moveToFirst();

            stored_hash = cursor.getString(cursor.getColumnIndex(Users.COLUMN_PASSWORD));
            current_user = cursor.getInt(cursor.getColumnIndex(Users._ID));

            cursor.close();

            received = true;
            return null;
        }
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
        else if (requestCode == RC_FB_SIGN_IN)
        {
            Log.d("LoginActivity", "received an FB request");
            callbackManager.onActivityResult(requestCode, resultCode, data);
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


    private class AddUserDb extends AsyncTask<ContentValues, Void, Void>
    {
        @Override
        protected Void doInBackground(ContentValues... contentValues)
        {
            Uri uri = getContentResolver().insert(ChatProviderContract.Users.CONTENT_URI, contentValues[0]);
            rowInserted = (uri != null);
            if (uri != null)
            {
                current_user = (Long.valueOf(Objects.requireNonNull(uri.getLastPathSegment()))).intValue();
            }
            else
            {
                Uri uri1 = ChatProviderContract.Users.CONTENT_URI;
                String emailId = contentValues[0].getAsString("email_id");
                Log.d("Email: ", emailId);

                Cursor cursor1 = getContentResolver().query(uri1, new String[]{"_id"}, " email_id = ? ", new String[]{emailId}, null);
                cursor1.moveToFirst();
                current_user = cursor1.getInt(cursor1.getColumnIndex("_id"));
            }
            addedToDb = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }
    }
    private class UpdateChats extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... email)
        {
            Uri uri = ChatProviderContract.Users.CONTENT_URI;
            String emailId = email[0];

            while (!addedToDb);
            if (!rowInserted)
                return null;

            Cursor cursor1 = getContentResolver().query(uri, new String[]{"_id"}, " email_id = ? ", new String[]{emailId}, null);
            if (cursor1 == null)
                return null;
            cursor1.moveToFirst();
            int userId1 = cursor1.getInt(cursor1.getColumnIndex("_id"));
            Log.d("userID1", "" + userId1);
            cursor1.close();

            String[] user_columns =
                    {
                            BaseColumns._ID,
                            DatabaseContract.userEntry.COLUMN_NAME,
                            DatabaseContract.userEntry.COLUMN_EMAIL_ID,
                            DatabaseContract.userEntry.COLUMN_LAST_MESSAGE
                    };
            String selection = DatabaseContract.userEntry._ID + " != ? ";
            String selectionArgs[] =
                    {
                            Integer.toString(userId1)
                    };
            Cursor cursor = getContentResolver().query(uri, user_columns, selection, selectionArgs, null);

            if (cursor == null)
                return null;


            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                int userId2 = cursor.getInt(cursor.getColumnIndex("_id"));
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.chatEntry.COLUMN_USER1, userId2);
                values.put(DatabaseContract.chatEntry.COLUMN_USER2, userId1);
                values.put(DatabaseContract.chatEntry.COLUMN_LAST_MESSAGE, "");

                getContentResolver().insert(ChatProviderContract.Chat.CONTENT_URI, values);
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