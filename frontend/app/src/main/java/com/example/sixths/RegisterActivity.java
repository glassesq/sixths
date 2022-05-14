package com.example.sixths;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    public static final int REG_FAIL = 0;
    public static final int REG_SUCCESS = 1;

    private TextView login_username = null;
    private TextView login_email = null;
    private TextView login_password = null;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == REG_SUCCESS) {
                successRegister();
            } else if (msg.what == REG_FAIL) {
                failRegister();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login_username = findViewById(R.id.login_username);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);

        Service.setRegisterHandler(handler);
    }

    public void register(View view) {
        /* 此处应该获取帐号密码 */
//        String username = login_em
        String username = login_username.getText().toString();
        String email = login_email.getText().toString();
        String password = login_password.getText().toString();
        Service.register(username, email, password);
    }

    private void successRegister() {
        Intent main_intent = new Intent(RegisterActivity.this, MainActivity.class);
        main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(main_intent);
    }

    private void failRegister() {
        /* jump to fail page */
        Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
    }

    public void cancel(View view) {
        this.finish();
    }
}