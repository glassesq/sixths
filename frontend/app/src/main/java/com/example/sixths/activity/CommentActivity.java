package com.example.sixths.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.sixths.R;
import com.example.sixths.service.Service;

public class CommentActivity extends AppCompatActivity {

    public int article_id;

    private TextView content_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        content_view = findViewById(R.id.content_view);

        Intent intent = getIntent();
        article_id = intent.getIntExtra("article_id", 0);
    }


    public void makeComment(View view) {
        String content = content_view.getText().toString();
        System.out.println(content);
        Service.makeComment(article_id, content);
        this.finish();
    }

    public void cancel(View view) {
        this.finish();
    }
}