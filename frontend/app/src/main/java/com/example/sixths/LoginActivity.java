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

public class LoginActivity extends AppCompatActivity {

    private TextView login_email = null;
    private TextView login_password = null;


    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                successSignIn();
            } else if (msg.what == 1) {
                failSignIn();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        Service.setLoginHandler(handler);
    }

    public void signIn(View view) {
        /* 此处应该获取帐号密码 */
        String email = login_email.getText().toString();
        String password = login_password.getText().toString();
        System.out.println(email + " " + password);
        Service.signIn(email, password);
    }

    private void successSignIn() {
        Intent main_intent = new Intent(LoginActivity.this, MainActivity.class);
        main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(main_intent);

    }

    private void failSignIn() {
        /* jump to fail page */
        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
    }

    public void cancel(View view) {
        this.finish();
    }
}