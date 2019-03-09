package utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Lee on 2019/2/26.
 */


public class BluetoothClientUtils {

    public Context context;

    public BluetoothSocket bluetoothSocket;

    public static final UUID BLUETOOTH_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ClientListener listener;

    public BluetoothClientUtils(Context context) {
        this.context = context;
    }

    public void connect(BluetoothDevice device,boolean flag){
        if (device != null) {
            LoggerUtils.d("device is not null");
            LoggerUtils.d("prepare to connect "+device.getName() + ",address = " + device.getAddress());
            listener.onStart();
            ConnectThread thread = new ConnectThread(device,flag);
            thread.cancel();
            thread.start();

        }
    }

    public class ConnectThread extends Thread{
        public BluetoothDevice device;
        public String socketType;

        public ConnectThread(BluetoothDevice device,boolean type) {
            this.device = device;
            this.socketType = type ? "secure" : "inSecure";
        }

        @Override
        public void run() {
            try {
                super.run();
                if ("secure".equals(socketType)){
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
                }else if ("inSecure".equals(socketType)){
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(BLUETOOTH_UUID);
                }
                listener.onConnecting();
                bluetoothSocket.connect();
                listener.onSuccess(bluetoothSocket);
            } catch (IOException e) {
                listener.onFailed();
            }
        }
        public void cancel(){
            try {
                if (bluetoothSocket != null) {
                    bluetoothSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface ClientListener{

        void onStart();

        void onConnecting();

        void onSuccess(BluetoothSocket socket);

        void onFailed();
    }

    public void setListener(ClientListener listener){
        this.listener = listener;
    }

}
