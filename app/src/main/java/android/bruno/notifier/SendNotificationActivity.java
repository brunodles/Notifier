package android.bruno.notifier;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import yuku.ambilwarna.AmbilWarnaDialog;


public class SendNotificationActivity extends Activity {

    public static final String TAG = "SendNotificationActivity";
    Button selectColor;
    Button sendNotification;
    Button openSetings;

    int color = 0xff00ff00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        selectColor = (Button) findViewById(R.id.selectColor);
        sendNotification = (Button) findViewById(R.id.sendNotification);
        openSetings = (Button) findViewById(R.id.openSetings);
    }

    @Override
    protected void onStart() {
        super.onStart();

        selectColor.setOnClickListener(new ReflectionListener(this, "selectColor"));
        sendNotification.setOnClickListener(new ReflectionListener(this, "sendNotification"));
        openSetings.setOnClickListener(new ReflectionListener(this, "openSetings"));

        updateSelectColorBackgroundColor();
    }

    private void updateSelectColorBackgroundColor(){
        selectColor.setBackgroundColor(color);
    }

    private void sendNotification(){
        Log.d(TAG, "Prepare to send notification");
        Notification notification = new Notification.Builder(this)
                .setContentText("ContentTExt")
                .setContentTitle("ContentTitle")
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("Ticker")
                .setLights(color, 200, 500)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);
        Log.d(TAG, "Notification sent");
    }

    public void selectColor(){
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, color, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                SendNotificationActivity.this.color = color;
                updateSelectColorBackgroundColor();
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
        });

        dialog.show();
    }

    public void openSetings(){
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);

    }


}
