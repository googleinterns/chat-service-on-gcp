package com.gpayinterns.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.gpayinterns.chat.ServerConstants.USERS;
import static com.gpayinterns.chat.ServerConstants.BASE_URL;
import static com.gpayinterns.chat.ServerConstants.CHATS;

public class ViewContactsActivity extends AppCompatActivity
{
    public static final String CHAT_LOGGED_IN_USER = "CHAT_LOGGED_IN_USER";
    public static final String CURRENT_USER = "currentUser";

    private static boolean active = false;

    ContactsRecyclerAdapter contactsRecyclerAdapter;
    LinearLayoutManager contactLayoutManager;
    private FloatingActionButton fab;
    private List<User> users = new ArrayList<User>();

    private String currentUser;
    RecyclerView recyclerContacts;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_view_contacts);
        Toolbar toolbar = findViewById(R.id.view_contacts_toolbar);
        setSupportActionBar(toolbar);

        getCurrentUser();
        progressBar = (ProgressBar) findViewById(R.id.view_contacts_indeterminateBar);

        initializeDisplayContent();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(ViewContactsActivity.this, NewMessageActivity.class));
            }
        });
    }

    /**
     * loads all chats with whom the current user has had a conversation earlier.
     */
    private void loadChatsFromServer()
    {
        Log.d("currentUser sent to server:",currentUser);

        String URL = BASE_URL + USERS + "/" + currentUser + "/" + CHATS;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("ResponseMessage: " , response.toString());
                        try
                        {
                            JSONArray chats = response.getJSONArray("payload");
                            List <User> contacts = new ArrayList<User>();
                            for(int i=0;i<chats.length();i++)
                            {
                                JSONObject chat = (JSONObject) chats.get(i);

                                String username = chat.getString("Username");
                                String chatID = chat.getString("ChatId");
                                String lastMessageID = chat.getString("LastSentMessageId");
                                String phoneNum = chat.getString("MobileNo");
 
                                contacts.add(new User(username,chatID,lastMessageID,phoneNum));
                            }
                            if(active)
                            {
                                progressBar.setVisibility(View.GONE);
                                contactsRecyclerAdapter.updateContactsList(contacts);
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

        VolleyController.getInstance(this).addToRequestQueueWithRetry(jsonObjectRequest);
    }


    /**
     * A method to get the currentUser stored in Shared Preferences & log out if no user found.
     */
    private void getCurrentUser()
    {
        SharedPreferences mPrefs= getSharedPreferences("CHAT_LOGGED_IN_USER", 0);
        currentUser = mPrefs.getString("currentUser","");
        if(currentUser.equals(""))
        {
            startActivity(new Intent(this,LoginActivity.class));
        }
    }

    /**
     * sets up the recyclerView for displaying contacts
     */
    private void initializeDisplayContent()
    {
        recyclerContacts = (RecyclerView) findViewById(R.id.contacts_recyclerView);
        contactLayoutManager = new LinearLayoutManager(this);
        contactsRecyclerAdapter = new ContactsRecyclerAdapter(this,users,currentUser);
        recyclerContacts.addItemDecoration(new DividerItemDecoration(recyclerContacts.getContext(), DividerItemDecoration.VERTICAL));
        recyclerContacts.setLayoutManager(contactLayoutManager);
        recyclerContacts.setAdapter(contactsRecyclerAdapter);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        active=true;
        loadChatsFromServer();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Runtime.getRuntime().gc();// free heap memory by destroying unreachable objects
    }

    @Override
    protected void onPause()
    {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        active=false;
        super.onPause();
    }


    private static final int TIME_INTERVAL = 2000; // (in milliseconds) desired time passed between two back presses.
    private long mBackPressed=0;


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

    /**
     * exits the application
     */
    private void exit()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
            public boolean onMenuItemActionExpand(MenuItem item)
            {
                searchView.setFocusable(true);
                searchView.requestFocus();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                searchView.setQuery("",false);
                fab.setVisibility(View.VISIBLE);
                hideSoftKeyboard();
                loadChatsFromServer();
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener()
        {
            @Override
            public boolean onClose()
            {
                progressBar.setVisibility(View.GONE);
                searchView.setQuery("",false);
                fab.setVisibility(View.VISIBLE);
                loadChatsFromServer();
                hideSoftKeyboard();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener()
                {
                    @Override
                    public boolean onQueryTextSubmit(String query)
                    {
                        contactsRecyclerAdapter.getFilter().filter(query.toLowerCase());
                        new CountDownTimer(2000, 1000)
                        {
                            @Override
                            public void onTick(long millisUntilFinished)
                            {
                            }
                            @Override
                            public void onFinish()
                            {
                                progressBar.setVisibility(View.GONE);
                            }
                        }.start();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText)
                    {
                        progressBar.setVisibility(View.VISIBLE);
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
            fab.setVisibility(View.INVISIBLE);
        }
        return true;
    }

    private void logout()
    {
        SharedPreferences mPrefs = getSharedPreferences(CHAT_LOGGED_IN_USER, 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString(CURRENT_USER,"").apply();
        startActivity(new Intent(this,LoginActivity.class));
    }

    /**
     * show a dialog window for confirmation when the user opts to logout.
     */
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