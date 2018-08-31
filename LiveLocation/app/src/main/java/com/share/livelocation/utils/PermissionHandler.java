package com.share.livelocation.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.share.livelocation.R;
import com.share.livelocation.dialogs.LiveLocationDialog;
import com.share.livelocation.enums.AlertDialogIndex;
import com.share.livelocation.interfaces.AlertDialogCallbackInterface;
import com.share.livelocation.interfaces.PermissionRequestCallbackListener;


public class PermissionHandler {

    private static PermissionHandler ourInstance;

    public static PermissionHandler getInstance() {
        if (ourInstance == null)
            ourInstance = new PermissionHandler();
        return ourInstance;
    }

    private PermissionHandler() {
    }


    public void requestDynamicPermission(final Activity context, final int requestCode, final PermissionRequestCallbackListener permissionRequestCallbackListener) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)/*
                + ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)*/
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("===checkSelfPermission");
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION)/*
                    || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.CAMERA)*/
                    ) {
                System.out.println("===shouldShowRequestPermissionRationale");
                LiveLocationDialog.getInstance().showAlertDialog(context, null, context.getString(R.string.grant_permission_message), context.getString(R.string.enable_permission), context.getString(R.string.cancel), null, AlertDialogIndex.SIMPLE_ALERT_DIALOG, new AlertDialogCallbackInterface() {

                    @Override
                    public void getAlertDialogAction(int buttonClick, AlertDialogIndex dialogFor, Bundle bundle, LiveLocationDialog oicAlertDialog) {
                        if (buttonClick == AlertDialogCallbackInterface.POSITIVE_BUTTON_CLICK) {
                            ActivityCompat.requestPermissions(context,
                                    new String[]{
                                            Manifest.permission.ACCESS_COARSE_LOCATION/*,
                                            Manifest.permission.CAMERA*/
                                    }, requestCode);
                        } else {
                            permissionRequestCallbackListener.getPermissionData(PermissionRequestCallbackListener.PERMISSION_CANCELLED);
                        }
                    }

                });
            } else {
                System.out.println("===shouldShowRequestPermissionRationale else");
                ActivityCompat.requestPermissions(context,
                        new String[]
                                {Manifest.permission.ACCESS_COARSE_LOCATION/*,
                                        Manifest.permission.CAMERA*/},

                        requestCode);
            }
        } else {
            System.out.println("===checkSelfPermission else");
            permissionRequestCallbackListener.getPermissionData(PermissionRequestCallbackListener.PERMISSION_GRANTED);
        }
    }


    public void redirectToSettingsScreen(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

}
