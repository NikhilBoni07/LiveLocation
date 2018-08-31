package com.share.livelocation.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.share.livelocation.service.GPSTracker;
import com.share.livelocation.service.TrackerService;

import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";


    private static final int PERMISSIONS_REQUEST = 1;

    private Button start_btn, stop_btn, share_btn, sharedUsers_btn;
    private EditText ciclecode_edt;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    FirebaseUser user;
    String userId;
    int shareCircleCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_btn = (Button) findViewById(R.id.start_btn);
        stop_btn = (Button) findViewById(R.id.stop_btn);
        share_btn = (Button) findViewById(R.id.share_btn);
        sharedUsers_btn = (Button) findViewById(R.id.sharedUsers_btn);
        ciclecode_edt = (EditText) findViewById(R.id.ciclecode_edt);

        start_btn.setOnClickListener(this);
        stop_btn.setOnClickListener(this);
        share_btn.setOnClickListener(this);
        sharedUsers_btn.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();


        user = mAuth.getCurrentUser();
        userId = user.getUid();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (!data.child(userId).exists()) {
                            myRef.child("Users").child(userId).child("name").setValue(user.getDisplayName());
                            myRef.child("Users").child(userId).child("email").setValue(user.getEmail());
                            myRef.child("Users").child(userId).child("issharing").setValue("false");
                            myRef.child("Users").child(userId).child("userId").setValue(userId);
                            myRef.child("Users").child(userId).child("circlecode").setValue(generateRandomNo());
                        }
                    }
                } else {
                    myRef.child("Users").child(userId).child("name").setValue(user.getDisplayName());
                    myRef.child("Users").child(userId).child("email").setValue(user.getEmail());
                    myRef.child("Users").child(userId).child("issharing").setValue("false");
                    myRef.child("Users").child(userId).child("userId").setValue(userId);
                    myRef.child("Users").child(userId).child("circlecode").setValue(generateRandomNo());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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


                //String userId1 = "4iUVdYtplTdHbYbdJUWIYNf5zYm2";
                Log.d(TAG, "myRef.getRoot() : " + myRef.getRoot());
                Log.d(TAG, "userId : " + userId);

                // check if GPS enabled
                GPSTracker gpsTracker = new GPSTracker(this);
                Double lat = gpsTracker.getLatitude();
                Double lng = gpsTracker.getLongitude();

                myRef.child("Users").child(userId).child("lat").setValue(lat);
                myRef.child("Users").child(userId).child("lng").setValue(lng);
                myRef.child("Users").child(userId).child("issharing").setValue("true");


                serviceStarting();
                /*Intent mapIntent = new Intent(this, MapActivity.class);
                startActivity(mapIntent);*/


                break;


            case R.id.stop_btn:
                //stopService(new Intent(this, TrackerService.class));
                if (isMyServiceRunning(TrackerService.class))
                    processStopService(TrackerService.SERVICE_TAG);
                else {
                    Toast.makeText(this, "Service is not running", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.share_btn:

                if (!ciclecode_edt.getText().equals(""))
                    shareCircleCode = Integer.parseInt(ciclecode_edt.getText().toString());
                else {
                    Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseReference commandsRef = myRef.child("Users");
                DatabaseReference myAccountRef = myRef.child("Users");

                final HashMap<String, String> userDetailsHm = new HashMap<String, String>();

                final Long[] myCircleCode = new Long[1];

                myAccountRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.i(TAG, "myAccountRef ds :: " + ds);
                            UserDetails userDetails = ds.getValue(UserDetails.class);
                            if (userDetails.getUserId().equals(userId)) {
                                Log.i(TAG, "My circle code :: " + Long.toString(userDetails.getCirclecode()));
                                myCircleCode[0] = userDetails.getCirclecode();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                commandsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Getting the User Data
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.i(TAG, "ds :: " + ds);
                            UserDetails userDetails = ds.getValue(UserDetails.class);
                            if (userDetails.getUserId().equals(userId)) {
                                userDetailsHm.put("name", userDetails.getName());
                                userDetailsHm.put("issharing", "true");
                                userDetailsHm.put("lat", String.valueOf(userDetails.getLat()));
                                userDetailsHm.put("lng", String.valueOf(userDetails.getLng()));
                                userDetailsHm.put("userId", String.valueOf(userDetails.getUserId()));

                            }
                        }

                        // Sharing the user data to shared userId
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            UserDetails userDetails = ds.getValue(UserDetails.class);
                            if (shareCircleCode != 0 && myCircleCode[0] != shareCircleCode && userDetails.getCirclecode() == shareCircleCode) {
                                myRef.child("Users").child(userDetails.getUserId()).child("JoinedCircles").child(userDetailsHm.get("userId")).child("issharing").setValue(userDetailsHm.get("issharing"));
                                myRef.child("Users").child(userDetails.getUserId()).child("JoinedCircles").child(userDetailsHm.get("userId")).child("lat").setValue(userDetailsHm.get("lat"));
                                myRef.child("Users").child(userDetails.getUserId()).child("JoinedCircles").child(userDetailsHm.get("userId")).child("lng").setValue(userDetailsHm.get("lng"));
                                myRef.child("Users").child(userDetails.getUserId()).child("JoinedCircles").child(userDetailsHm.get("userId")).child("name").setValue(userDetailsHm.get("name"));
                                myRef.child("Users").child(userId).child("CircleMembers").child(userDetails.getUserId()).child("circlememberid").setValue(userDetails.getUserId());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                break;

            case R.id.sharedUsers_btn:

                startActivity(new Intent(this, SharedUserList.class));

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

    private void startTrackerService() {
        //startService(new Intent(this, TrackerService.class));
        processStartService(TrackerService.SERVICE_TAG);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        } else {
            finish();
        }
    }

    private void processStartService(final String tag) {
        Intent intent = new Intent(getApplicationContext(), TrackerService.class);
        intent.addCategory(tag);
        startService(intent);
    }


    public void serviceStarting() {
        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }


    public int generateRandomNo() {
        Random rand = new Random();

        int n = rand.nextInt(90000) + 10000;

        return n;
    }
}
