package com.github.brunodles.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by bruno on 17/03/16.
 */
public class PermissionAsker {

    private final Activity activity;
    private final String permission;
    private final int requestCode;
    private final int dialogMessage;

    public PermissionAsker(Activity activity, String permission, int requestCode,
                           @StringRes int dialogMessage) {
        this.activity = activity;
        this.permission = permission;
        this.requestCode = requestCode;
        this.dialogMessage = dialogMessage;
    }

    public void askPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            new AlertDialog.Builder(activity)
                    .setMessage(dialogMessage)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermission();
                        }
                    })
                    .show();
        } else {
            // No explanation needed, we can request the permission.
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    /**
     * This method should be called from the activity to enable this class to manage the request
     * permission result.
     *
     * @return Will return true if the permission was granted.
     */
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                              @NonNull int[] grantResults) {
        return ((requestCode == this.requestCode)
                && (grantResults.length > 0)
                && (grantResults[0] == PackageManager.PERMISSION_GRANTED));
    }
}
