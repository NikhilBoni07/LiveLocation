package com.share.livelocation.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.share.livelocation.R;
import com.share.livelocation.adapters.JoinedCircleAdapter;
import com.share.livelocation.pojo.JoinedCircleUsers;

import java.util.ArrayList;

public class SharedUserList extends AppCompatActivity {

    private static final String TAG = "SharedUserList";

    ListView userslist;
    TextView nodata_txt;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myAccRef;

    private FirebaseUser user;
    private String userId;

    private ArrayList<JoinedCircleUsers> memberId = new ArrayList<JoinedCircleUsers>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_user_list);

        userslist = (ListView) findViewById(R.id.userslist);
        nodata_txt = (TextView) findViewById(R.id.nodata_txt);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        user = mAuth.getCurrentUser();
        userId = user.getUid();

        myAccRef = mFirebaseDatabase.getReference().child("Users").child(userId).child("JoinedCircles");


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Sign in logic here.
                    Toast.makeText(SharedUserList.this, "Signed In", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SharedUserList.this, "Signed Out", Toast.LENGTH_SHORT).show();
                }
            }
        };

        //myRef.addValueEventListener(this);

        getSharedList();


    }

    private void getSharedList() {

        myAccRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "myAccountRef dataSnapshot :: " + dataSnapshot);
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String memberIds = ds.getKey();
                    JoinedCircleUsers joinedCircleUsers = new JoinedCircleUsers();
                    joinedCircleUsers.setJoinedMemberId(memberIds);
                    memberId.add(joinedCircleUsers);
                }
                setListAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }


    public void setListAdapter() {
        if (memberId.size() > 0) {
            JoinedCircleAdapter joinedCircleAdapter = new JoinedCircleAdapter(this, memberId);
            userslist.setAdapter(joinedCircleAdapter);
            userslist.setVisibility(View.VISIBLE);
        } else {
            nodata_txt.setVisibility(View.VISIBLE);
        }
    }

    /*@Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }*/
}
