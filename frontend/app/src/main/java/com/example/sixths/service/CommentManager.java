package com.example.sixths.service;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.sixths.adapter.CommentListAdapter;
import com.example.sixths.adapter.PostListAdapter;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CommentManager {

    private int article_id = -1;

    public int allowSize = Service.START_ARTICLE_NUM;
    private ArrayList<Comment> comment_list = new ArrayList<Comment>();
    private CommentListAdapter adapter = null;

    public void setAdapter(CommentListAdapter adapter) {
        this.adapter = adapter;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void fresh() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setArticle(int article_id) {
        this.article_id = article_id;
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

    public void fetchComment() {
        @SuppressLint("NotifyDataSetChanged") Thread thread = new Thread(() -> {
            try {
                String params = "article_id=" + URLEncoder.encode(String.valueOf(article_id), "UTF-8");
                HttpURLConnection conn = Service.getConnectionWithToken("/article/get_comments", "GET", params);

                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = Service.is2String(in);

                    ArrayList<Comment> list = new ArrayList<>();
                    JSONArray arr = new JSONArray(result);
                    for (int i = 0; i < arr.length(); i++) {
                        Comment comment = Service.decodeComment(arr.getJSONObject(i));
                        if (comment != null) {
                            Service.fetchImage(comment);
                            list.add(comment);
                        }
                    }
                    comment_list = list;

                    Message msg = new Message();
                    msg.setTarget(handler);
                    msg.sendToTarget();
                } else {
                    InputStream in = conn.getErrorStream();

                    String result = Service.is2String(in);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

    }

    public Comment getByIndex(int index) {
        if (index < 0 || index >= count()) return null;
        return comment_list.get(index);
    }

    public int count() {
        return Math.min(allowSize, comment_list.size());
    }
}
