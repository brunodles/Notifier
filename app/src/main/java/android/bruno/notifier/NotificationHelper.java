package android.bruno.notifier;

import android.os.Build;
import android.service.notification.StatusBarNotification;

/**
 * Created by bruno on 24/12/15.
 */
public final class NotificationHelper {

    private NotificationHelper() {
    }

    public static String buildKey(StatusBarNotification sbn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH)
            return sbn.getKey();
        else
            return sbn.getUser() + "|" + sbn.getPackageName() + "|" + sbn.getId() + "|" +
                    sbn.getTag();
    }
}
