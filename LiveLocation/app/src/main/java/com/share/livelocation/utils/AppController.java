package com.share.livelocation.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.share.livelocation.R;
import com.share.livelocation.interfaces.AppConstant;

public class AppController {

    private static AppController ourInstance = null;

    public static AppController getInstance() {
        if (ourInstance == null)
            ourInstance = new AppController();
        return ourInstance;
    }

    private AppController() {
    }


    /**
     * method to hide soft keypad
     */
    public void hideSoftInputKeypad(Activity activity) {
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View focusView = activity.getCurrentFocus();
            if (focusView != null) {
                IBinder iBinder = focusView.getWindowToken();
                if (iBinder != null)
                    inputMethodManager.hideSoftInputFromWindow(iBinder, 0);
            }
        }
    }


    public void replaceFragment(FragmentActivity activity, Fragment fragment, boolean flagIsAddToBackStack) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.screen_area, fragment, AppConstant.FRAGMENT_TAG);
        if (flagIsAddToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void addFragment(FragmentActivity activity, Fragment fragment, boolean flagIsAddToBackStack) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.screen_area, fragment, AppConstant.FRAGMENT_TAG);
        if (flagIsAddToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void removeFragment(Activity activity, Fragment fragment) {
        FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
        fragmentManager.popBackStack();
    }


    /**
     * This is used to check weather Internet is on or off
     *
     * @return true if internet is on else return false
     */
    @SuppressWarnings("deprecation")
    public boolean checkInternet(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        // check if network is connected or device is in range
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This is used to show String Message
     *
     * @param message message String to show
     */
    public void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is used to show Toast Message
     */
    public void showMessage(Context context, int messageResourceId) {
        Toast.makeText(context, context.getString(messageResourceId), Toast.LENGTH_SHORT).show();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @SuppressLint("ObsoleteSdkInt")
    public ProgressDialog getProgressDialog(Context context, String title, String message, boolean bIsShow) {
        ProgressDialog pd;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            pd = new ProgressDialog(new ContextThemeWrapper(context, R.style.AppTheme));
        } else {
            pd = new ProgressDialog(context);
        }
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        if (title != null) {
            pd.setTitle(title);
        }
        if (message != null) {
            pd.setMessage(message);
        }

        if (bIsShow) {
            pd.show();
        }

        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);

        return pd;
    }

    public void dismissProgressDialog(ProgressDialog progressDialog) {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
