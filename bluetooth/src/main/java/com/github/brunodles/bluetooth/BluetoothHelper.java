package com.github.brunodles.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.brunodles.bluetooth.exception.AdapterNotFoundException;
import com.github.brunodles.bluetooth.impl.DeviceHelperDirect;
import com.github.brunodles.bluetooth.listener.AdminPermissionErrorListener;
import com.github.brunodles.bluetooth.listener.DisabledErrorListener;
import com.github.brunodles.bluetooth.listener.DiscoveryListener;
import com.github.brunodles.bluetooth.listener.PermissionErrorListener;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.github.brunodles.bluetooth.Connector.connect;
import static com.github.brunodles.common.PermissionChecker.checkPermission;


/**
 * Created by bruno on 23/12/15.
 */
public class BluetoothHelper {
    private static final String TAG = "BluetoothHelper";

    private PermissionErrorListener permissionErrorListener;
    private DisabledErrorListener DisabledErrorListener;
    private AdminPermissionErrorListener adminPermissionErrorListener;

    private final Context context;
    private DiscoveryReceiver discoveryReceiver;
    @Nullable public final BluetoothAdapter mBluetoothAdapter;

    /**
     * Here I'll try to get the default {@link BluetoothAdapter} from the device,
     * if it doesn't exists I'll throw a {@link RuntimeException} and you won't be able to use
     * this class since I can't access the bluetooth adapter.
     *
     * @param context I need it, to check the permissions and to bind a intentReceiver.
     * @throws AdapterNotFoundException if can't get the default {@link BluetoothAdapter}
     */
    public BluetoothHelper(Context context) {
        this.context = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
            throw new AdapterNotFoundException();
    }

    /**
     * Create a new DeviceHelper for a Bonded device.
     * To use it you
     *
     * @param address the Address of the wanted device
     * @return a {@link DeviceHelperDirect}, or null if the device don't exist on bonded device list.
     */
    @Nullable
    public DeviceHelper deviceHelper(String address) {
        if (address == null) return null;
        if (needUserInteraction()) return null;
        BluetoothDevice device = getBluetoothDevice(address);
        if (device == null) return null;
        return new DeviceHelperDirect(device);
    }

    @Nullable
    public DeviceHelper deviceHelper(BluetoothDevice device) {
        if (device == null) return null;
        if (needUserInteraction()) return null;
        return new DeviceHelperDirect(device);
    }

    private boolean needUserInteraction() {
        if (!checkPermission(context, Manifest.permission.BLUETOOTH)) {
            askForBluetoothPermission();
            return true;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            requestEnableBluetooth();
            return true;
        }
        return false;
    }

    private void askForBluetoothPermission() {
        if (permissionErrorListener == null)
            Log.e(TAG, "Hey dev, you need to ask for bluetooth permission. To handle this error you need to set a PermissionErrorListener.");
        else
            permissionErrorListener.needBluetoothPermission();
    }

    private void requestEnableBluetooth() {
        if (DisabledErrorListener == null)
            Log.e(TAG, "Hey dev, you need to ask to enable bluetooth. To handle this error you need to set a DisabledErrorListener.");
        else
            DisabledErrorListener.disabledErrorListener();
    }

    @Nullable
    private BluetoothDevice getBluetoothDevice(String address) {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
            for (BluetoothDevice device : pairedDevices)
                if (device.getAddress().equals(address))
                    return device;
        return null;
    }

    public boolean startDiscovery() {
        return startDiscovery(null);
    }

    public boolean startDiscovery(DiscoveryListener listener) {
        if (!checkPermission(context, Manifest.permission.BLUETOOTH_ADMIN)) {
            askForBluetoothAdminPermission();
            return false;
        }
        if (mBluetoothAdapter.isDiscovering()) mBluetoothAdapter.cancelDiscovery();
        if (!mBluetoothAdapter.isEnabled()) {
            requestEnableBluetooth();
            return false;
        }
        registerReceiver(listener);
        boolean b = mBluetoothAdapter.startDiscovery();
        if (!b) unregisterReceiver();
        return b;
    }

    private void askForBluetoothAdminPermission() {
        if (adminPermissionErrorListener == null)
            Log.e(TAG, "Hey dev, you need to ask for bluetooth permission. To handle this error you need to set a PermissionErrorListener.");
        else
            adminPermissionErrorListener.needBluetoothAdminPermission();
    }

    public void registerReceiver(DiscoveryListener listener) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryReceiver = new DiscoveryReceiver();
        if (listener != null) discoveryReceiver.setListener(listener);
        context.registerReceiver(discoveryReceiver, filter);
    }

    public boolean stopDiscovery() {
        if (discoveryReceiver == null) return true;
        boolean b = mBluetoothAdapter.cancelDiscovery();
        unregisterReceiver();
        return b;
    }

    public void unregisterReceiver() {
        context.unregisterReceiver(discoveryReceiver);
    }

    public List<BluetoothDevice> getLastDiscoveryDeviceList() {
        if (discoveryReceiver == null)
            return Collections.emptyList();
        return Collections.unmodifiableList(discoveryReceiver.getDeviceList());
    }

    public void pair(BluetoothDevice device) {
        if (mBluetoothAdapter.isDiscovering())
            stopDiscovery();
        connect(device);
    }
}
