package com.example.chat;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.chat.ChatProviderContract.Chat;
import com.example.chat.ChatProviderContract.Messages;
import com.example.chat.ChatProviderContract.Users;
import com.example.chat.DatabaseContract.userEntry;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewContactsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final String CHAT_LOGGED_IN_USER = "CHAT_LOGGED_IN_USER";
    public static final String CURRENT_USER = "currentUser";
    ContactsRecyclerAdapter contactsRecyclerAdapter;
    LinearLayoutManager contactLayoutManager;

    public static int LOADER_CONTACTS = 0;
    private volatile boolean lastMessageUpdated;
    private volatile boolean currentUserUpdated;

    public volatile int currentUser;
    private volatile boolean receivedMessageUpdated;
    RecyclerView recyclerContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        setContentView(R.layout.activity_view_contacts);
        Toolbar toolbar = findViewById(R.id.view_contacts_toolbar);
        setSupportActionBar(toolbar);

        initializeDisplayContent();

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //TODO Launch NewContactActivity
            }
        });

        enableStrictMode();



//        LoadChatsFromServer();
    }

//    private void LoadChatsFromServer()
//    {
//        String url = "https://gcp-chat-service.an.r.appspot.com/users/3441453482889885209/chats";
//        RequestQueue queue = RequestSingleton.getInstance(this.getApplicationContext()).
//                getRequestQueue();
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        textView.setText("Response: " + response.toString());
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO: Handle error
//
//                    }
//                });
//        RequestSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
//    }


    private void HideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void getCurrentUser()
    {
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids)
            {
                SharedPreferences mPrefs= getSharedPreferences("CHAT_LOGGED_IN_USER", 0);
                currentUser = mPrefs.getInt("currentUser",-1);
                currentUserUpdated=true;
                return null;
            }
        };
        currentUserUpdated = false;
        task.execute();
        while(!currentUserUpdated);
        if(currentUser==-1)
        {
            startActivity(new Intent(this,LoginActivity.class));
        }
    }

    private void enableStrictMode()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);
    }

    private void initializeDisplayContent()
    {
        recyclerContacts = (RecyclerView) findViewById(R.id.contacts_recyclerView);
        contactLayoutManager = new LinearLayoutManager(this);
        contactsRecyclerAdapter = new ContactsRecyclerAdapter(this,null);
        recyclerContacts.addItemDecoration(new DividerItemDecoration(recyclerContacts.getContext(), DividerItemDecoration.VERTICAL));
        recyclerContacts.setLayoutManager(contactLayoutManager);
        recyclerContacts.setAdapter(contactsRecyclerAdapter);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        getCurrentUser();


        receivedMessageUpdated=false;
        new UpdateReceivedMessageDb().execute();
        lastMessageUpdated=false;
        new UpdateLastMessageDb().execute();
        while(!lastMessageUpdated);

        LoaderManager.getInstance(this).restartLoader(LOADER_CONTACTS,null,this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args)
    {
        CursorLoader loader = null;
        if(id==LOADER_CONTACTS)
        {
            Uri uri = Users.CONTENT_URI;
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
                           Integer.toString(currentUser)
                    };
            while(!receivedMessageUpdated);
            loader = new CursorLoader(this,uri,user_columns,selection,selectionArgs,null);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data)
    {

        if(loader.getId() == LOADER_CONTACTS)
        {
            contactsRecyclerAdapter.changeCursor(data);
        }
    }

    @Override
    protected void onPause()
    {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onPause();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader)
    {
        if(loader.getId() == LOADER_CONTACTS)
        {
            contactsRecyclerAdapter.changeCursor(null);
        }
    }



    private class UpdateReceivedMessageDb extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... aVoid)
        {
            ContentValues argReceived = new ContentValues();
            argReceived.put(Messages.COLUMN_RECEIVED, "1");
            getContentResolver().update(Messages.CONTENT_URI,argReceived,Messages.COLUMN_RECEIVER+" = ? ",new String []{Integer.toString(currentUser)});

            ContentValues argSent = new ContentValues();
            argSent.put(Messages.COLUMN_RECEIVED, "0");
            getContentResolver().update(Messages.CONTENT_URI,argSent,Messages.COLUMN_SENDER+" = ? ",new String []{Integer.toString(currentUser)});
            receivedMessageUpdated=true;
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }
    }
    private class UpdateLastMessageDb extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... aVoid)
        {
            Uri uri = ChatProviderContract.Chat.CONTENT_URI;
            //columns,where,whereArgs
            String[] chatColumns = {Chat.COLUMN_USER1, Chat.COLUMN_USER2, Chat.COLUMN_LAST_MESSAGE};
            String selection = " user1 = ? OR user2 = ? ";
            String [] selectionArgs = new String[]
                    {
                            Integer.toString(currentUser),
                            Integer.toString(currentUser)
                    };
            Cursor cursor = getContentResolver().query(uri, chatColumns,selection,selectionArgs,null);

            if(cursor==null)
                return null;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                int user2 = cursor.getInt(cursor.getColumnIndex(Chat.COLUMN_USER1));
                if(user2==currentUser)
                {
                    user2=cursor.getInt(cursor.getColumnIndex(Chat.COLUMN_USER2));
                }
                String lastMessage = cursor.getString(cursor.getColumnIndex(Chat.COLUMN_LAST_MESSAGE));


                ContentValues values = new ContentValues();
                values.put(Users.COLUMN_LAST_MESSAGE,lastMessage);
                String where = "_id = ? ";
                getContentResolver().update(Users.CONTENT_URI,values,where,new String []{Integer.toString(user2)});
            }
            lastMessageUpdated=true;
            cursor.close();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }
    }


    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;


    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            exit();
            super.onBackPressed();
            return;
        }
        else
        {
            Toast.makeText(getBaseContext(), "Tap back once again to exit", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();

    }

    private void exit()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void restartLoader()
    {
        LoaderManager.getInstance(this).restartLoader(LOADER_CONTACTS,null,this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_logout,menu);
        final MenuItem searchItem = menu.findItem(R.id.contacts_search_menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);


        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchView.setFocusable(true);
                searchView.requestFocus();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchView.setQuery("",false);
                HideSoftKeyboard();
                restartLoader();
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener()
        {
            @Override
            public boolean onClose()
            {
                searchView.setQuery("",false);
                restartLoader();
                HideSoftKeyboard();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener()
                {
                    @Override
                    public boolean onQueryTextSubmit(String query)
                    {
                        contactsRecyclerAdapter.getFilter().filter(query.toLowerCase());
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText)
                    {
                        contactsRecyclerAdapter.getFilter().filter(newText.toLowerCase());
                        return true;
                    }
                }
        );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.action_log_out)
        {
            showDialogWindow();
        }
        else if(item.getItemId()==R.id.contacts_search_menu)
        {
        }
        return true;
    }

    private void logout()
    {
        SharedPreferences mPrefs = getSharedPreferences(CHAT_LOGGED_IN_USER, 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putInt(CURRENT_USER,-1).apply();
        disconnectFromFacebook();
        startActivity(new Intent(this,LoginActivity.class));
    }

    private void showDialogWindow()
    {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        logout();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .create();


        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(-16777216);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(-16777216);
            }

        });
        dialog.show();

    }

    public void disconnectFromFacebook()
    {

        if (AccessToken.getCurrentAccessToken() == null)
        {
            return; // already logged out
        }
        LoginManager.getInstance().logOut();
    }


    public void ConnectURL(String url)
    {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        // Display the first 500 characters of the response string.
                        Log.d("Response is: ", response.substring(0,500));
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("That didn't work!" , error.toString());
                    }
                });

        // Add the request to the RequestQueue.
        RequestSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

}