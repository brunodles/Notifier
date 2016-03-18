package com.github.brunodles.bluetooth.impl;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.brunodles.bluetooth.DeviceHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * This class will help you to send and receive messages.
 * <p/>
 * Created by bruno on 24/12/15.
 */
public class DeviceHelperDirect implements DeviceHelper {

    private static final String TAG = "DeviceHelperDirect";

    private final BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;

    public DeviceHelperDirect(BluetoothDevice device) {
        this.mmDevice = device;
    }

    @Override
    public void openBT() {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        try {
//            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = getOutputStream();
            mmInputStream = mmSocket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Hey dev, looks like we got one exception on openBT, so, I'll try to close it: ", e);
            closeBT();
//            throw e;
        }

//        beginListenForData();
    }

    private OutputStream getOutputStream() throws IOException {
        if (mmOutputStream == null)
            mmOutputStream = mmSocket.getOutputStream();
        return mmOutputStream;
    }

    void beginListenForData() {
        final boolean stopWorker = false;

        Thread workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    readData();
                }
            }
        });

        workerThread.start();
    }


    @Override
    public void sendData(String msg) {
        msg += "\n";
        try {
            if (!mmSocket.isConnected()) openBT();
            getOutputStream().write(msg.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Hey dev, looks like we got one exception here, sendData: ", e);
        }
    }

    @Override
    @Nullable
    public String readData() {
        try {
            return getString();
        } catch (IOException e) {
            Log.e(TAG, "Hey dev, looks like we got one exception here, readData: ", e);
        }
        return null;
    }

    @Nullable
    private String getString() throws IOException {
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

    @Override
    public void closeBT() {
//        stopWorker = true;
        try {
            if (mmOutputStream != null) mmOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Hey dev, looks like we got one exception here, closeBT: ", e);
        }
        try {
            if (mmInputStream != null) mmInputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Hey dev, looks like we got one exception here, closeBT: ", e);
        }
        try {
            if (mmSocket != null) mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Hey dev, looks like we got one exception here, closeBT: ", e);
        }
    }
}
