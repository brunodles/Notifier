package com.github.brunodles.bluetooth;

import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by bruno on 17/03/16.
 */
public interface DeviceHelper {
    void openBT() throws IOException;

    void sendData(String msg) throws IOException;

    @Nullable
    String readData() throws IOException;

    void closeBT() throws IOException;
}
