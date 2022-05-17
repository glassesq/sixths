package com.example.sixths.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.sixths.R;
import com.example.sixths.service.Service;

public class NewActivity extends AppCompatActivity {
    // 新建post的activity

    private TextView content_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        content_view = findViewById(R.id.content_view);
    }

    public void makePost(View view) {
        String content = content_view.getText().toString();
        System.out.println(content);
        Service.makeArticle(content);
        this.finish();
    }

    public void cancel(View view) {
        this.finish();
    }
}