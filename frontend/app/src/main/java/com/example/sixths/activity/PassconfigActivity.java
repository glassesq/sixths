package com.example.sixths.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sixths.R;
import com.example.sixths.service.Service;

public class PassconfigActivity extends AppCompatActivity {


    static final public int SUCCESS = 1;
    static final public int FAIL = 3;

    private TextView pass_view;


    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                successSet();
            } else if (msg.what == FAIL) {
                failSet();
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passconfig);


        Service.setPasswordSetHandler(handler);
        pass_view = findViewById(R.id.password_set);
    }

    public void cancel(View view) {
        this.finish();
    }

    public void successSet() {
        Toast.makeText(this.getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void failSet() {
        Toast.makeText(this.getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
    }


    public void submit(View view) {
        String pass = pass_view.getText().toString();
        if (pass == null || pass.isEmpty()) {
            Toast.makeText(this.getApplicationContext(), "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Service.setPassword(pass);
    }
}