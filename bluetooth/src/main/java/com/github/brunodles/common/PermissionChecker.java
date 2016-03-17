package com.github.brunodles.common;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by bruno on 17/03/16.
 */
public final class PermissionChecker {

    private PermissionChecker() {
    }

    public static boolean checkPermission(Context context, String bluetooth) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, bluetooth);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }
}
