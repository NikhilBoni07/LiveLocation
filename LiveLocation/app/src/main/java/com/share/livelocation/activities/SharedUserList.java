package com.share.livelocation.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.share.livelocation.R;
import com.share.livelocation.pojo.UserDetails;

public class SharedUserList extends AppCompatActivity implements ValueEventListener {

    private static final String TAG = "SharedUserList";

    ListView userslist;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_user_list);

        userslist = (ListView) findViewById(R.id.userslist);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        user = mAuth.getCurrentUser();
        userId = user.getUid();

        myRef = mFirebaseDatabase.getReference().child(userId);

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

        myRef.addValueEventListener(this);

        getSharedList();

    }

    private void getSharedList() {

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.i(TAG, "myAccountRef ds :: " + ds);
                    UserDetails userDetails = ds.getValue(UserDetails.class);
                    /*if (userDetails.getUserId().equals(userId)) {
                        Log.i(TAG, "My circle code :: " + Long.toString(userDetails.getCirclecode()));
                        myCircleCode[0] = userDetails.getCirclecode();
                    }*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
