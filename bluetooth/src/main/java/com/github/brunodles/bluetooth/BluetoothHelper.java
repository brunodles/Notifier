package com.github.brunodles.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Set;

/**
 * Created by bruno on 23/12/15.
 */
public class BluetoothHelper {
    private static final String TAG = "BluetoothHelper";

    public static final int RC_ENABLE_BLUETOOTH = 0;
    private final ActivityStarter activityStarter;
    private final Context context;
    BluetoothAdapter mBluetoothAdapter;

    public BluetoothHelper(ActivityStarter activityStarter, Context context) {
        this.activityStarter = activityStarter;
        this.context = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private void check() {
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "check: Sorry developer. This device don't have a bluetooth adapter.");
        } else if (!checkPermission()) {
            Log.e(TAG, "check: Need to ask for bluetooth permission.");
        } else if (!mBluetoothAdapter.isEnabled()) {
            requestEnableBluetooth();
        }
    }

    private boolean checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    public void requestEnableBluetooth() {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activityStarter.startActivityForResult(enableBluetooth, RC_ENABLE_BLUETOOTH);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_ENABLE_BLUETOOTH) {
            return true;
        } else {
            return false;
        }
    }

    public DeviceHelper findDevice(String address) {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
            for (BluetoothDevice device : pairedDevices)
                if (device.getAddress().equals(address))
                    return new DeviceHelper(device);
        return null;
    }


}
