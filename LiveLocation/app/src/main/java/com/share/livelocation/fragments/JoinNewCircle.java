package com.share.livelocation.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.share.livelocation.R;
import com.share.livelocation.pojo.UserDetails;
import com.share.livelocation.utils.AppController;

import java.util.HashMap;

public class JoinNewCircle extends Fragment implements View.OnClickListener {

    private static final String TAG = "JoinNewCircle";

    Button submit_btn;
    EditText ciclecode_edt;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    FirebaseUser user;
    String userId;
    int shareCircleCode = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.joint_new_circle_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        submit_btn = (Button) view.findViewById(R.id.submit_btn);
        ciclecode_edt = (EditText) view.findViewById(R.id.ciclecode_edt);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference().child("Users");
        user = mAuth.getCurrentUser();
        userId = user.getUid();

        submit_btn.setOnClickListener(this);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ServiceProviderListFragment.
     */
    public static JoinNewCircle newInstance(/*Bundle bundle*/) {
        JoinNewCircle joinNewCircle = new JoinNewCircle();
        //sharedMembers.setArguments(bundle);
        return joinNewCircle;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_btn:
                if (!ciclecode_edt.getText().equals("")) {
                    shareCircleCode = Integer.parseInt(ciclecode_edt.getText().toString());
                } else {
                    AppController.getInstance().showMessage(getActivity(), "Circle code field cannot be empty");
                }

                DatabaseReference myAccountRef = myRef;
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


                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

            default:
                break;

        }
    }
}
