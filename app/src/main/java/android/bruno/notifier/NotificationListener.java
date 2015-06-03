package android.bruno.notifier;

import android.app.Notification;
import android.graphics.Color;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import at.abraxas.amarino.Amarino;

/**
 * Created by bruno on 17/08/14.
 */
public class NotificationListener extends android.service.notification.NotificationListenerService {

    public static final String TAG = "NotificationListener";
    public static final int MIN_COLOR_VALUE = 50;
    private String lastKey;


    @Override
    public void onCreate() {
        super.onCreate();
        Amarino.connect(this, Application.ARDUINO_BLUETOOTH_ADDRESS);
    }

    @Override
    public void onDestroy() {
        Amarino.disconnect(this, Application.ARDUINO_BLUETOOTH_ADDRESS);
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "NotificationPosted");
        lastKey = sbn.getKey();
        Notification notification = sbn.getNotification();
        sendNotificationToArduino(notification);
    }

    private void sendNotificationToArduino(Notification notification) {
        int ledARGB = notification.ledARGB;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            if (ledARGB == 0)
                ledARGB = notification.color;
        int red = Color.red(ledARGB);
        int green = Color.green(ledARGB);
        int blue = Color.blue(ledARGB);

        if (red < MIN_COLOR_VALUE && blue < MIN_COLOR_VALUE && green < MIN_COLOR_VALUE){
            red = 100;
            green = 100;
            blue = 100;
        }
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            Log.d(TAG, String.format("Notification colors\nLedColor = %06X\ncolor = %06X\nR = %s, G = %s, B = %s",
                notification.ledARGB, notification.color, red, green, blue));

        Amarino.sendDataToArduino(this, Application.ARDUINO_BLUETOOTH_ADDRESS, 'R', red);
        Amarino.sendDataToArduino(this, Application.ARDUINO_BLUETOOTH_ADDRESS, 'G', green);
        Amarino.sendDataToArduino(this, Application.ARDUINO_BLUETOOTH_ADDRESS, 'B', blue);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (lastKey!=null && lastKey.equals(sbn.getKey()))
            clearArduino();
    }

    public void checkTopNotification(){
        StatusBarNotification[] notifications = getActiveNotifications();
        StatusBarNotification topNotification = null;
        int topPriority = 0;
        for (StatusBarNotification notification: notifications){
            if (topNotification == null) {
                topNotification = notification;
                topPriority = topNotification.getNotification().priority;
                continue;
            } else if (notification.getNotification().priority > topPriority){
                topNotification = notification;
                topPriority = topNotification.getNotification().priority;
                continue;
            }
        }
        if (topNotification != null)
            sendNotificationToArduino(topNotification.getNotification());
    }

    public void clearArduino(){
        Amarino.sendDataToArduino(this, Application.ARDUINO_BLUETOOTH_ADDRESS, 'R', 0);
        Amarino.sendDataToArduino(this, Application.ARDUINO_BLUETOOTH_ADDRESS, 'G', 0);
        Amarino.sendDataToArduino(this, Application.ARDUINO_BLUETOOTH_ADDRESS, 'B', 0);
    }


}
