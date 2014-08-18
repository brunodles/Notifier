package android.bruno.notifier;

import android.app.Notification;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import at.abraxas.amarino.Amarino;

/**
 * Created by bruno on 17/08/14.
 */
public class NotificationListener extends android.service.notification.NotificationListenerService {

    public static final String TAG = "NotificationListener";
//    private Handler handler;
//    private String lastKey;


    @Override
    public void onCreate() {
        super.onCreate();
        Amarino.connect(this, Application.ARDUINO_BLUETOOTH_ADDRESS);
//        handler = new Handler();
    }

    @Override
    public void onDestroy() {
        Amarino.disconnect(this, Application.ARDUINO_BLUETOOTH_ADDRESS);
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "NotificationPosted");
//        lastKey = sbn.getKey();
        Notification notification = sbn.getNotification();
        sendNotificationToArduino(notification);
    }

    private void sendNotificationToArduino(Notification notification) {
        int ledARGB = notification.ledARGB;
        int red = Color.red(ledARGB);
        int green = Color.green(ledARGB);
        int blue = Color.blue(ledARGB);
        Log.d(TAG, String.format("LedColor = %s, R = %s, G = %s, B = %s",
                ledARGB, red, green, blue));

        Amarino.sendDataToArduino(this, Application.ARDUINO_BLUETOOTH_ADDRESS, 'R', red);
        Amarino.sendDataToArduino(this, Application.ARDUINO_BLUETOOTH_ADDRESS, 'G', green);
        Amarino.sendDataToArduino(this, Application.ARDUINO_BLUETOOTH_ADDRESS, 'B', blue);

//        handler.postDelayed(new ReflectionRunable(this, "clearArduino"), 5000);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
//        if (lastKey!=null && lastKey.equals(sbn.getKey()))
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

    private void clearArduino(){
        Amarino.sendDataToArduino(this, Application.ARDUINO_BLUETOOTH_ADDRESS, 'R', 0);
        Amarino.sendDataToArduino(this, Application.ARDUINO_BLUETOOTH_ADDRESS, 'G', 0);
        Amarino.sendDataToArduino(this, Application.ARDUINO_BLUETOOTH_ADDRESS, 'B', 0);
//        handler.postDelayed(new ReflectionRunable(this, "checkTopNotification"), 1000);
    }


}
