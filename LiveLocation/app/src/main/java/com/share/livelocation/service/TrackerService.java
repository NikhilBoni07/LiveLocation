package com.share.livelocation.service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.share.livelocation.R;
import com.share.livelocation.pojo.UserDetailsService;

import java.util.ArrayList;

public class TrackerService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ValueEventListener {
    private static final String TAG = "TrackerService";


    public static final String SERVICE_TAG = "MyServiceTag";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DatabaseReference myAccountRef;

    //New
    FusedLocationProviderClient mFusedLocationClient;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    FirebaseUser user;
    String userId;

    private ArrayList<String> circleMember = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();


        user = mAuth.getCurrentUser();
        userId = user.getUid();

        myAccountRef = mFirebaseDatabase.getReference().child("Users").child(userId);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (mGoogleApiClient != null && mFusedLocationClient != null) {
            requestLocationUpdates();
        } else {
            buildGoogleApiClient();
        }

        // Will handle if data is shared and update to every other user
        myAccountRef.addValueEventListener(this);

    }

    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setOngoing(true)
                .setContentIntent(broadcastIntent);
        //.setSmallIcon(R.drawable.ic_launcher_foreground);
        startForeground(1, builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    public void requestLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                Log.d(TAG, "location update " + location);
                myRef.child("Users").child(userId).child("lat").setValue(location.getLatitude());
                myRef.child("Users").child(userId).child("lng").setValue(location.getLongitude());
            }
        }

        ;

    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Sercive on Destroy");
        //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "onLocationChanged location update " + location);
        myRef.child("Users").child(userId).child("lat").setValue(location.getLatitude());
        myRef.child("Users").child(userId).child("lng").setValue(location.getLongitude());
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


        Log.d(TAG, "onDataChange dataSnapshot " + dataSnapshot);
        UserDetailsService userDetailsService = dataSnapshot.getValue(UserDetailsService.class);

        if (userDetailsService != null && userDetailsService.getUserId().equals(userId)) {
            if (userDetailsService.getIssharing().equals("true")) {
                myAccountRef.child("CircleMembers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "Test CircleMembers dataSnapshot :: " + dataSnapshot.getValue());
                        dataSnapshot.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d(TAG, "Test dataSnapshot.getRef() dataSnapshot :: " + dataSnapshot.getValue());
                                // Get all the userId to which I shared
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String memberId = ds.getKey();
                                    circleMember.add(memberId);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        // Updating my Data to all the other users.
        for (int i = 0; i < circleMember.size(); i++) {
            Log.d(TAG, "circleMember " + circleMember.get(i));
            myRef.child("Users").child(circleMember.get(i)).child("JoinedCircles").child(userId).child("lat").setValue(userDetailsService.getLat());
            myRef.child("Users").child(circleMember.get(i)).child("JoinedCircles").child(userId).child("lng").setValue(userDetailsService.getLng());
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
