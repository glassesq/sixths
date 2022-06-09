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

public class UsernameconfigActivity extends AppCompatActivity {

    static final public int SUCCESS = 1;
    static final public int DUP = 2;
    static final public int FAIL = 3;

    private TextView user_view;


    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == SUCCESS) {
                successSet();
            } else if (msg.what == DUP) {
                dupSet();
            } else if (msg.what == FAIL) {
                failSet();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usernameconfig);

        Service.setUsernameSetHandler(handler);
        user_view = findViewById(R.id.username_set);
        user_view.setText(Service.myself.name);
    }

    public void cancel(View view) {
        this.finish();
    }

    public void successSet() {
        Toast.makeText(this.getApplicationContext(), "更新成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void dupSet() {
        Toast.makeText(this.getApplicationContext(), "更新失败，与已有用户名重复", Toast.LENGTH_SHORT).show();
    }

    public void failSet() {
        Toast.makeText(this.getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
    }


    public void submit(View view) {
        String name = user_view.getText().toString();
        if( name == null || name.isEmpty() ) {
            Toast.makeText(this.getApplicationContext(), "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Service.setUsername(name);
    }
}