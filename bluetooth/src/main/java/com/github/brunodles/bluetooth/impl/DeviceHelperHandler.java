package com.github.brunodles.bluetooth.impl;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.brunodles.bluetooth.DeviceHelper;

/**
 * Created by bruno on 17/03/16.
 */
public class DeviceHelperHandler implements DeviceHelper {
    private static final String TAG = "DeviceHelperHandler";

    private static final int OPEN_BT = 10;
    private static final int SEND_DATA = 11;
    private static final int CLOSE_BT = 13;
    private static final String EXTRA_STRING = "EXTRA_STRING";

    private static final long CLOSE_DELAY_MILIS = 500L;

    private final HandlerThread handlerThread;
    private final MyHandler handler;

    private final DeviceHelperDirect direct;

    public DeviceHelperHandler(BluetoothDevice device) {
        handlerThread = new HandlerThread("DeviceHelperHandler-" + device.getAddress());
        handlerThread.start();
        direct = new DeviceHelperDirect(device);
        handler = new MyHandler(handlerThread.getLooper(), direct);
    }

    @Override
    public void openBT() {
        handler.sendEmptyMessage(OPEN_BT);
    }

    @Override
    public void sendData(String msg) {
        Message m = new Message();
        m.what = SEND_DATA;
        Bundle b = new Bundle();
        b.putString(EXTRA_STRING, msg);
        m.setData(b);
        handler.sendMessage(m);
        rescheduleCloseBtIfIsOnQueue();
    }

    private void rescheduleCloseBtIfIsOnQueue() {
        if (handler.hasMessages(CLOSE_BT)) {
            handler.removeMessages(CLOSE_BT);
            closeBT();
        }
    }

    @Nullable
    @Override
    public String readData() {
        rescheduleCloseBtIfIsOnQueue();
        return direct.readData();
    }

    @Override
    public void closeBT() {
        if (handler.hasMessages(CLOSE_BT)) handler.removeMessages(CLOSE_BT);
        handler.sendEmptyMessageDelayed(CLOSE_BT, CLOSE_DELAY_MILIS);
    }

    private static class MyHandler extends Handler {

        DeviceHelper helper;
        private long lastData = 0L;

        public MyHandler(Looper looper, DeviceHelper helper) {
            super(looper);
            this.helper = helper;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OPEN_BT:
                    helper.openBT();
                    lastData = System.currentTimeMillis();
                    return;
                case SEND_DATA:
                    lastData = System.currentTimeMillis();
                    Bundle b = msg.getData();
                    helper.sendData(b.getString(EXTRA_STRING, ""));
                    lastData = System.currentTimeMillis();
                    return;
                case CLOSE_BT:
                    long closeTime = lastData + CLOSE_DELAY_MILIS;
                    if (System.currentTimeMillis() > closeTime) {
                        helper.closeBT();
                    } else {
                        this.removeMessages(CLOSE_BT);
                        this.sendEmptyMessageDelayed(CLOSE_BT, CLOSE_DELAY_MILIS);
                    }
                    return;
            }
        }
    }
}
