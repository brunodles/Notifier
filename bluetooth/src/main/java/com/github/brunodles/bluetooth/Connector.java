package com.github.brunodles.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Handler;
import android.util.Log;


import com.github.brunodles.bluetooth.impl.DeviceHelperDirect;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by bruno on 17/03/16.
 */
final class Connector {
    private static final String TAG = "Connector";

    private Connector() {
    }

    static void connect(BluetoothDevice bluetoothDevice) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bluetoothDevice.createBond();
            return;
        }
        if (connectWithReflection(bluetoothDevice))
            return;
        sendEmptyMessage(bluetoothDevice);
    }

    private static boolean connectWithReflection(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "connectWithReflection: ");
        try {
            Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
            Method createBondMethod = class1.getMethod("createBond");
            return (Boolean) createBondMethod.invoke(bluetoothDevice);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean sendEmptyMessage(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "sendEmptyMessage: ");
        final DeviceHelperDirect device = new DeviceHelperDirect(bluetoothDevice);
        boolean result = false;
        try {
            device.openBT();
            device.sendData("");
            result = true;
        } finally {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    device.closeBT();
                }
            }, 500L);
        }
        return result;
    }
}
