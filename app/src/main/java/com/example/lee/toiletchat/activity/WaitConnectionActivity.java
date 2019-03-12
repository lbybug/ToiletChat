package com.example.lee.toiletchat.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lee.toiletchat.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import adapter.BluetoothDeviceAdapter;
import butterknife.BindView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import utils.BluetoothClientUtils;
import utils.ChatUtils;
import utils.LoggerUtils;

@RuntimePermissions
public class WaitConnectionActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "WaitConnectionActivity";
    public static final int REQUEST_BLUETOOTH = 0x04;
    public static final int CONNECT_SUCCESS = 0x05;

    public int platform = 0;

    public BluetoothAdapter bluetoothAdapter;

    public BluetoothServerSocket serverSocket;

    public BluetoothSocket socket;

    public BluetoothClientUtils bluetoothClientUtils;

    public ConnectHandler connectHandler;

    public String serviceName = "btspp";
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @BindView(R.id.btBluetoothScan)
    Button btBluetoothScan;
    @BindView(R.id.bluetoothDevices)
    ListView bluetoothDevices;
    @BindView(R.id.clientLayout)
    LinearLayout clientLayout;
    @BindView(R.id.clientStatus)
    TextView clientStatus;
    @BindView(R.id.serverLayout)
    RelativeLayout serverLayout;


    private Set<BluetoothDevice> bondedDevices;
    private List<BluetoothDevice> pairedDevices;
    private List<BluetoothDevice> newDevices;

    public BluetoothDeviceAdapter bluetoothDeviceAdapter;


    @Override
    public void initData() {
        platform = getIntent().getIntExtra("platform", 0);
        LoggerUtils.d(String.valueOf(platform));
        WaitConnectionActivityPermissionsDispatcher.locationPermissionWithCheck(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wait_connection;
    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            LoggerUtils.d("没有蓝牙");
            return;
        }
        if (bluetoothAdapter.isEnabled()) {
            checkPlatform();
        } else {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_BLUETOOTH);
        }
    }

    public void checkPlatform() {
        connectHandler = new ConnectHandler(this);
        if (platform == 0) { //服务端
            clientLayout.setVisibility(View.GONE);
            waitClient();
        } else { //客户端
            bluetoothClientUtils = new BluetoothClientUtils(this);
            serverLayout.setVisibility(View.GONE);
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(findBluetooth, filter);
            pairedDevices = new ArrayList<>();
            newDevices = new ArrayList<>();
            bluetoothDeviceAdapter = new BluetoothDeviceAdapter(pairedDevices, newDevices, this);
            bluetoothDevices.setAdapter(bluetoothDeviceAdapter);
            bluetoothDevices.setOnItemClickListener(this);
            getBoundDevices();
        }
    }

    private void waitClient() {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (serverSocket == null) {
                        serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(serviceName, MY_UUID_SECURE);
                        LoggerUtils.d("开始监听");
                        socket = serverSocket.accept();
                        LoggerUtils.d("有客户端介入" + socket.getRemoteDevice().getName());
                        LoggerUtils.d("套接字是否为空：" + (socket == null));
                        ChatUtils.setBluetoothSocket(socket);
                        connectHandler.obtainMessage(CONNECT_SUCCESS).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 获取已经配对过的蓝牙
     */
    private void getBoundDevices() {
        bondedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            pairedDevices.add(device);
        }
        bluetoothDeviceAdapter.notifyDataSetChanged();
        startDiscoveryDevices();
    }

    /**
     * 搜索新的设备
     */
    private void startDiscoveryDevices() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
        btBluetoothScan.setText("扫描中");

    }

    public static void actionStart(Context context, int type) { //外部启动
        Intent intent = new Intent(context, WaitConnectionActivity.class);
        intent.putExtra("platform", type);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    checkPlatform();
                } else {
                    LoggerUtils.d("请打开蓝牙");
                }
                break;
        }
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void locationPermission() {
        initBluetooth();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WaitConnectionActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void showLocationPermission(final PermissionRequest request) {
        request.proceed();
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void deniedLocationPermission() {
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void neverAskLocationPermission() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (platform == 1) {
            unregisterReceiver(findBluetooth);
            //关闭广播
        }
        if (connectHandler != null) {
            connectHandler.removeCallbacksAndMessages(null);
            connectHandler = null;
        }
    }

    public BroadcastReceiver findBluetooth = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        newDevices.add(device);
                        bluetoothDeviceAdapter.notifyDataSetChanged();
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    btBluetoothScan.setText("扫描结束");
                    break;
                default:
                    break;
            }
        }
    };

    @OnClick(R.id.btBluetoothScan)
    public void onViewClicked() {
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == 0 || i == pairedDevices.size() + 1) {
            return;
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        BluetoothDevice device;
        if (i < pairedDevices.size() + 1) { //点击配对设备
            device = pairedDevices.get(i - 1);
        } else {
            device = newDevices.get(i - pairedDevices.size() - 2);
        }
        String address = device.getAddress();
        bluetoothClientUtils.setListener(new BluetoothClientUtils.ClientListener() {
            @Override
            public void onStart() {
                LoggerUtils.d("connect start");
            }

            @Override
            public void onConnecting() {
                LoggerUtils.d("connecting...");
            }

            @Override
            public void onSuccess(BluetoothSocket socket) {
                LoggerUtils.d("connect success,the socket is null ? " + (socket == null));
                ChatUtils.setBluetoothSocket(socket);
                connectHandler.obtainMessage(CONNECT_SUCCESS).sendToTarget();
            }

            @Override
            public void onFailed() {
                LoggerUtils.d("connect failed");
            }
        });
        bluetoothClientUtils.connect(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address), true);
    }

    public class ConnectHandler extends Handler{

        public WeakReference<WaitConnectionActivity> waitConnectionActivityWeakReference;

        public ConnectHandler(WaitConnectionActivity waitConnectionActivityWeakReference) {
            this.waitConnectionActivityWeakReference = new WeakReference<>(waitConnectionActivityWeakReference);
        }

        @Override
        public void handleMessage(Message msg) {
            WaitConnectionActivity activity = waitConnectionActivityWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case CONNECT_SUCCESS:
                        LoggerUtils.d("Successful docking");
                        ChatActivity.actionStart(WaitConnectionActivity.this);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
