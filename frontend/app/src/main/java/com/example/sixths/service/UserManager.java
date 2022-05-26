package com.example.sixths.service;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.adapter.UserListAdapter;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class UserManager {

    private boolean like = false;
    private boolean follow = false;

    int article_id;
    int user_id;

    public int allowSize = Service.START_ARTICLE_NUM;
    private ArrayList<User> user_list = new ArrayList<>();
    private UserListAdapter adapter = null;

    public void enableFollow() {
        follow = true;
    }

    public void enableLike() {
        like = true;
    }

    public void setUser(int id) {
        user_id = id;
    }

    public void setArticle(int id) {
        article_id = id;
    }

    public void setAdapter(UserListAdapter adapter) {
        this.adapter = adapter;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void fresh() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            fresh();
        }
    };

    public void fetchUser() {
        System.out.println("fetch User");
        Thread thread = new Thread(() -> {
            try {
                String params = "start=" + URLEncoder.encode(String.valueOf(0), "UTF-8")
                        + "&num=" + URLEncoder.encode(String.valueOf(allowSize), "UTF-8");

                if (follow && user_id >= 0) {
                    params = params.concat("&user_id=" + URLEncoder.encode(String.valueOf(user_id), "UTF-8"));
                }

                if (like && article_id >= 0) {
                    params = params.concat("&article_id=" + URLEncoder.encode(String.valueOf(article_id), "UTF-8"));
                }

                System.out.println(params);

                HttpURLConnection conn;
                if (follow) {
                    conn = Service.getConnectionWithToken("/user/get_following_detail", "GET", params);
                } else if (like) {
                    conn = Service.getConnectionWithToken("/article/get_liker", "GET", params);
                } else return;

                System.out.println("fetch User conn established");

                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = Service.is2String(in);
                    System.out.println(result);

                    ArrayList<User> list = new ArrayList<>();
                    JSONArray arr = new JSONArray(result);
                    for (int i = 0; i < arr.length(); i++) {
                        User user = Service.decodeUserInfo(arr.getJSONObject(i));
                        if (user != null) {
                            list.add(user);
                        }
                    }
                    user_list = list;

                    Message msg = new Message();
                    msg.setTarget(handler);
                    msg.sendToTarget();
                } else {
                    System.out.println(conn.getResponseCode());
                    InputStream in = conn.getErrorStream();

                    String result = Service.is2String(in);
                    System.out.println(result);
                }
                System.out.println("notify data set changed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

    }

    public User getByIndex(int index) {
        if (index < 0 || index >= count()) return null;
        return user_list.get(index);
    }

    public int count() {
        return Math.min(allowSize, user_list.size());
    }
}
