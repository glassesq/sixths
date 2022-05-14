package com.example.sixths.service;

import android.os.Handler;
import android.os.Message;

import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.activity.RegisterActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class Service {

    public enum POST_LIST_TYPE {ALL, FOLLOW}

    private static Handler login_handler = null;
    private static Handler register_handler = null;

    public static final int START_ARTICLE_NUM = 100;
    public static final int MORE_ARTICLE_NUM = 100;

    private static final ArticleManager all_manager = new ArticleManager();
    private static final ArticleManager follow_manager = new ArticleManager();

    private static String token = null;

    private static final String url = "http://10.0.2.2:8080";

    public static void setToken(String _token) {
        /* check 是否 存在token && token有效 */
        // TODO
        token = _token;
    }

    public static String getToken() {
        return token;
    }

    public static boolean checkToken() {
        /* check 是否 存在token && token有效 */
        // TODO
        return token != null;
    }

    public static void setLoginHandler(Handler handler) {
        login_handler = handler;
    }

    public static void setRegisterHandler(Handler handler) {
        register_handler = handler;
    }

    public static void setArticleAdapter(PostListAdapter adapter, POST_LIST_TYPE type) {
        if (type == POST_LIST_TYPE.ALL) {
            all_manager.setAdapter(adapter);
        }
        if (type == POST_LIST_TYPE.FOLLOW) {
            follow_manager.setAdapter(adapter);
        }
    }

    /* 网络工具 */

    public static String is2String(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        while ((line = reader.readLine()) != null) {
            if (!first) builder.append("\n");
            builder.append(line);
            first = false;
        }
        if (builder.length() == 0) return null;
        return builder.toString();
    }

    public static HttpURLConnection getConnection(String path, String method, String params) throws Exception {
        if( method.equals("GET") ) {
            HttpURLConnection conn = (HttpURLConnection) new URL(url + path + "?" + params).openConnection();
            conn.setRequestMethod(method);
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            return conn;
        }
        HttpURLConnection conn = (HttpURLConnection) new URL(url + path).openConnection();
        conn.setRequestMethod(method);
        conn.setReadTimeout(5000);
        conn.setConnectTimeout(5000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);


        OutputStream out = conn.getOutputStream();
        out.write(params.getBytes());
        out.flush();
        conn.connect();

        return conn;
    }


    public static HttpURLConnection getConnectionWithToken(String path, String method, String params) throws Exception {

        if( method.equals("GET") ) {
            HttpURLConnection conn = (HttpURLConnection) new URL(url + path + "?" + params).openConnection();
            conn.setRequestMethod(method);
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("token", token);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            return conn;
        }
        HttpURLConnection conn = (HttpURLConnection) new URL(url + path).openConnection();
        conn.setRequestMethod(method);
        conn.setReadTimeout(5000);
        conn.setConnectTimeout(5000);
        conn.setRequestProperty("token", token);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        OutputStream out = conn.getOutputStream();
        out.write(params.getBytes());
        out.flush();
        conn.connect();

        return conn;
    }

    /* 用户 */

    private static String encryptPassword(String password) throws Exception {
        MessageDigest messageDigest;
        messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(password.getBytes(StandardCharsets.UTF_8));
        String encrypt_password = new String(Base64.getUrlEncoder().encode(messageDigest.digest()), StandardCharsets.UTF_8);
        System.out.println(encrypt_password);
        return encrypt_password;
    }

    public static void signIn(String email, String password) {
        Thread thread = new Thread(() -> {
            try {
                /* check 是否 存在token && token有效 */
                System.out.println("signIn: email " + email + "password " + password);
                // TODO: check username and password not null.

                String params = "password=" + URLEncoder.encode(encryptPassword(password), "UTF-8")
                        + "&email=" + URLEncoder.encode(email, "UTF-8");
                HttpURLConnection conn = getConnection("/user/login", "POST", params);

                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = is2String(in);
                    token = result;
                    System.out.println(result);

                    Message msg = new Message();
                    msg.what = 1;
                    msg.setTarget(login_handler);
                    msg.sendToTarget();
                } else {
                    Message msg = new Message();
                    msg.what = 0;
                    msg.setTarget(login_handler);
                    msg.sendToTarget();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void register(String username, String email, String password) {
        Thread thread = new Thread(() -> {
            try {
                /* check 是否 存在token && token有效 */
                System.out.println("register: email " + email + "password " + password);
                // TODO: check username and password not null.

                String params = "password=" + URLEncoder.encode(encryptPassword(password), "UTF-8")
                        + "&email=" + URLEncoder.encode(email, "UTF-8")
                        + "&name=" + URLEncoder.encode(username, "UTF-8");
                HttpURLConnection conn = getConnection("/user/register", "POST", params);

                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = is2String(in);
                    token = result;
                    System.out.println(result);

                    Message msg = new Message();
                    msg.what = RegisterActivity.REG_SUCCESS;
                    msg.setTarget(register_handler);
                    msg.sendToTarget();
                } else {
//                        in = conn.getErrorStream();

                    Message msg = new Message();
                    msg.what = RegisterActivity.REG_FAIL;
                    msg.setTarget(register_handler);
                    msg.sendToTarget();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    /* article */
    public static Article getArticle(int index, POST_LIST_TYPE type) {
        if (type == POST_LIST_TYPE.FOLLOW) {
            return follow_manager.getByIndex(index);
        }
        return all_manager.getByIndex(index);
    }

    public static int getArticleCount(POST_LIST_TYPE type) {
        if (type == POST_LIST_TYPE.FOLLOW) {
            return follow_manager.count();
        }
        return all_manager.count();
    }

    public static void fetchArticle(POST_LIST_TYPE type) {
        if (type == POST_LIST_TYPE.FOLLOW) {
            follow_manager.fetchArticle();
        }
        all_manager.fetchArticle();
    }

}
