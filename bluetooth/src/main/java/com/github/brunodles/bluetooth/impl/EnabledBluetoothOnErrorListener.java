package com.github.brunodles.bluetooth.impl;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import com.github.brunodles.common.ActivityStarter;
import com.github.brunodles.bluetooth.listener.DisabledErrorListener;

/**
 * Created by bruno on 17/03/16.
 */
public class EnabledBluetoothOnErrorListener implements DisabledErrorListener {

    private static final int RC_ENABLE_BLUETOOTH = 87124;
    private ActivityStarter starter;
    private final int rc_enable;

    public EnabledBluetoothOnErrorListener(ActivityStarter starter) {
        this.starter = starter;
        rc_enable = RC_ENABLE_BLUETOOTH;
    }

    public EnabledBluetoothOnErrorListener(int rc_enable, ActivityStarter starter) {
        this.rc_enable = rc_enable;
        this.starter = starter;
    }

    @Override
    public void disabledErrorListener() {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        starter.startActivityForResult(enableBluetooth, rc_enable);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == rc_enable && resultCode == Activity.RESULT_OK) {
            return true;
        } else {
            return false;
        }
    }
}
