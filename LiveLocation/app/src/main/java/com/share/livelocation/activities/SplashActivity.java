package com.share.livelocation.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.share.livelocation.R;
import com.share.livelocation.dialogs.LiveLocationDialog;
import com.share.livelocation.enums.AlertDialogIndex;
import com.share.livelocation.interfaces.AlertDialogCallbackInterface;
import com.share.livelocation.interfaces.PermissionRequestCallbackListener;
import com.share.livelocation.utils.PermissionHandler;

import java.util.Calendar;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private Context context;
    private Calendar calendar;
    private final int REQUEST_CODE_ENABLE_GPS = 12;
    private final int PERMISSIONS_REQUEST_CODE = 100;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        context = this;

        TextView tvGreetingMessage = (TextView) findViewById(R.id.tvGreetingMessage);
        calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        // Check current time to show
        if (hour < 12)
            tvGreetingMessage.setText(getString(R.string.good_morning));
        else if (hour >= 12 && hour < 16)
            tvGreetingMessage.setText(getString(R.string.good_afternoon));
        else
            tvGreetingMessage.setText(getString(R.string.good_evening));

        PermissionHandler.getInstance().requestDynamicPermission(this, PERMISSIONS_REQUEST_CODE, new PermissionRequestCallbackListener() {
            @Override
            public void getPermissionData(int permissionAction) {
                if (permissionAction == 1) {
                    showGPSDisabledAlertToUser();
                } else
                    finish();
            }
        });
    }

    private void showGPSDisabledAlertToUser() {
        // Check if GPS is enabled or not
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Show GPS disable alert
            LiveLocationDialog.getInstance().showAlertDialog(context, null, getString(R.string.gps_disable_message), getString(R.string.enable_gps), getString(R.string.exit_app), null, AlertDialogIndex.SIMPLE_ALERT_DIALOG, new AlertDialogCallbackInterface() {
                @Override
                public void getAlertDialogAction(int buttonClick, AlertDialogIndex dialogFor, Bundle bundle, LiveLocationDialog oicAlertDialog) {
                    if (buttonClick == AlertDialogCallbackInterface.POSITIVE_BUTTON_CLICK) {
                        // Open Location setting to enable GPS
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(callGPSSettingIntent, REQUEST_CODE_ENABLE_GPS);
                    } else // Exit App
                        System.exit(0);
                }
            });
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    startActivity(new Intent(context, GoogleSignInActivity.class));
                    finish();

                }
            }, 3000);// Splash screen time
        }
    }

    /**
     * BroadCast Receiver to receive GPS enable-disable updates
     */
    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED"))
                //showGPSDisabledAlertToUser();
                PermissionHandler.getInstance().requestDynamicPermission(SplashActivity.this, PERMISSIONS_REQUEST_CODE, new PermissionRequestCallbackListener() {
                    @Override
                    public void getPermissionData(int permissionAction) {
                        if (permissionAction == 1)
                            showGPSDisabledAlertToUser();
                        else
                            finish();
                    }
                });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check Location service is enable or not
        if (requestCode == REQUEST_CODE_ENABLE_GPS) {
            //showGPSDisabledAlertToUser();
            PermissionHandler.getInstance().requestDynamicPermission(this, PERMISSIONS_REQUEST_CODE, new PermissionRequestCallbackListener() {
                @Override
                public void getPermissionData(int permissionAction) {
                    if (permissionAction == 1)
                        showGPSDisabledAlertToUser();
                    else
                        finish();
                }
            });
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1]/* + grantResults[2] + grantResults[3]*/) == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("===onRequestPermissionsResult");
                    //redirectToNextScreen();
                    showGPSDisabledAlertToUser();
                } else {
                    System.out.println("===onRequestPermissionsResult else");
                    LiveLocationDialog.getInstance().showAlertDialog(this, null, getString(R.string.enable_permission_message), getString(R.string.enable_permission), getString(R.string.cancel), null, AlertDialogIndex.SIMPLE_ALERT_DIALOG, new AlertDialogCallbackInterface() {
                        @Override
                        public void getAlertDialogAction(int buttonClick, AlertDialogIndex dialogFor, Bundle bundle, LiveLocationDialog oicAlertDialog) {
                            if (buttonClick == AlertDialogCallbackInterface.POSITIVE_BUTTON_CLICK) {
                                PermissionHandler.getInstance().redirectToSettingsScreen(SplashActivity.this);
                            } else {
                                //showGPSDisabledAlertToUser();
                                finish();
                            }
                        }

                    });
                }
            }
        }
    }


}
