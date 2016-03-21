package android.bruno.notifier;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.brunodles.bluetooth.BluetoothHelper;
import com.github.brunodles.bluetooth.DeviceHelper;


public class ArduinoService extends IntentService {

    private static final String TAG = "ArduinoService";

    public static final String EXTRA_COLOR_HEX = "EXTRA_COLOR_HEX";
    private BluetoothHelper bluetoothHelper;
    private DeviceHelper device;

    public ArduinoService() {
        super("ArduinoService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothHelper = new BluetoothHelper(this);
        device = createDevice();
        device.openBT();
    }

    private DeviceHelper createDevice() {
        DeviceHelper device = bluetoothHelper.deviceHelper(Application.ARDUINO_BLUETOOTH_ADDRESS);
        if (device == null) return new LogDevice();
        if (BuildConfig.DEBUG) return new LogDevice(device);
        return device;
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
        device.sendData(color);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothHelper.stopDiscovery();
        device.closeBT();
    }
}
