package com.example.chat;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chat.ChatProviderContract.Users;

import java.util.Objects;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

import static util.BCrypt.checkPassword;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText mEmailField;
    private EditText mPasswordField;
    private volatile String stored_hash;
    private volatile boolean received;
    private volatile int current_user;
    private volatile boolean currentUserUpdated;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}

        setContentView(R.layout.activity_login);


        //Views
        mEmailField=findViewById(R.id.input_email_id);
        mPasswordField=findViewById(R.id.input_password);
        //Buttons
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.new_user_register).setOnClickListener(this);
        findViewById(R.id.forgot_password).setOnClickListener(this);

        enableStrictMode();
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
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                SharedPreferences mPrefs= getSharedPreferences("CHAT_LOGGED_IN_USER", 0);
                current_user = mPrefs.getInt("currentUser",-1);
                currentUserUpdated = true;
                return null;
            }
        };
        task.execute();
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
        while(!currentUserUpdated);
        if(current_user>=0)
        {
            startActivity(new Intent(LoginActivity.this,ViewContactsActivity.class));
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
        else if(inValidEmailId(email))
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
        int i = v.getId();
        if(i==R.id.new_user_register)
        {
            startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
        }
        if (i == R.id.login_button)
        {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }

    private void shakeLoginButton()
    {
        Button loginButton = (Button)findViewById(R.id.login_button);
        loginButton.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake));
    }
    private void signIn(final String email, String password)
    {

        if (!validateForm())
        {
            shakeLoginButton();
            return;
        }

        if(authenticate(email,password))
        {
            setCurrentUser();//correct
            startActivity(new Intent(LoginActivity.this,ViewContactsActivity.class));
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
        mEditor.putInt("currentUser",current_user).apply();
    }

    private boolean authenticate(String email, String password)
    {
        received=false;
        new ReceivePasswordDb().execute(email);
        while(!received);
        if(stored_hash==null || stored_hash.equals(""))
            return false;

        return checkPassword(password,stored_hash);

    }

    private class ReceivePasswordDb extends AsyncTask<String,Void,Void>
    {

        @Override
        protected Void doInBackground(String... emailID)
        {
            String whereClause = Users.COLUMN_EMAIL_ID+" = ? ";
            Cursor cursor = getContentResolver().query(Users.CONTENT_URI,new String[]{Users.COLUMN_PASSWORD,Users._ID},whereClause,emailID,null);
            if(cursor==null || cursor.getCount()==0)
            {
                if(!cursor.isClosed())
                    cursor.close();
                received=true;
                return null;
            }
            cursor.moveToFirst();

            stored_hash=cursor.getString(cursor.getColumnIndex(Users.COLUMN_PASSWORD));
            current_user = cursor.getInt(cursor.getColumnIndex(Users._ID));

            cursor.close();

            received=true;
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
    }
}
