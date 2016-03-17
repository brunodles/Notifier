package com.github.brunodles.bluetooth.listener;

import android.bluetooth.BluetoothDevice;

/**
 * Created by bruno on 17/03/16.
 */
public interface DiscoveryListener {

    void onReceive(BluetoothDevice device);
}
