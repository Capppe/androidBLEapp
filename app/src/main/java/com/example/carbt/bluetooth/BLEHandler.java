package com.example.carbt.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.carbt.R;

import java.util.List;

public class BLEHandler extends Fragment {

    private final TextView scanStatus;

    public BLEHandler(TextView scanStatus){
        this.scanStatus = scanStatus;
    }

    @SuppressLint("MissingPermission")
    public void sendCommand(String command, BluetoothGattCharacteristic characteristic){
        System.out.println("Sending command: " + command);
        byte[] data = command.getBytes();

        characteristic.setValue(data);
    }



    // Callbacks

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        final Handler connectedHandler = new Handler(Looper.getMainLooper());
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if(newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();

                connectedHandler.post(() -> scanStatus.setText(R.string.connected_text));
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectedHandler.post(() -> scanStatus.setText(R.string.start_scan_text));
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if(status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("Status success: " + status);
                //connectedHandler.post(() -> System.out.println(gatt.getServices()));
                List<BluetoothGattService> services = gatt.getServices();
                List<BluetoothGattCharacteristic> characteristics = services.get(0).getCharacteristics();

                System.out.println(characteristics);
                connectedHandler.post(() -> sendCommand("s", characteristics.get(1)));
            }else {
                System.out.println("Status not success: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if(status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("Success in writing");
            }else {
                System.out.println("No success in writing :(");
            }
        }
    };
}
