package android.bruno.notifier;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.brunodles.bluetooth.BluetoothHelper;
import com.github.brunodles.bluetooth.impl.DeviceHelperDirect;

import java.io.IOException;


public class ArduinoService extends IntentService {

    private static final String TAG = "ArduinoService";

    public static final String EXTRA_COLOR_HEX = "EXTRA_COLOR_HEX";
    private BluetoothHelper bluetoothHelper;

    public ArduinoService() {
        super("ArduinoService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothHelper = new BluetoothHelper(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent() called with: " + "intent = [" + intent + "]");
        if (intent != null) {
            Bundle extras = intent.getExtras();
            sendColorToArduino(extras.getString(EXTRA_COLOR_HEX, "#000000"));
        }
    }

    private void sendColorToArduino(final String color) {
        Log.d(TAG, "sendColorToArduino() called with: " + "color = [" + color + "]");
        DeviceHelperDirect device = bluetoothHelper.deviceHelper(Application.ARDUINO_BLUETOOTH_ADDRESS);
        if (device == null) return;
        try {
            device.openBT();
            device.sendData(color);
        } catch (IOException e) {
            Log.e(TAG, "onCreate: ", e);
        }
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
        }
        try {
            device.closeBT();
        } catch (IOException e) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothHelper.stopDiscovery();
    }
}
