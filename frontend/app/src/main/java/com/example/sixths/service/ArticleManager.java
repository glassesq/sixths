package com.example.sixths.service;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.sixths.adapter.PostListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ArticleManager {

    private int person_userid = -1;

    public int allowSize = Service.START_ARTICLE_NUM;
    private ArrayList<Article> article_list = new ArrayList<Article>();
    private PostListAdapter adapter = null;

    public void setPerson(int id) {
        person_userid = id;
    }

    //        @SuppressLint("NotifyDataSetChanged")
    public void setAdapter(PostListAdapter adapter) {
        this.adapter = adapter;
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
        }
    };

    public void fetchArticle() {
        System.out.println("fetch Article");
        Thread thread = new Thread(() -> {
            try {
                String params = "start=" + URLEncoder.encode(String.valueOf(0), "UTF-8")
                        + "&num=" + URLEncoder.encode(String.valueOf(allowSize), "UTF-8");
                System.out.println("person userid");
                System.out.println(person_userid);
                if (person_userid >= 0) {
                    params = params.concat("&userid=" + URLEncoder.encode(String.valueOf(person_userid), "UTF-8"));
                }
                HttpURLConnection conn =
                        Service.getConnectionWithToken("/article/get_list", "GET", params);
                System.out.println("fetch Article conn established");

                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = Service.is2String(in);
                    System.out.println(result);

//                    JSONObject obj = new JSONObject(result);
                    article_list.clear(); // TODO: more efficient way
                    JSONArray arr = new JSONArray(result);
                    for (int i = article_list.size(); i < arr.length(); i++) {
                        Article article = Service.decodeArticle(arr.getJSONObject(i));
                        if (article != null) {
                            System.out.println(article.author_nickname + " " + article.author_username + " " + article.content);
                            article_list.add(article);
                        }
                    }

                    Message msg = new Message();
                    msg.setTarget(handler);
                    msg.sendToTarget();
                } else {
                    System.out.println("fetch failed");
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

    @SuppressLint("NotifyDataSetChanged")
    public void showMoreArticle(int more) {
        allowSize += more;
        fetchArticle();
        if (adapter != null) {
            adapter.notifyDataSetChanged(); // TODO
//                adapter.setIfMore(allowSize < article_list.size());
        }
    }

    public Article getByIndex(int index) {
        if (index < 0 || index >= count()) return null;
        return article_list.get(index);
    }

    public int count() {
        return Math.min(allowSize, article_list.size());
    }
}
