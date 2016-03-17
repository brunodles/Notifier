package com.github.brunodles.bluetooth.impl;

import android.app.Activity;
import android.content.Intent;

import com.github.brunodles.bluetooth.listener.ActivityStarter;

/**
 * Created by bruno on 24/12/15.
 */
public class ActivityActivityStarter implements ActivityStarter {

    Activity activity;

    public ActivityActivityStarter(Activity activity) {
        this.activity = activity;
    }

    @Override public void startActivityForResult(Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }
}
