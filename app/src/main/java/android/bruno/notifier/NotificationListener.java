package android.bruno.notifier;

import android.app.Notification;
import android.graphics.Color;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.github.brunodles.bluetooth.BluetoothHelper;
import com.github.brunodles.bluetooth.DeviceHelper;

import java.io.IOException;

/**
 * Created by bruno on 17/08/14.
 */
public class NotificationListener extends android.service.notification.NotificationListenerService {

    public static final String TAG = "NotificationListener";
    private String lastKey;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "NotificationPosted");
        lastKey = NotificationHelper.buildKey(sbn);
        Notification notification = sbn.getNotification();
        sendNotificationToArduino(notification);
    }

    private void sendNotificationToArduino(Notification notification) {
        int color = notification.ledARGB;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            if (color == 0)
                color = notification.color;
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            Log.d(TAG, String.format("Notification colors\nLedColor = %06X\ncolor = %06X\nR = %s, G = %s, B = %s",
                    notification.ledARGB, notification.color, red, green, blue));

        sendColorToArduino(String.format("#%02x%02x%02x", red, green, blue));
    }

    private void sendColorToArduino(String color) {
        BluetoothHelper bluetoothHelper = new BluetoothHelper(null, this);
        final DeviceHelper device = bluetoothHelper
                .findDevice(Application.ARDUINO_BLUETOOTH_ADDRESS);
        if (device != null) {
            bluetoothHelper.mBluetoothAdapter.cancelDiscovery();
            try {
                device.openBT();
                Log.d(TAG, "sendColorToArduino: sendColor " + color);
                device.sendData(color);
            } catch (IOException e) {
                Log.e(TAG, "sendColorToArduino: ", e);
            }
            Thread thread = new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                    try {
                        device.closeBT();
                    } catch (IOException e1) {
                        Log.e(TAG, "sendColorToArduino.run: ", e1);
                    }
                }
            });
            thread.start();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (lastKey != null && lastKey.equals(NotificationHelper.buildKey(sbn))) clearArduino();
        checkTopNotification();
    }

    public void checkTopNotification() {
        StatusBarNotification[] notifications = getActiveNotifications();
        StatusBarNotification topNotification = null;
        int topPriority = Notification.PRIORITY_MIN;
        for (StatusBarNotification notification : notifications) {
            if (notification.getNotification().priority > topPriority) {
                topNotification = notification;
                topPriority = notification.getNotification().priority;
                continue;
            }
        }
        if (topNotification != null)
            sendNotificationToArduino(topNotification.getNotification());
    }

    public void clearArduino() {
        sendColorToArduino("#000000");
    }
}
