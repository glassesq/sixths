package com.example.sixths.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.sixths.fragment.MainFragment;
import com.example.sixths.fragment.PersonFragment;
import com.example.sixths.R;
import com.example.sixths.service.Service;

public class MainActivity extends AppCompatActivity {

    private enum FragName {MAIN, PERSON, SEARCH}

    private SharedPreferences preferences;
    public static final String TOKEN_PREF = "token";
    private final String sharedPrefFile = "com.example.sixths.tokenprefs";

    private FragmentTransaction transaction;
    private Fragment main_frag = null;
    private Fragment person_frag = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* 从sharedPreference中获取token */
        updateTokenFromPref();

        /* 跳转欢迎页逻辑 */
        if (!Service.checkToken()) {
            Intent welcome_intent = new Intent(MainActivity.this, WelcomeActivity.class);
            welcome_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(welcome_intent);
            return;
        }

        setContentView(R.layout.overall_page);

        /* 准备fragment内容并跳转至主页 */
        initFragment();
        selectTab(FragName.MAIN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveTokenToPref();
    }

    public void updateTokenFromPref() {
        preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        String token = preferences.getString(TOKEN_PREF, null);
        if( token != null ) Service.setToken(token);
    }

    public void saveTokenToPref() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN_PREF, Service.getToken());
        editor.apply();
    }

    public void initFragment() {
        transaction = getSupportFragmentManager().beginTransaction();
        if (main_frag == null) {
            main_frag = new MainFragment();
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



}