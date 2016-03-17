package android.bruno.notifier;

import android.app.Activity;
import android.app.Notification;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.github.brunodles.bluetooth.BluetoothHelper;
import com.github.brunodles.bluetooth.listener.DiscoveryListener;

import yuku.ambilwarna.AmbilWarnaDialog;


public class SendNotificationActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String TAG = "SendNotificationAct";
    Button selectLedColor;
    Button sendNotification;
    Button openSetings;
    Button sendColor;
    private Button find;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private BluetoothHelper bluetoothHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        selectLedColor = (Button) findViewById(R.id.selectLedColor);
        sendNotification = (Button) findViewById(R.id.sendNotification);
        openSetings = (Button) findViewById(R.id.openSetings);
        sendColor = (Button) findViewById(R.id.sendColor);
        find = (Button) findViewById(R.id.find);
        listView = (ListView) findViewById(android.R.id.list);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(adapter);

        selectLedColor.setTag("led");
    }

    @Override
    protected void onStart() {
        super.onStart();
        selectLedColor.setOnClickListener(this);
        sendNotification.setOnClickListener(this);
        openSetings.setOnClickListener(this);
        sendColor.setOnClickListener(this);
        find.setOnClickListener(this);

        listView.setOnItemClickListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        selectLedColor.setBackgroundColor(preferences.getInt("led", 0xFFFFFFFF));
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick() called with: " + "view = [" + view + "]");
        switch (view.getId()) {
            case R.id.selectLedColor:
                selectLedColorClick(view);
                break;
            case R.id.sendNotification:
                sendNotification();
                break;
            case R.id.sendColor:
                sendColor();
                break;
            case R.id.openSetings:
                openSetings();
                break;
            case R.id.find:
                findDevice();
                break;
        }
    }

    private void findDevice() {
        Log.d(TAG, "device: start");
        bluetoothHelper = new BluetoothHelper(this);
        adapter.clear();
        adapter.notifyDataSetChanged();
        boolean b = bluetoothHelper.startDiscovery(new DiscoveryListener() {
            @Override
            public void onReceive(BluetoothDevice device) {
                Log.d(TAG, String.format("device: receive %s - %s", device.getName(), device.getAddress()));
                adapter.add(String.format("%16s - %s", device.getAddress(), device.getName()));
                adapter.notifyDataSetChanged();
            }
        });
        Log.d(TAG, "device: start " + b);
        if (b) new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "device: stop");
                bluetoothHelper.stopDiscovery();
            }
        }, 12000);
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

    private void sendColor() {
        Log.d(TAG, "sendColor: ");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ColorValues color = ColorValues.from(preferences.getInt("led", 0x000000));
        Intent intent = new Intent(this, ArduinoService.class);
        intent.putExtra(ArduinoService.EXTRA_COLOR_HEX, color.toHexRGB());
        Log.d(TAG, "sendColor: " + color.toHexRGB());
        startService(intent);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice bluetoothDevice = bluetoothHelper.getLastDiscoveryDeviceList().get(position);
        bluetoothHelper.pair(bluetoothDevice);
    }
}
