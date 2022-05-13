package com.example.sixths;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void signIn(View view) {
        Toast.makeText(WelcomeActivity.this, "sign in", Toast.LENGTH_LONG).show();
        Intent login_intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(login_intent);
    }

    public void signUp(View view) {
        Toast.makeText(WelcomeActivity.this, "sign up", Toast.LENGTH_LONG).show();
    }
}