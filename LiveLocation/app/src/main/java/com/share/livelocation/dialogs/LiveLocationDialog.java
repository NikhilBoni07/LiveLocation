package com.share.livelocation.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.share.livelocation.enums.AlertDialogIndex;
import com.share.livelocation.interfaces.AlertDialogCallbackInterface;

public class LiveLocationDialog {

    private static LiveLocationDialog sfAlertDialog;

    public static LiveLocationDialog getInstance() {
        if (sfAlertDialog == null)
            sfAlertDialog = new LiveLocationDialog();
        return sfAlertDialog;
    }

    private LiveLocationDialog() {
    }

    /**
     * show AlertDialog
     */
    public void showAlertDialog(Context context, String strTitle, String strMessage, String strPositiveButtonName, String strNegativeButtonName, String strNeutralButtonName, final AlertDialogIndex dialogFor, final AlertDialogCallbackInterface alertDialogInterface) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        if (strTitle != null)
            alertDialogBuilder.setTitle(strTitle);

        if (strMessage != null)
            alertDialogBuilder.setMessage(strMessage);
        //TODO uncomment
        // alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(strPositiveButtonName, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                if (alertDialogInterface != null)
                    alertDialogInterface.getAlertDialogAction(AlertDialogCallbackInterface.POSITIVE_BUTTON_CLICK, dialogFor, null, LiveLocationDialog.this);
            }
        });
        if (strNegativeButtonName != null) {
            alertDialogBuilder.setNegativeButton(strNegativeButtonName, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    if (alertDialogInterface != null)
                        alertDialogInterface.getAlertDialogAction(AlertDialogCallbackInterface.NEGATIVE_BUTTON_CLICK, dialogFor, null, LiveLocationDialog.this);
                }
            });
        }
        if (strNeutralButtonName != null) {
            alertDialogBuilder.setNeutralButton(strNegativeButtonName, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    if (alertDialogInterface != null)
                        alertDialogInterface.getAlertDialogAction(AlertDialogCallbackInterface.NEUTRAL_BUTTON_CLICK, dialogFor, null, LiveLocationDialog.this);
                }
            });
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
