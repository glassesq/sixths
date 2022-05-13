package com.example.sixths;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* 跳转欢迎页逻辑 */
        if (!Utils.checkToken()) {
            Intent welcome_intent = new Intent(MainActivity.this, WelcomeActivity.class);
            /*
             ActivityResultLauncher<Intent> welcomeLauncher = registerForActivityResult(
                     new ActivityResultContracts.StartActivityForResult(),
                     new ActivityResultCallback<ActivityResult>() {
                         @Override
                         public void onActivityResult(ActivityResult result) {
                             if (result.getResultCode() != Activity.RESULT_OK) {
                                 Toast.makeText(MainActivity.this, "欢迎失败", Toast.LENGTH_LONG).show();
                             }
                         }
                     }
             );
            */
            welcome_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(welcome_intent);
            return;
        }

        setContentView(R.layout.overall_page);
    }


}