package com.example.carbt.ui.home;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.carbt.R;
import com.example.carbt.bluetooth.BLEScanner;
import com.example.carbt.databinding.FragmentHomeBinding;

import java.util.List;
import java.util.UUID;

public class HomeFragment extends Fragment {

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final UUID serviceUUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private static final UUID charUUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

    private FragmentHomeBinding binding;
    private BLEScanner bleHandler;

    private Button startButton;
    private Button windowsDownButton;
    private Button windowsUpButton;
    private Button hazardLightsButton;
    private Button headlightsButton;

    private TextView top;
    private TextView bottom;

    private Boolean status;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        bleHandler = new BLEScanner();
        status = bleHandler.connectToDevice(bluetoothAdapter.getRemoteDevice("94:A9:A8:3B:7B:57"));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottom = this.getView().findViewById(R.id.connectedToText);
        top = this.getView().findViewById(R.id.connStatusText);

        startButton = this.getView().findViewById(R.id.startB);
        windowsDownButton = this.getView().findViewById(R.id.windDownB);
        windowsUpButton = this.getView().findViewById(R.id.windUpB);
        hazardLightsButton = this.getView().findViewById(R.id.hazardLightsB);
        headlightsButton = this.getView().findViewById(R.id.headlightsFlashB);

        setListeners();

        if(status){
            top.setText("Connected to:");
            bottom.setText("Volvo 740");
        }
    }

    private void setListeners() {
        startButton.setOnClickListener(v -> bleHandler.sendCommand("s", serviceUUID, charUUID));
        windowsDownButton.setOnClickListener(v -> bleHandler.sendCommand("d", serviceUUID, charUUID));
        windowsUpButton.setOnClickListener(v -> bleHandler.sendCommand("u", serviceUUID, charUUID));
        hazardLightsButton.setOnClickListener(v -> bleHandler.sendCommand("w", serviceUUID, charUUID));
        headlightsButton.setOnClickListener(v -> bleHandler.sendCommand("h", serviceUUID, charUUID));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}