package android.bruno.notifier;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by bruno on 17/03/16.
 */
public class DeviceAdapter extends BaseAdapter {

    ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public static final class ViewHolder {

    }
}
