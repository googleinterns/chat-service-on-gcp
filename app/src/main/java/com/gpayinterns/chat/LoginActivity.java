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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.interns.chat.R;

import java.util.Objects;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText mEmailField;
    private EditText mPasswordField;
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
        mEmailField = findViewById(R.id.input_email_id);
        mPasswordField = findViewById(R.id.input_password);
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
        mEditor.putString("currentUser", currentUser).apply();
    }

    private boolean authenticate(String email, String password)
    {
        //TODO authenticate from backend server
        //currentUser = userID received from the server
        return true;
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