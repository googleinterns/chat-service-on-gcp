package com.example.chat;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chat.DatabaseContract.userEntry;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsRecyclerAdapter extends RecyclerView.Adapter <ContactsRecyclerAdapter.ViewHolder>
    implements Filterable
{
    private final Context mContext;
    private final LayoutInflater mlayoutInflater;
    private Cursor mCursor;
    private int mUserIdPos;
    private int mNamePos;
    private int mEmailIdPos;
    private int mLastMessagePos;

    //viewType 0: left side text
    //viewType 1: right side text

    public ContactsRecyclerAdapter(Context Context, Cursor cursor)
    {
        mContext = Context;
        mCursor = cursor;
        mlayoutInflater = LayoutInflater.from(mContext);
        populateColumnPositions();
    }

    private void populateColumnPositions()
    {
        if(mCursor==null)
            return;
        //Get column indices from mCursor
        mUserIdPos = mCursor.getColumnIndex(BaseColumns._ID);
        mNamePos = mCursor.getColumnIndex(userEntry.COLUMN_NAME);
        mEmailIdPos = mCursor.getColumnIndex(userEntry.COLUMN_EMAIL_ID);
        mLastMessagePos = mCursor.getColumnIndex(userEntry.COLUMN_LAST_MESSAGE);
    }

    public void changeCursor(Cursor cursor)
    {
        if(mCursor != null)
            mCursor.close();
        mCursor=cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View itemView = mlayoutInflater.inflate(R.layout.item_contact,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        if(mCursor.isClosed())
            return;
        mCursor.moveToPosition(position);

        String name = mCursor.getString(mNamePos);
        String last_message = mCursor.getString(mLastMessagePos);

        holder.mUserId = mCursor.getInt(mUserIdPos);
        holder.mContactName = name;
        holder.mName.setText(name);
        holder.mMessage.setText(last_message.trim());
        holder.mPicName.setText((Character.toString(name.charAt(0))).toUpperCase());

        int random_color=getRandomColor();
        holder.mContactIcon.getBackground().setColorFilter(random_color, PorterDuff.Mode.SRC_ATOP);



    }

    @Override
    public int getItemCount()
    {
        if(mCursor==null)
            return 0;
        else
            return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView mName;
        public final TextView mMessage;
        public final TextView mPicName;
        public final RelativeLayout mContactIcon;
        public int mUserId;
        public String mContactName;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.contact_name);
            mMessage = (TextView) itemView.findViewById(R.id.view_contact_message);
            mPicName = (TextView) itemView.findViewById(R.id.tvWeekDayFirstLetter);
            mContactIcon = (RelativeLayout) itemView.findViewById(R.id.rlWeekDay);


            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mContext,ViewMessageActivity.class);
                    intent.putExtra(ViewMessageActivity.CONTACT_ID,mUserId);
                    intent.putExtra(ViewMessageActivity.CONTACT_NAME,mContactName);
                    mContext.startActivity(intent);
                }
            });
        }

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
                MatrixCursor matrixCursor = new MatrixCursor(new String[] {BaseColumns._ID,userEntry.COLUMN_NAME,userEntry.COLUMN_EMAIL_ID,userEntry.COLUMN_LAST_MESSAGE});
                if(constraint.length()>0 && !(mCursor.isClosed()))
                {
                    populateColumnPositions();


                    for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext())
                    {
                        int ID = mCursor.getInt(mUserIdPos);
                        String name = mCursor.getString(mNamePos);
                        String email = mCursor.getString(mEmailIdPos);
                        String last_message = mCursor.getString(mLastMessagePos);
                        if(name.toLowerCase().contains(constraint.toString()))
                        {
                            matrixCursor.addRow(new Object[]{ID, name, email, last_message});
                        }
                    }
                }
                results.values = matrixCursor;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                MatrixCursor matrixCursor = (MatrixCursor) results.values;
                changeCursor(matrixCursor);
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
