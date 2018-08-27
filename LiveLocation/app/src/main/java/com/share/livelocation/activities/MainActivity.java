package com.share.livelocation.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.share.livelocation.R;
import com.share.livelocation.service.GPSTracker;
import com.share.livelocation.service.TrackerService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";


    Button start_btn, stop_btn;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_btn = (Button) findViewById(R.id.start_btn);
        stop_btn = (Button) findViewById(R.id.stop_btn);

        start_btn.setOnClickListener(this);
        stop_btn.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Sign in logic here.
                    Toast.makeText(MainActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                }
            }
        };


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_btn:

                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();
                Log.d(TAG, "myRef.getRoot() : " + myRef.getRoot());
                Log.d(TAG, "userId : " + userId);
                // check if GPS enabled
                GPSTracker gpsTracker = new GPSTracker(this);
                String lat = String.valueOf(gpsTracker.getLatitude());
                String lng = String.valueOf(gpsTracker.getLongitude());
                myRef.child("Users").child(userId).child("name").setValue(user.getDisplayName());
                myRef.child("Users").child(userId).child("email").setValue(user.getEmail());
                myRef.child("Users").child(userId).child("issharing").setValue("false");
                myRef.child("Users").child(userId).child("lat").setValue(lat);
                myRef.child("Users").child(userId).child("lng").setValue(lng);
                myRef.child("Users").child(userId).child("userId").setValue(userId);

                startActivity(new Intent(MainActivity.this, TrackerActivity.class));
                break;


            case R.id.stop_btn:
                //stopService(new Intent(this, TrackerService.class));
                if (isMyServiceRunning(TrackerService.class))
                    processStopService(TrackerService.SERVICE_TAG);
                else {
                    Toast.makeText(this, "Service is not running", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void processStopService(final String tag) {
        Intent intent = new Intent(getApplicationContext(), TrackerService.class);
        intent.addCategory(tag);
        stopService(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
