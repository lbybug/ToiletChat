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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lee.toiletchat.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import adapter.BluetoothDeviceAdapter;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import utils.LoggerUtils;

@RuntimePermissions
public class WaitConnectionActivity extends BaseActivity {

    public static final int REQUEST_BLUETOOTH = 0x04;

    public int platform = 0;

    public BluetoothAdapter bluetoothAdapter;

    public BluetoothServerSocket serverSocket;

    public BluetoothSocket socket;

    public String serviceName = "btspp";
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Button btBluetoothScan;
    ListView bluetoothDevices;
    LinearLayout serverLayout;
    TextView clientStatus;
    RelativeLayout clientLayout;


    private Set<BluetoothDevice> bondedDevices;
    private List<BluetoothDevice> pairedDevices;
    private List<BluetoothDevice> newDevices;

    public BluetoothDeviceAdapter bluetoothDeviceAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_connection);
    }

    @Override
    public void initData() {
        super.initData();
        btBluetoothScan = findViewById(R.id.btBluetoothScan);
        bluetoothDevices = findViewById(R.id.bluetoothDevices);
        serverLayout = findViewById(R.id.serverLayout);
        clientLayout = findViewById(R.id.clientLayout);
        clientStatus = findViewById(R.id.clientStatus);
        platform = getIntent().getIntExtra("platform", 0);
        LoggerUtils.d(String.valueOf(platform));
        WaitConnectionActivityPermissionsDispatcher.locationPermissionWithCheck(this);
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
        if (platform == 0) { //服务端
            clientLayout.setVisibility(View.GONE);
            waitClient();
        } else { //客户端
            serverLayout.setVisibility(View.VISIBLE);
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(findBluetooth, filter);
            pairedDevices = new ArrayList<>();
            newDevices = new ArrayList<>();
            bluetoothDeviceAdapter = new BluetoothDeviceAdapter(pairedDevices, newDevices, this);
            bluetoothDevices.setAdapter(bluetoothDeviceAdapter);
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
                        socket.connect();
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
        if (platform == 0) {
            unregisterReceiver(findBluetooth);
            //关闭广播
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
}
