package com.example.sixths.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.fragment.MainFragment;
import com.example.sixths.fragment.PersonFragment;
import com.example.sixths.R;
import com.example.sixths.service.Service;

public class MainActivity extends AppCompatActivity implements PostListAdapter.postListener {

    private enum FragName {MAIN, PERSON, SEARCH}

    private SharedPreferences preferences;
    public static final String TOKEN_PREF = "token";
    private final String sharedPrefFile = "com.example.sixths.tokenprefs";

    private FragmentTransaction transaction;
    private Fragment main_frag = null;
    private Fragment person_frag = null;

    public static final int NEW_FAIL = 0;
    public static final int NEW_SUCCESS = 1;


    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == NEW_FAIL) {
                failNewPost();
            } else if (msg.what == NEW_SUCCESS) {
                successNewPost();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* 从sharedPreference中获取token */
        if( Service.getToken() == null ) updateTokenFromPref();

        /* 跳转欢迎页逻辑 */
        if (!Service.checkToken()) {
            gotoWelcome();
            return;
        }

        setContentView(R.layout.overall_page);

        Service.setMainHandler(handler);
        Service.getMyself();

        /* 准备fragment内容并跳转至主页 */
        initFragment();
        selectTab(FragName.MAIN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTokenFromPref();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveTokenToPref();
    }

    public void updateTokenFromPref() {
        preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String token = preferences.getString(TOKEN_PREF, null);
        if( token != null ) {
            System.out.print("update token:");
            System.out.println(token);
        }
        if (token != null) Service.setToken(token);
    }

    public void saveTokenToPref() {
        SharedPreferences.Editor editor = preferences.edit();
        System.out.print("save token:");
        System.out.println(Service.getToken());
        editor.putString(TOKEN_PREF, Service.getToken());
        editor.apply();
    }

    public void initFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        if (main_frag == null) {
            MainFragment f = new MainFragment();
            f.setListener(this);
            main_frag = f;
            transaction.add(R.id.frame_content, main_frag, "main_frag");
        }
        if (person_frag == null) {
            person_frag = new PersonFragment();
            transaction.add(R.id.frame_content, person_frag);
        }
        transaction.hide(main_frag);
        transaction.hide(person_frag);
        transaction.commit();
    }

    public void gotoMain(View view) {
        selectTab(FragName.MAIN);
    }

    public void gotoPerson(View view) {
        selectTab(FragName.PERSON);
    }

    private void selectTab(FragName aim) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(main_frag);
        transaction.hide(person_frag);
        if (aim == FragName.PERSON) {
            transaction.show(person_frag);
        } else {
            transaction.show(main_frag);
        }
        transaction.commit();
    }

    private void failNewPost() {
        Toast.makeText(MainActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
        // TODO: save to draft box.
    }

    private void successNewPost() {
        Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
        Service.fetchArticle(Service.POST_LIST_TYPE.ALL);
        Service.fetchArticle(Service.POST_LIST_TYPE.FOLLOW);
    }

    public void gotoNew(View view) {
        Intent intent = new Intent(MainActivity.this, NewActivity.class);
        startActivity(intent);
    }

    public void gotoWelcome() {
        Intent welcome_intent = new Intent(MainActivity.this, WelcomeActivity.class);
        welcome_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(welcome_intent);
    }

    public void logout(View view) {
        Service.logout();
        gotoWelcome();
        // 登出成功
    }

    public void gotoUserPage(int userid) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("id", userid);
        startActivity(intent);
    }

    public void gotoSetting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }


    private void failUserInfo() {
        Toast.makeText(MainActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
        // TODO: save to draft box.
    }

}