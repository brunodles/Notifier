package com.github.brunodles.bluetooth;

import android.support.annotation.Nullable;

/**
 * Created by bruno on 17/03/16.
 */
public interface DeviceHelper {
    void openBT();

    void sendData(String msg);

    @Nullable
    String readData();

    void closeBT();
}
