package adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lee.toiletchat.R;

import java.util.List;


/**
 * 作者： Circle
 * 创造于 2018/5/24.
 */
public class BluetoothDeviceAdapter extends BaseAdapter {

    private List<BluetoothDevice> pairedDevices;
    private List<BluetoothDevice> newDevices;
    private Context mContext;
    private static final int TITLE = 0;
    private static final int CONTENT = 1;

    public BluetoothDeviceAdapter(List<BluetoothDevice> lists, List<BluetoothDevice> new_device, Context context) {
        pairedDevices = lists;
        newDevices = new_device;
        mContext = context;
    }

    @Override
    public int getCount() {
        return pairedDevices.size() + newDevices.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TITLE:
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_bluetooth_devices, parent, false);
                TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                if (position == 0) {
                    tv_title.setText("已配对:");
                } else {
                    tv_title.setText("未配对:");
                }
                break;
            case CONTENT:
                String blueName = null;
                String address = null;
                if (position < pairedDevices.size() + 1) {
                    blueName = pairedDevices.get(position - 1).getName();
                    address = pairedDevices.get(position - 1).getAddress();
                }
                if (position > pairedDevices.size() + 1 && newDevices.size() > 0) {
                    blueName = newDevices.get(position - pairedDevices.size() - 2).getName();
                    address = newDevices.get(position - pairedDevices.size() - 2).getAddress();
                }
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_new_bluetooth, parent, false);
                TextView tvName = convertView.findViewById(R.id.b_name);
                TextView tvMac = convertView.findViewById(R.id.b_mac);
                tvName.setText(blueName);
                tvMac.setText(address);
                break;
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if ((position == pairedDevices.size() + 1) || (position == 0)) {
            return TITLE;
        } else {
            return CONTENT;
        }
    }
}
