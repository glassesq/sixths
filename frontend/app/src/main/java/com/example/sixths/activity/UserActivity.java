package com.example.sixths.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sixths.R;
import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.service.Service;
import com.example.sixths.service.User;
import com.example.sixths.view.CustomLayoutManager;

public class UserActivity extends AppCompatActivity {

    public static final int USER_FOLLOW = 1;
    public static final int USER_FAIL = 2;
    public static final int USER_SUCCESS = 3;
    public static final int USER_UNFOLLOW = 4;
    public static final int USER_PHOTO = 5;
    public static final int BLOCK_DONE = 6;

    private TextView nickname_view = null;
    private TextView username_view = null;
    private TextView bio_view = null;

    private TextView follower_text = null;
    private TextView following_text = null;


    private ImageView profile_view = null;

    private Button followed_but = null;
    private Button follow_but = null;

    public RecyclerView recycler_view = null;

    public int userid = 0;
    public User user;

    private final PostListAdapter.postListener listener = new PostListAdapter.postListener() {
        @Override
        public void gotoUserPage(int userid) {
            return;
        }

        @Override
        public void switchLike(int article_id) {
            Service.switchLike(article_id);
        }

        @Override
        public void gotoArticlePage(int article_id) {
            Intent intent = new Intent(UserActivity.this, ArticleActivity.class);
            intent.putExtra("article_id", article_id);
            startActivity(intent);
        }

        @Override
        public void shareArticle(String str) {
            UserActivity.this.shareArticle(str);
        }

    };


    public void shareArticle(String txt) {
        String mimeType = "text/plain";
        ShareCompat.IntentBuilder builder = new ShareCompat.IntentBuilder(this);
        builder.setType(mimeType).setChooserTitle("选择您要分享的应用").setText(txt).startChooser();
    }

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
            } else if (msg.what == USER_PHOTO) {
                freshProfile();
            } else if (msg.what == BLOCK_DONE) {
                doneBlock();
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

        nickname_view = findViewById(R.id.username_set);
        username_view = findViewById(R.id.username_view);
        recycler_view = findViewById(R.id.recycler_view);
        profile_view = findViewById(R.id.user_profile_view);
        bio_view = findViewById(R.id.user_bio);

        followed_but = findViewById(R.id.followed_but);
        follow_but = findViewById(R.id.follow_but);

        follower_text = findViewById(R.id.followed_view);
        following_text = findViewById(R.id.following_view);

        /* 设计 recycle view 的 adapter */
//        PostListAdapter adapter = new PostListAdapter(this, listener, Service.POST_LIST_TYPE.PERSON); // TODO
//        recycler_view.setAdapter(adapter);
//        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        /* 从后端获取信息 */
//        Service.fetchArticle(Service.POST_LIST_TYPE.PERSON);
    }

    public void switchFollow() {
        if (!Service.isFollow(userid)) {
            Service.followUser(userid);
        } else {
            Service.unfollowUser(userid);
        }
    }

    public void freshProfile() {
        System.out.println("profile fresh");
        if (user.profile == null) return;
        Uri u = Service.getResourceUri(user.profile);
        if (u != null) profile_view.setImageURI(u);
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
        if (user == null) {
            user = Service.decodeUserInfo(info_str);
            if (user != null) {
                username_view.setText(user.name);
                nickname_view.setText(user.nickname);
                bio_view.setText(user.bio);
                follower_text.setText(String.valueOf(user.followed_num));
                following_text.setText(String.valueOf(user.following_num));
                if (user.profile_fetched) {
                    System.out.println("the profile is here");
                    freshProfile();
                } else Service.fetchImage(user);
                freshFollow();
            }
        } else {
            user = Service.decodeUserInfo(info_str);
            if (user != null) {
                follower_text.setText(String.valueOf(user.followed_num));
                following_text.setText(String.valueOf(user.following_num));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Service.setUserHandler(null);
        Service.clearPerson();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("on resume");
        Service.setUserHandler(handler);

        /* 恢复用户信息 */
        Service.setPerson(userid);
        Service.getUserInfo(userid);

        /* 设计 recycle view 的 adapter */
        PostListAdapter adapter = new PostListAdapter(this, listener, Service.POST_LIST_TYPE.PERSON); // TODO
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new CustomLayoutManager(this));

        /* 从后端获取信息 */
        Service.fetchArticle(Service.POST_LIST_TYPE.PERSON);
    }


    public void gotoFollowerPage(View view) {
        Intent intent = new Intent(UserActivity.this, FollowerActivity.class);
        intent.putExtra("id", userid);
        startActivity(intent);
    }

    public void startBlock(View view) {
        if (userid == Service.myself.id) {
            Toast.makeText(this.getApplicationContext(), "您不能屏蔽自己", Toast.LENGTH_SHORT).show();
        } else {
            Service.blockUser(userid);
        }
    }

    public void doneBlock() {
        Toast.makeText(this.getApplicationContext(), "屏蔽成功", Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        /* 清理用户信息 */
        Service.setUserHandler(null);
    }
}