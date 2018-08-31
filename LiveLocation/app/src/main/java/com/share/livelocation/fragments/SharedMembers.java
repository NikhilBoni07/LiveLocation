package com.share.livelocation.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.share.livelocation.utils.AppController;

import java.util.ArrayList;

public class SharedMembers extends Fragment {

    private static final String TAG = "SharedMembers";

    Context context;

    ListView userslist;
    TextView nodata_txt;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myAccRef;

    private FirebaseUser user;
    private String userId;
    private ProgressDialog mProgressDialog;
    private ArrayList<JoinedCircleUsers> memberId = new ArrayList<JoinedCircleUsers>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shared_members_fragment, null);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        userslist = (ListView) view.findViewById(R.id.userslist);
        nodata_txt = (TextView) view.findViewById(R.id.nodata_txt);

        if (!AppController.getInstance().checkInternet(context)) {
            Toast.makeText(getContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

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
                    Toast.makeText(getContext(), "Signed In", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Signed Out", Toast.LENGTH_SHORT).show();
                }
            }
        };
        getSharedList();
    }

    private void getSharedList() {

        mProgressDialog = AppController.getInstance().getProgressDialog(context, getResources().getString(R.string.app_name), getResources().getString(R.string.wait), true);

        myAccRef.addValueEventListener(new ValueEventListener() {
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
            JoinedCircleAdapter joinedCircleAdapter = new JoinedCircleAdapter(getContext(), memberId);
            userslist.setAdapter(joinedCircleAdapter);
            userslist.setVisibility(View.VISIBLE);
            AppController.getInstance().dismissProgressDialog(mProgressDialog);
        } else {
            nodata_txt.setVisibility(View.VISIBLE);
            AppController.getInstance().dismissProgressDialog(mProgressDialog);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ServiceProviderListFragment.
     */
    public static SharedMembers newInstance(/*Bundle bundle*/) {
        SharedMembers sharedMembers = new SharedMembers();
        //sharedMembers.setArguments(bundle);
        return sharedMembers;
    }
}
