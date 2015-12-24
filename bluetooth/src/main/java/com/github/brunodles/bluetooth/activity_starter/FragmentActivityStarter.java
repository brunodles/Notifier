package com.github.brunodles.bluetooth.activity_starter;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by bruno on 24/12/15.
 */
public class FragmentActivityStarter implements ActivityStarter {
    Fragment fragment;

    public FragmentActivityStarter(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override public void startActivityForResult(Intent intent, int requestCode) {
        fragment.startActivityForResult(intent, requestCode);
    }
}
