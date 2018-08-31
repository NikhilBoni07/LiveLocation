package com.share.livelocation.interfaces;

import android.os.Bundle;

import com.share.livelocation.enums.FragmentRedirectionIndex;

public interface OnFragmentInteractionListener {

    void onFragmentInteraction(Bundle bundle, FragmentRedirectionIndex index, String strParameter, boolean flagBackStack);

    void setToolBarTitle(String strTitle);

    void logOutUser();
}
