package com.github.brunodles.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.brunodles.bluetooth.listener.DiscoveryListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruno on 17/03/16.
 */
class DiscoveryReceiver extends BroadcastReceiver {

    private List<BluetoothDevice> deviceList = new ArrayList<>();
    //    private List<DiscoveryListener> listeners = new ArrayList<>();
    private DiscoveryListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
//        Log.d("DiscoveryReceiver", "onReceive: action "+ action);
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            deviceList.add(device);
            notifyListener(device);
        }
    }

    private void notifyListener(BluetoothDevice device) {
//        for (int i = 0; i < listeners.size(); i++)
//            listeners.get(i).onReceive(device);
        if (listener != null) listener.onReceive(device);
    }

    public List<BluetoothDevice> getDeviceList() {
        return deviceList;
    }

    public void setListener(DiscoveryListener listener) {
        this.listener = listener;
    }
}
