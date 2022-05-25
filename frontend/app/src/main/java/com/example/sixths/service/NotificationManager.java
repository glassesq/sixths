package com.example.sixths.service;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.sixths.adapter.NotificationListAdapter;

import org.json.JSONArray;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class NotificationManager {

    public boolean unchecked = false;

    public int allowSize = Service.START_ARTICLE_NUM;
    private ArrayList<Notification> noti_list = new ArrayList<>();
    private NotificationListAdapter adapter = null;

    public void setAdapter(NotificationListAdapter adapter) {
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
            Service.notiGot();
        }
    };

    public void fetchNotification() {
        System.out.println("fetch Notification");
        Thread thread = new Thread(() -> {
            try {
                String params = "";
                HttpURLConnection conn = Service.getConnectionWithToken("/user/get_notification", "GET", params);

                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = Service.is2String(in);
                    System.out.println(result);

                    JSONArray arr = new JSONArray(result);
                    if (arr.length() != noti_list.size()) {
                        boolean tmp_checked = true;
                        ArrayList<Notification> list = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            Notification noti = Service.decodeNotification(arr.getJSONObject(i));
                            if (noti != null) {
                                list.add(noti);
                                tmp_checked = tmp_checked & noti.checked;
                            }
                        }
                        noti_list = list;
                        unchecked = !tmp_checked;
                        System.out.println("unchecked: " + unchecked);

                        Message msg = new Message();
                        msg.setTarget(handler);
                        msg.sendToTarget();
                    }
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

    public Notification getByIndex(int index) {
        if (index < 0 || index >= count()) return null;
        return noti_list.get(index);
    }

    public int count() {
        return Math.min(allowSize, noti_list.size());
    }

    public void freshUncheck() {
        boolean tmp_check = true;
        for(Notification n : noti_list) {
            tmp_check = tmp_check & n.checked;
        }
        unchecked = !tmp_check;
    }
}
