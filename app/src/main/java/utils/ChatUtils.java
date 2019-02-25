package utils;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;

import service.BluetoothService;

/**
 * Created by Lee on 2019/2/25.
 */


public class ChatUtils {

    public static BluetoothService bluetoothService;

    public static BluetoothSocket bluetoothSocket;

    public static BluetoothService getBluetoothService() {
        return bluetoothService;
    }

    public static void setBluetoothService(BluetoothService bluetoothService) {
        ChatUtils.bluetoothService = bluetoothService;
    }

    public static BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    public static void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        ChatUtils.bluetoothSocket = bluetoothSocket;
    }

    public static void clean(){
        try {
            if (bluetoothService != null) {
                bluetoothService.stop();
                bluetoothService.close();
                bluetoothService = null;
            }
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
