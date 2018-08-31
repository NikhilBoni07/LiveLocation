package com.share.livelocation.interfaces;

import android.os.Bundle;

import com.share.livelocation.dialogs.LiveLocationDialog;
import com.share.livelocation.enums.AlertDialogIndex;

public interface AlertDialogCallbackInterface {
    int POSITIVE_BUTTON_CLICK = 1, NEGATIVE_BUTTON_CLICK = 2, NEUTRAL_BUTTON_CLICK = 3;

    void getAlertDialogAction(int buttonClick, AlertDialogIndex dialogFor, Bundle bundle, LiveLocationDialog oicAlertDialog);
}
