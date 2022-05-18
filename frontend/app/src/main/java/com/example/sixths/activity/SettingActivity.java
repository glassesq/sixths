package com.example.sixths.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sixths.R;
import com.example.sixths.service.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;

public class SettingActivity extends AppCompatActivity {

    private TextView nickname_view;
    private TextView bio_view;

    private ImageView image_view;

    Bitmap bitmap = null;

    private String profile_src = null;

    public static final int UPLOAD_IMAGE = 1;

    ActivityResultLauncher<String> launcher;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPLOAD_IMAGE) {
                System.out.println("settings upload image ok");
                successUpload(msg.getData().getString("data"));
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_info);
        image_view = findViewById(R.id.setting_profile_view);

        launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        try {
                            /* 获取bitmap */
                            InputStream input = getContentResolver().openInputStream(uri);
                            bitmap = Service.getBitmap(input);

                            /* 上传文件 */
                            input = getContentResolver().openInputStream(uri);
                            String type = getContentResolver().getType(uri);
                            Service.uploadImage(UPLOAD_IMAGE, handler, input, type);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


        nickname_view = findViewById(R.id.nickname_view);
        bio_view = findViewById(R.id.bio_view);

        nickname_view.setText(Service.myself.nickname);
        bio_view.setText(Service.myself.bio);
    }

    public void submit(View view) {
        String nickname = nickname_view.getText().toString();
        String bio = bio_view.getText().toString();
        Service.setUserInfo(nickname, bio, profile_src);
        this.finish();
    }

    public void choosePhoto(View view) {
        launcher.launch("image/*");
    }

    public void cancel(View view) {
        this.finish();
    }

    public void successUpload(String src) {
        this.profile_src = src;
        Toast.makeText(this, "上传头像成功", Toast.LENGTH_LONG).show();
        if (bitmap != null) image_view.setImageBitmap(bitmap);
    }
}