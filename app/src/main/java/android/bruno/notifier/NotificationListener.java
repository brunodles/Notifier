package android.bruno.notifier;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.service.notification.StatusBarNotification;

/**
 * Created by bruno on 17/08/14.
 */
public class NotificationListener extends android.service.notification.NotificationListenerService {

    public static final String TAG = "NotificationListener";
    private String lastKey;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
//        Log.d(TAG, "NotificationPosted");
        lastKey = NotificationHelper.buildKey(sbn);
        Notification notification = sbn.getNotification();
        sendNotificationToArduino(notification);
    }

    private void sendNotificationToArduino(Notification notification) {
        int color = notification.ledARGB;
        if (color == 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        color = notification.color;
        ColorValues cv = ColorValues.from(color);

//        Log.d(TAG, String.format("Notification colors\nLedColor = %06X\nR = %s, G = %s, B = %s",
//                color, cv.red, cv.green, cv.blue));

        if (cv.isBlack()) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            color = preferences.getInt("led", 0x000000);
//            Log.d(TAG, String.format("Using default color = %06X", color));
            cv = ColorValues.from(color);
        }
        sendColorToArduino(cv.toHexRGB());
    }

    private void sendColorToArduino(final String color) {
        Intent intent = new Intent(this, ArduinoService.class);
        intent.putExtra(ArduinoService.EXTRA_COLOR_HEX, color);
        startService(intent);
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
