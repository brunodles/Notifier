package com.github.brunodles.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by bruno on 24/12/15.
 */
public class DeviceHelper {
    private final BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;

    public DeviceHelper(BluetoothDevice device) {
        this.mmDevice = device;
    }

    public void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

//        beginListenForData();
    }

    void beginListenForData() {
        final boolean stopWorker = false;

        Thread workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        readData();
                    } catch (IOException ex) {
//                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }


    public void sendData(String msg) throws IOException {
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
    }

    @Nullable
    public String readData() throws IOException {
        int readBufferPosition = 0;
        byte[] readBuffer = new byte[1024];
        final byte delimiter = 10; //This is the ASCII code for a newline character

        int bytesAvailable = mmInputStream.available();
        if (bytesAvailable > 0) {
            byte[] packetBytes = new byte[bytesAvailable];
            mmInputStream.read(packetBytes);
            for (int i = 0; i < bytesAvailable; i++) {
                byte b = packetBytes[i];
                if (b == delimiter) {
                    byte[] encodedBytes = new byte[readBufferPosition];
                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                    final String data = new String(encodedBytes, "US-ASCII");
                    readBufferPosition = 0;

                    return data;
                } else {
                    readBuffer[readBufferPosition++] = b;
                }
            }
        }
        return null;
    }

    void closeBT() throws IOException {
//        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
    }
}
