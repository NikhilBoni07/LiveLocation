package com.share.livelocation.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.share.livelocation.R;
import com.share.livelocation.pojo.UserDetails;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, ValueEventListener, GoogleMap.OnCameraMoveListener {

    private static final String TAG = "MapActivity";


    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    FirebaseUser user;
    String userId;

    private MapView mapView;

    GoogleMap map;
    MarkerOptions markerOptions;

    String jointedUserId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        if (intent != null) {
            jointedUserId = intent.getStringExtra("memberId");
        }

        mapView = (MapView) findViewById(R.id.map);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        user = mAuth.getCurrentUser();
        userId = user.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Sign in logic here.
                    Toast.makeText(MapActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MapActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                }
            }
        };
        

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        DatabaseReference commandsRef = myRef.child("Users").child(jointedUserId);
        commandsRef.addValueEventListener(this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        // googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);

        // Enable / Disable zooming controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Enable / Disable my location button
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Enable / Disable Compass icon
        googleMap.getUiSettings().setCompassEnabled(true);

        // Enable / Disable Rotate gesture
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        // Enable / Disable zooming functionality
        googleMap.getUiSettings().setZoomGesturesEnabled(true);


    }


    public void mapSettings(GoogleMap googleMap, Double lat, Double lng) {
        Log.d(TAG, "mapSettings lat : " + lat);
        Log.d(TAG, "mapSettings lng : " + lng);

        addLocationMapper(googleMap, lat, lng);
    }


    public void addLocationMapper(GoogleMap googleMap, Double lat, Double lng) {

        LatLng allLatLang = new LatLng(lat, lng);

        if (markerOptions == null) {
            markerOptions = new MarkerOptions();
            markerOptions.title("My Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerOptions.position(allLatLang);
            googleMap.addMarker(markerOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(allLatLang, 15));
        } else {
            markerOptions.position(allLatLang);
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


    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Log.d("TAG", "onDataChange dataSnapshot:: " + dataSnapshot);
        //for (DataSnapshot ds : dataSnapshot.getChildren()) {

        UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
        Log.d("TAG", "onDataChange :: " + userDetails.getLat() + " / " + userDetails.getLng());

        //if (userDetails.getUserId().equals(jointedUserId))
        if (map != null) {

            mapSettings(map, userDetails.getLat(), userDetails.getLng());
        }
        //}
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }


    @Override
    public void onCameraMove() {


    }
}
