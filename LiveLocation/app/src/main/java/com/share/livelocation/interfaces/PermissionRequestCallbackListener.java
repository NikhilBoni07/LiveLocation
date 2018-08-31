package com.share.livelocation.interfaces;

public interface PermissionRequestCallbackListener {

    int PERMISSION_GRANTED = 1, PERMISSION_CANCELLED = 2;

    void getPermissionData(int permissionAction);
}