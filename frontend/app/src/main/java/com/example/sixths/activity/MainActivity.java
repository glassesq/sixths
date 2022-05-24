package com.example.sixths.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.fragment.MainFragment;
import com.example.sixths.fragment.PersonFragment;
import com.example.sixths.R;
import com.example.sixths.service.Service;

public class MainActivity extends AppCompatActivity implements PostListAdapter.postListener {

    private enum FragName {MAIN, PERSON, SEARCH}

    private int PERMISSION_REQUEST = 999;

    private SharedPreferences preferences;
    public static final String TOKEN_PREF = "token";
    private final String sharedPrefFile = "com.example.sixths.tokenprefs";

    private FragmentTransaction transaction;
    private Fragment main_frag = null;
    private Fragment person_frag = null;

    public static final int NEW_FAIL = 0;
    public static final int NEW_SUCCESS = 1;
    public static final int FRESH = 2;
    public static final int FRESH_PROFILE = 3;

    String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};

    public void checkPermissions() {
        for (int i = 0; i < permissions.length; i++) {
            int ret = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (ret != PackageManager.PERMISSION_GRANTED) {
                startRequestPermission();
                return;
            }
        }
    }

    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast toast = Toast.makeText(this, "请前往设置界面获取权限", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }


    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == NEW_FAIL) {
                failNewPost();
            } else if (msg.what == NEW_SUCCESS) {
                successNewPost();
            } else if (msg.what == FRESH) {
                fresh();
            } else if (msg.what == FRESH_PROFILE) {
                PersonFragment p = (PersonFragment) person_frag;
                if (person_frag != null) p.freshProfile();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* 从sharedPreference中获取token */
        if (Service.getToken() == null) updateTokenFromPref();

        /* 跳转欢迎页逻辑 */
        if (!Service.checkToken()) {
            gotoWelcome();
            return;
        }

        setContentView(R.layout.overall_page);

        Service.setMainHandler(handler);
        Service.initStorage(this.getApplicationContext().getFilesDir().getPath());
        Service.getMyself();
        Service.setFollow();

        Service.initColor(this.getApplicationContext());

        /* 准备fragment内容并跳转至主页 */
        initFragment();
        selectTab(FragName.MAIN);

        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Service.setMainHandler(handler);
        updateTokenFromPref();
        fresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Service.setMainHandler(null);
        saveTokenToPref();
    }

    public void updateTokenFromPref() {
        preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String token = preferences.getString(TOKEN_PREF, null);
        if (token != null) {
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

    private void fresh() {
        Service.fresh();
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

    /* post list listener */

    public void gotoUserPage(int userid) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("id", userid);
        startActivity(intent);
    }

    public void switchLike(int article_id) {
        Service.switchLike(article_id);
    }

    public void gotoArticlePage(int article_id) {
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra("article_id", article_id);
        startActivity(intent);
    }

    /* setting */

    public void gotoSetting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private void failUserInfo() {
        Toast.makeText(MainActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
        // TODO: save to draft box.
    }

}