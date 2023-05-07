package com.example.carbt.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.carbt.R;
import com.example.carbt.bluetooth.BLEScanner;
import com.example.carbt.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private BLEScanner bleScanner;
    private Button scanButton;
    private TextView scanStatus;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout devViewLayout = this.getView().findViewById(R.id.devViewLayout);

        scanButton = this.getView().findViewById(R.id.scanB);
        scanStatus = this.getView().findViewById(R.id.scanStatusText);

        bleScanner = new BLEScanner(devViewLayout, scanStatus, getContext());

        scanButton.setOnClickListener(v -> {
            int i = bleScanner.scanLeDevice();
            if(i == 0) {
                // TODO: add bt popup
                Toast.makeText(this.getContext(), "Please enable bluetooth!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}