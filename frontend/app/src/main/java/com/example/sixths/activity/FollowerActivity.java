package com.example.sixths.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sixths.R;
import com.example.sixths.adapter.UserListAdapter;
import com.example.sixths.service.Service;

public class FollowerActivity extends AppCompatActivity {

    private final UserListAdapter.userListener listener = new UserListAdapter.userListener() {
        @Override
        public void gotoUserPage(int userid) {
            Intent intent = new Intent(FollowerActivity.this, UserActivity.class);
            intent.putExtra("id", userid);
            startActivity(intent);
        }
    };

    public RecyclerView recycler_view;

    public int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        Intent intent = getIntent();
        user_id = intent.getIntExtra("id", -1);
        if (user_id < 0) this.finish();

        Service.setUserFollowId(user_id);

        recycler_view = findViewById(R.id.recycler_view);

        /* 设计 recycle view 的 adapter */
        UserListAdapter adapter = new UserListAdapter(this, listener, Service.USER_LIST_TYPE.FOLLOW); // TODO
        recycler_view.setAdapter(adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        /* 从后端获取信息 */
        Service.fetchUser(Service.USER_LIST_TYPE.FOLLOW);
    }

    public void cancel(View view) {
        finish();
    }

}