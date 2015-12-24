package android.bruno.notifier;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import yuku.ambilwarna.AmbilWarnaDialog;


public class SendNotificationActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "SendNotificationAct";
    Button selectLedColor;
    Button sendNotification;
    Button openSetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        selectLedColor = (Button) findViewById(R.id.selectLedColor);
        sendNotification = (Button) findViewById(R.id.sendNotification);
        openSetings = (Button) findViewById(R.id.openSetings);

        selectLedColor.setTag("led");
    }

    @Override
    protected void onStart() {
        super.onStart();
        selectLedColor.setOnClickListener(this);
        sendNotification.setOnClickListener(this);
        openSetings.setOnClickListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        selectLedColor.setBackgroundColor(preferences.getInt("led", 0xFFFFFFFF));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selectLedColor:
                selectLedColorClick(view);
                break;
            case R.id.sendNotification:
                sendNotification();
                break;
            case R.id.openSetings:
                openSetings();
                break;
        }
    }

    private void sendNotification() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(TAG, "Prepare to send notification");
        Notification notification = new NotificationCompat.Builder(this)
                .setContentText("ContentTExt")
                .setContentTitle("ContentTitle")
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("Ticker")
                .setLights(preferences.getInt("led", 0x00000000), 200, 500)
                .setColor(preferences.getInt("led", 0x00000000))
                .build();

        NotificationManagerCompat.from(this).notify(0, notification);
        Log.d(TAG, "Notification sent");
    }

    public void selectLedColorClick(final View view) {
        final String id = (String) view.getTag();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, preferences.getInt(id, 0xFFFFFFFF), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                preferences
                        .edit()
                        .putInt(id, color)
                        .commit();
                view.setBackgroundColor(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }
        });

        dialog.show();
    }

    public void openSetings() {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);
    }
}
