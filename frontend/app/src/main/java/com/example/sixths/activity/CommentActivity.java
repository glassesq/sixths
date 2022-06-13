package com.example.sixths.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sixths.R;
import com.example.sixths.service.Service;

public class CommentActivity extends AppCompatActivity {

    public int article_id;

    private TextView content_view;

    private ImageView profile_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        content_view = findViewById(R.id.content_view);

        Intent intent = getIntent();
        article_id = intent.getIntExtra("article_id", 0);

        profile_view = findViewById(R.id.user_profile_view);
        Uri u = Service.getResourceUri(Service.myself.profile);
        if (u != null) {
            profile_view.setImageURI(u);
            profile_view.setVisibility(View.VISIBLE);
        }
    }


    public void makeComment(View view) {
        String content = content_view.getText().toString();
        Service.makeComment(article_id, content);
        this.finish();
    }

    public void cancel(View view) {
        this.finish();
    }
}