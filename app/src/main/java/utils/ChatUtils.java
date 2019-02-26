package utils;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;

/**
 * Created by Lee on 2019/2/25.
 */


public class ChatUtils {


    public static BluetoothSocket bluetoothSocket;

    public static BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public static void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        ChatUtils.bluetoothSocket = bluetoothSocket;
    }

    public static void clean(){
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
