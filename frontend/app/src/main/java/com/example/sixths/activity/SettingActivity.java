package com.example.sixths.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.example.sixths.R;
import com.example.sixths.service.Service;

public class SettingActivity extends AppCompatActivity {

    private TextView nickname_view;
    private TextView bio_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_info);

        nickname_view = findViewById(R.id.nickname_view);
        bio_view = findViewById(R.id.bio_view);

        nickname_view.setText(Service.myself.nickname);
        bio_view.setText(Service.myself.bio);
    }

    public void submit(View view) {
        String nickname = nickname_view.getText().toString();
        String bio = bio_view.getText().toString();
        Service.setUserInfo(nickname, bio);
        this.finish();
    }

    public void cancel(View view) {
        this.finish();
    }
}