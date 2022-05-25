package com.example.sixths.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sixths.R;
import com.example.sixths.adapter.NotificationListAdapter;
import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.service.Notification;
import com.example.sixths.service.Service;

public class NotificationActivity extends AppCompatActivity {
    /* https://zhuanlan.zhihu.com/p/22697316 */

    //TODO

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
            Intent intent = new Intent(NotificationActivity.this, ArticleActivity.class);
            intent.putExtra("article_id", article_id);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_box);

        recycler_view = findViewById(R.id.notification_recycler_view);

        /* 设计 recycle view 的 adapter */

        NotificationListAdapter adapter = new NotificationListAdapter(this, listener);
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        /* 从后端获取信息 */
        Service.fetchNotification();
    }

    public void cancel(View view) {
        this.finish();
    }
}