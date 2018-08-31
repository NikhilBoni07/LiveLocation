package com.share.livelocation.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.share.livelocation.R;
import com.share.livelocation.activities.MapActivity;
import com.share.livelocation.pojo.JoinedCircleUsers;
import com.share.livelocation.pojo.UserDetails;

import java.util.ArrayList;

public class JoinedCircleAdapter extends BaseAdapter {

    private static final String TAG = "JoinedCircleAdapter";

    private Context context;
    private ArrayList<JoinedCircleUsers> joinedList;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    public JoinedCircleAdapter(Context context, ArrayList<JoinedCircleUsers> joinedList) {
        this.context = context;
        this.joinedList = joinedList;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
    }

    @Override
    public int getCount() {
        return joinedList.size();
    }

    @Override
    public Object getItem(int position) {
        return joinedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_joined_list, parent, false);
            holder = new ViewHolder();
            holder.userName = (TextView) convertView.findViewById(R.id.userName);
            holder.member_list = (LinearLayout) convertView.findViewById(R.id.member_list);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JoinedCircleUsers joinedCircleUsers = (JoinedCircleUsers) getItem(position);

        final ViewHolder finalHolder = holder;
        myRef.child("Users").child(joinedCircleUsers.getJoinedMemberId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "joinedCircleUsers.getJoinedMemberId() :: " + dataSnapshot);
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                finalHolder.userName.setText(userDetails.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.member_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(context, MapActivity.class);
                mapIntent.putExtra("memberId", "b04v80bQfxVnv9YQdi6Hf9fRy6J3");
                context.startActivity(mapIntent);
            }
        });

        return convertView;
    }


    /*private view holder class*/
    private class ViewHolder {
        TextView userName;
        LinearLayout member_list;
    }
}
