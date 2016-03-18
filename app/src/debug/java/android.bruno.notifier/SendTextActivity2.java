package android.bruno.notifier;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.brunodles.bluetooth.BluetoothHelper;
import com.github.brunodles.bluetooth.DeviceHelper;
import com.github.brunodles.bluetooth.impl.DeviceHelperDirect;

import java.io.IOException;

public class SendTextActivity2 extends Activity {
    private static final String TAG = "SettingsActivity";
    TextView myLabel;
    EditText myTextbox;
    private BluetoothHelper bluetoothHelper;
    private DeviceHelper device;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_text);

        Button openButton = (Button) findViewById(R.id.open);
        Button sendButton = (Button) findViewById(R.id.send);
        Button closeButton = (Button) findViewById(R.id.close);
        myLabel = (TextView) findViewById(R.id.label);
        myTextbox = (EditText) findViewById(R.id.entry);

        //Open Button
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    findBT();
                    openBT();
                } catch (IOException ex) {
                    Log.e(TAG, "onClick: ", ex);
                }
            }
        });

        //Send Button
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    sendData();
                } catch (IOException ex) {
                    Log.e(TAG, "onClick: ", ex);
                }
            }
        });

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    closeBT();
                } catch (IOException ex) {
                }
            }
        });

        bluetoothHelper = new BluetoothHelper(this);
    }

    void findBT() {
        device = bluetoothHelper.deviceHelper(Application.ARDUINO_BLUETOOTH_ADDRESS);
        if (device != null) myLabel.setText("Bluetooth Device Found");
    }

    void openBT() throws IOException {
        device.openBT();
        myLabel.setText("Bluetooth Opened");
    }

    void sendData() throws IOException {
        Log.d(TAG, "sendData: ");
        if (device == null) {
            findBT();
            openBT();
        }
        device.sendData(myTextbox.getText().toString());
        myLabel.setText("Data Sent");
    }

    void closeBT() throws IOException {
        if (device != null) device.closeBT();
        myLabel.setText("Bluetooth Closed");
    }
}
