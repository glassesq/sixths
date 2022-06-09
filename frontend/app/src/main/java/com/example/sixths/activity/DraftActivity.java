package com.example.sixths.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sixths.R;
import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.service.Service;

public class DraftActivity extends AppCompatActivity {

    private RecyclerView recycler_view;


    private final PostListAdapter.postListener listener = new PostListAdapter.postListener() {
        @Override
        public void gotoUserPage(int userid) {
            return;
        }

        @Override
        public void switchLike(int article_id) {
            return;
        }

        @Override
        public void gotoArticlePage(int article_id) {
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            intent.putExtra("article_id", article_id);
            finish();
        }

        @Override
        public void shareArticle(String str) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft);

        recycler_view = findViewById(R.id.draft_recycler_view);

        /* 设计 recycle view 的 adapter */
        PostListAdapter adapter = new PostListAdapter(this, listener, Service.POST_LIST_TYPE.DRAFT);
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        /* 从后端获取信息 */
        Service.fetchArticle(adapter.type);
    }

    public void cancel(View view) {
        this.finish();
    }
}