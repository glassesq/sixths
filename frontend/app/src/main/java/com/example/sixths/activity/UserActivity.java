package com.example.sixths.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sixths.R;
import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.adapter.PostPagerAdapter;
import com.example.sixths.service.Service;
import com.example.sixths.service.User;
import com.google.android.material.tabs.TabLayoutMediator;

public class UserActivity extends AppCompatActivity {

    public static final int USER_FOLLOW = 1;
    public static final int USER_UNFOLLOW = 4;
    public static final int USER_FAIL = 2;
    public static final int USER_SUCCESS = 3;

    private TextView nickname_view = null;
    private TextView username_view = null;
    private TextView bio_view = null;

    private Button followed_but = null;
    private Button follow_but = null;

    public RecyclerView recycler_view = null;

    public int userid = 0;
    public User user;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == USER_FAIL) {
            } else if (msg.what == USER_SUCCESS) {
                successUserInfo(msg.getData().getString("info"));
            } else if (msg.what == USER_FOLLOW) {
                setFollow();
            } else if (msg.what == USER_UNFOLLOW) {
                setUnfollow();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.user_mainpage);

        Service.setUserHandler(handler);

        /* 获取userid */
        Intent intent = getIntent();
        userid = intent.getIntExtra("id", 0);

        /* 更新对应的postlist，从后端获取更多 */

        Service.setPerson(userid);
        Service.getUserInfo(userid);

        nickname_view = findViewById(R.id.nickname_view);
        username_view = findViewById(R.id.username_view);
        recycler_view = findViewById(R.id.recycler_view);
        bio_view = findViewById(R.id.user_bio);

        followed_but = findViewById(R.id.followed_but);
        follow_but = findViewById(R.id.follow_but);

        /* 设计 recycle view 的 adapter */
        PostListAdapter adapter = new PostListAdapter(this, null, Service.POST_LIST_TYPE.PERSON); // TODO
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        /* 从后端获取信息 */
        Service.fetchArticle(Service.POST_LIST_TYPE.PERSON);
    }

    public void switchFollow() {
        if (!Service.isFollow(userid)) {
            Service.followUser(userid);
        } else {
            Service.unfollowUser(userid);
        }
    }

    public void switchFollow(View view) {
        switchFollow();
    }

    public void freshFollow() {
        if (Service.isFollow(user.id)) {
            setFollow();
        } else {
            setUnfollow();
        }
    }

    public void setFollow() {
        follow_but.setVisibility(View.GONE);
        followed_but.setVisibility(View.VISIBLE);
    }

    public void setUnfollow() {

        follow_but.setVisibility(View.VISIBLE);
        followed_but.setVisibility(View.GONE);
    }

    public void successUserInfo(String info_str) {
        user = Service.decodeUserInfo(info_str);
        if (user != null) {
            username_view.setText(user.name);
            nickname_view.setText(user.nickname);
            bio_view.setText(user.bio);
            freshFollow();
        }
    }
}