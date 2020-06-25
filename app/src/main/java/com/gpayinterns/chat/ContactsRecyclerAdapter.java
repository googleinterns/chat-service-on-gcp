package com.gpayinterns.chat;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.gpayinterns.chat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import util.User;

public class ContactsRecyclerAdapter extends RecyclerView.Adapter <ContactsRecyclerAdapter.ViewHolder>
    implements Filterable
{
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;

    private List<User> mUsers;


    public ContactsRecyclerAdapter(Context context, List <User> users)
    {
        mContext = context;
        mUsers = users;
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = mLayoutInflater.inflate(R.layout.item_contact,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        if(position>=mUsers.size())
            return ;

        String name = mUsers.get(position).username;
        String chatID = mUsers.get(position).chatID;
        String lastMessageID = mUsers.get(position).lastMessageID;

        holder.mUsername.setText(name);
        holder.mPicName.setText((Character.toString(name.charAt(0))).toUpperCase());
        holder.mChatID = chatID;
        holder.mLastMessageID = lastMessageID;

        int random_color=getRandomColor();
        holder.mContactIcon.getBackground().setColorFilter(random_color, PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public int getItemCount()
    {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView mUsername;
        public final TextView mPicName;
        public final RelativeLayout mContactIcon;
        public String mChatID;
        public String mLastMessageID;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mUsername = (TextView) itemView.findViewById(R.id.contact_name);
            mPicName = (TextView) itemView.findViewById(R.id.tvWeekDayFirstLetter);
            mContactIcon = (RelativeLayout) itemView.findViewById(R.id.rlWeekDay);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mContext,ViewMessageActivity.class);

                    intent.putExtra(ViewMessageActivity.CHAT_ID,mChatID);
                    intent.putExtra(ViewMessageActivity.CONTACT_USERNAME,mUsername.getText().toString());
                    intent.putExtra(ViewMessageActivity.LAST_MESSAGE_ID,mLastMessageID);

                    mContext.startActivity(intent);
                }
            });
        }

    }

    public void updateContactsList(List <User> contactList)
    {
        mUsers.clear();
        mUsers.addAll(contactList);
        this.notifyDataSetChanged();
    }



    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults results = new FilterResults();
                List <User> filteredResult = new ArrayList<>();
                if(constraint.length()>0 && !(mUsers.isEmpty()))
                {
                    for(User u : mUsers)
                    {
                        String userName = u.username;
                        if(userName.toLowerCase().contains(constraint.toString()))
                        {
                            filteredResult.add(u);
                        }
                    }
                }
                results.values = filteredResult;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                mUsers = (List<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    int getRandomColor()
    {
        int r = 0;
        int g = 0;
        int b = 0;
        Random rand = new Random();
        switch(rand.nextInt(7)){    // switch over 7 possible colors
            case (0):   // red
                r = 205;
                break;
            case (1):   // green
                g = 205;
                break;
            case (2):   // blue
                b = 205;
                break;
            case (3):   // pink
                r = 255;
                g = 20;
                b = 147;
                break;
            case (4):   // magenta
                r = 200;
                b = 200;
                break;
            case (5):   // orange
                r = 255;
                g = 165;
                break;
            case (6):   // purple
                r = 128;
                b = 128;
                break;
        }
        return Color.rgb(r,g,b);
    }

}
