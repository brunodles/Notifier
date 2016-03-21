package android.bruno.notifier;

import android.support.annotation.Nullable;
import android.util.Log;

import com.github.brunodles.bluetooth.DeviceHelper;

/**
 * Created by bruno on 18/03/16.
 */
public class LogDevice implements com.github.brunodles.bluetooth.DeviceHelper {
    private static final String TAG = "LogDevice";

    DeviceHelper device;

    public LogDevice() {
    }

    public LogDevice(DeviceHelper device) {
        this.device = device;
    }

    @Override
    public void openBT() {
        Log.d(TAG, "openBT: ");
        if (device != null) device.openBT();
    }

    @Override
    public void sendData(String msg) {
        Log.d(TAG, "sendData: ");
        if (device != null) device.sendData(msg);
    }

    @Nullable
    @Override
    public String readData() {
        Log.d(TAG, "readData: ");
        if (device != null) return device.readData();
        return "";
    }

    @Override
    public void closeBT() {
        Log.d(TAG, "closeBT: ");
        if (device != null) device.closeBT();
    }
}
