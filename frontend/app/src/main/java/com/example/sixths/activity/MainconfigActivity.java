package com.example.sixths.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;

import com.example.sixths.R;
import com.example.sixths.service.Service;

public class MainconfigActivity extends AppCompatActivity {

    public SwitchCompat switch_sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainconfig);

        switch_sort = findViewById(R.id.switch_sort);
        switch_sort.setChecked(Service.enableSort);
    }

    public void cancel(View view) {
        finish();
    }

    public void submit(View view) {
        Service.setEnableSort(switch_sort.isChecked());
        finish();
    }
}