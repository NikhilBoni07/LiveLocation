package com.share.livelocation.fragments;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.share.livelocation.R;
import com.share.livelocation.service.GPSTracker;
import com.share.livelocation.service.TrackerService;

import static android.content.Context.LOCATION_SERVICE;

public class DefaultFragment extends Fragment implements View.OnClickListener {
    private static final int PERMISSIONS_REQUEST = 1;
    Button start_sharing, stop_sharing;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    FirebaseUser user;
    String userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.default_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        start_sharing = (Button) view.findViewById(R.id.start_sharing);
        stop_sharing = (Button) view.findViewById(R.id.stop_sharing);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();


        user = mAuth.getCurrentUser();
        userId = user.getUid();

        start_sharing.setOnClickListener(this);
        stop_sharing.setOnClickListener(this);
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ServiceProviderListFragment.
     */
    public static DefaultFragment newInstance(/*Bundle bundle*/) {
        DefaultFragment defaultFragment = new DefaultFragment();
        //sharedMembers.setArguments(bundle);
        return defaultFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_sharing:
// check if GPS enabled
                GPSTracker gpsTracker = new GPSTracker(getActivity());
                Double lat = gpsTracker.getLatitude();
                Double lng = gpsTracker.getLongitude();

                myRef.child("Users").child(userId).child("lat").setValue(lat);
                myRef.child("Users").child(userId).child("lng").setValue(lng);
                myRef.child("Users").child(userId).child("issharing").setValue("true");


                serviceStarting();
                break;

            case R.id.stop_sharing:
                if (isMyServiceRunning(TrackerService.class))
                    processStopService(TrackerService.SERVICE_TAG);
                else {
                    Toast.makeText(getActivity(), "Service is not running", Toast.LENGTH_SHORT).show();
                }
                break;


            default:
                break;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void processStopService(final String tag) {
        Intent intent = new Intent(getActivity(), TrackerService.class);
        intent.addCategory(tag);
        getActivity().stopService(intent);
    }


    public void serviceStarting() {
        // Check GPS is enabled
        LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity(), "Please enable location services", Toast.LENGTH_SHORT).show();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    private void startTrackerService() {
        processStartService(TrackerService.SERVICE_TAG);
    }

    private void processStartService(final String tag) {
        Intent intent = new Intent(getContext(), TrackerService.class);
        intent.addCategory(tag);
        getActivity().startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        } else {
            //finish();
        }
    }
}
