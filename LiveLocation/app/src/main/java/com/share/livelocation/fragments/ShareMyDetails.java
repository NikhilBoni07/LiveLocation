package com.share.livelocation.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.share.livelocation.R;

public class ShareMyDetails extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.share_details_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ServiceProviderListFragment.
     */
    public static ShareMyDetails newInstance(/*Bundle bundle*/) {
        ShareMyDetails shareMyDetails = new ShareMyDetails();
        //sharedMembers.setArguments(bundle);
        return shareMyDetails;
    }
}
