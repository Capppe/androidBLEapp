package com.example.carbt.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.carbt.R;
import com.example.carbt.files.FileHandler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BLEScanner {
    private Context context = null;
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final BluetoothLeScanner bleScanner = bluetoothAdapter.getBluetoothLeScanner();
    private final List<BluetoothDevice> devices = new ArrayList<>();
    private BluetoothGatt btGatt;

    private final Handler handler = new Handler();

    private boolean scanning;
    LinearLayout devViewLayout;

    private TextView scanStatus;

    private final FileHandler fileHandler = new FileHandler();

    private static final long SCAN_PERIOD = 10000;

    public BLEScanner(LinearLayout devViewLayout, TextView scanStatus, Context context){
        this.devViewLayout = devViewLayout;
        this.scanStatus = scanStatus;
        this.context = context;
    }

    public BLEScanner(){

    }

    @SuppressLint("MissingPermission")
    public void sendCommand(String command, UUID serviceUUID, UUID charUUID) {
        if(btGatt == null){
            btGatt = bluetoothAdapter.getRemoteDevice("94:A9:A8:3B:7B:57").connectGatt(fileHandler.getContext(),false, gattCallback);
        }

        BluetoothGattService service = btGatt.getService(serviceUUID);
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(charUUID);

        characteristic.setValue(command.getBytes(StandardCharsets.UTF_8));
        btGatt.writeCharacteristic(characteristic);
    }

    @SuppressLint("MissingPermission")
    public boolean connectToDevice(BluetoothDevice device) {
        btGatt = device.connectGatt(fileHandler.getContext(), false, gattCallback);

        return btGatt != null;
    }

    @SuppressLint("MissingPermission")
    public void disconnectDevice() {
        if(btGatt != null){
            btGatt.close();
        }
    }

    @SuppressLint("MissingPermission")
    private void addDevice(BluetoothDevice device) {
        if(devices.contains(device)){
            return;
        }

        devices.add(device);
        Button devButton = new Button(devViewLayout.getContext());

        if(Objects.equals(device.getName(), "") || device.getName() == null){
            devButton.setText(device.getAddress());
        }else {
            devButton.setText(device.getName());
        }
        devButton.setMinWidth(200);
        devButton.setMinHeight(270);
        devButton.setTextSize(20);

        devButton.setBackgroundColor(Color.TRANSPARENT);
        devButton.setOnClickListener(v -> {
            connectToDevice(device);
            bleScanner.stopScan(leScanCallback);
            this.scanStatus.setText(R.string.connecting_text);
        });

        View divider = new View(devViewLayout.getContext());
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 5
        ));
        divider.setBackgroundColor(Color.LTGRAY);

        devViewLayout.addView(devButton);
        devViewLayout.addView(divider);
    }




    //scanner
    //scanner
    //scanner
    //scanner
    //scanner
    //scanner
    @SuppressLint("MissingPermission")
    public int scanLeDevice() {
        if(!bluetoothAdapter.isEnabled()){
            return 0;
        }

        if (!scanning) {
            handler.postDelayed(() -> {
                String done = "Done, found " + devices.size() + " devices";
                this.scanStatus.setText(done);
                scanning = false;
                bleScanner.stopScan(leScanCallback);
            }, SCAN_PERIOD);

            this.scanStatus.setText(R.string.scanning_text);
            scanning = true;
            bleScanner.startScan(leScanCallback);
        }else {
            String done = "Done, found " + devices.size() + " devices";
            this.scanStatus.setText(done);
            scanning = false;
            bleScanner.stopScan(leScanCallback);
        }
        return 1;
    }

    // Callbacks
    // Callbacks
    // Callbacks
    // Callbacks
    // Callbacks
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        final Handler connectedHandler = new Handler(Looper.getMainLooper());
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if(newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();

//                connectedHandler.post(() -> scanStatus.setText(R.string.connected_text));
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
//                connectedHandler.post(() -> scanStatus.setText(R.string.start_scan_text));
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if(status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("Status success: " + status);
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

    private final ScanCallback leScanCallback =
            new ScanCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    addDevice(result.getDevice());
                }
            };
}
