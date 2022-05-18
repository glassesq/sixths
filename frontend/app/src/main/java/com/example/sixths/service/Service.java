package com.example.sixths.service;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.sixths.activity.LoginActivity;
import com.example.sixths.activity.MainActivity;
import com.example.sixths.activity.UserActivity;
import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.activity.RegisterActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashSet;

public class Service {

    public enum POST_LIST_TYPE {ALL, FOLLOW, PERSON}

    public static int myself_id = -1;
    public static User myself = new User();

    private static Handler login_handler = null;
    private static Handler register_handler = null;
    private static Handler main_handler = null;
    private static Handler user_handler = null;

    public static final int START_ARTICLE_NUM = 100;
    public static final int MORE_ARTICLE_NUM = 100;

    private static final ArticleManager all_manager = new ArticleManager();
    private static final ArticleManager follow_manager = new ArticleManager();
    private static final ArticleManager person_manager = new ArticleManager();

    private static String token = null;

    private static final String url = "http://10.0.2.2:8080";

    public static HashSet<Integer> following = new HashSet<>();

    public static void setToken(String _token) {
        /* check 是否 存在token && token有效 */
        // TODO
        token = _token;
    }

    public static void setPerson(int id) {
        person_manager.setPerson(id);
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

    public static void setMainHandler(Handler handler) {
        main_handler = handler;
    }

    public static void setRegisterHandler(Handler handler) {
        register_handler = handler;
    }

    public static void setUserHandler(Handler handler) {
        user_handler = handler;
    }

    public static void logout() {
        token = null;
    }

    public static void setArticleAdapter(PostListAdapter adapter, POST_LIST_TYPE type) {
        if (type == POST_LIST_TYPE.ALL) {
            all_manager.setAdapter(adapter);
        }
        if (type == POST_LIST_TYPE.FOLLOW) {
            follow_manager.setAdapter(adapter);
        }
        if (type == POST_LIST_TYPE.PERSON) {
            person_manager.setAdapter(adapter);
        }
    }

    /* 网络工具 */
    public static HashSet<Integer> decodeIntegerSet(String ret) {
        HashSet<Integer> h = new HashSet<>();
        try {
            JSONArray arr = new JSONArray(ret);
            for (int i = 0; i < arr.length(); i++) {
                h.add(arr.getInt(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return h;
    }

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
        if (method.equals("GET")) {
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
        System.out.println(path + "-token:" + token);
        if (method.equals("GET")) {
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
                    System.out.println("success");

                    String result = is2String(in);
                    setToken(result);
                    System.out.println(result);

                    System.out.println("success login");
                    Message msg = new Message();
                    msg.what = LoginActivity.LOGIN_SUCCESS;
                    msg.setTarget(login_handler);
                    msg.sendToTarget();
                } else {
                    System.out.println("fail");
                    Message msg = new Message();
                    msg.what = LoginActivity.LOGIN_FAIL;
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
                    setToken(result);
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

    public static void getUserInfo(int userid) {
        System.out.println(userid);
        System.out.println("start get user");
        Thread thread = new Thread(() -> {
            try {
                String params = "userid=" + URLEncoder.encode(String.valueOf(userid), "UTF-8");
                HttpURLConnection conn = getConnectionWithToken("/user/get_info", "GET", params);
                System.out.println("get user conn established");

                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = is2String(in);
                    System.out.println(result);

                    Message msg = new Message();
                    msg.what = UserActivity.USER_SUCCESS;
                    Bundle bundle = new Bundle();
                    bundle.putString("info", result);
                    msg.setData(bundle);
                    msg.setTarget(user_handler);
                    msg.sendToTarget();
                } else {
                    Message msg = new Message();
                    msg.what = UserActivity.USER_FAIL;
                    msg.setTarget(user_handler);
                    msg.sendToTarget();
                }
                System.out.println("get user finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void getMyself() {
        Thread thread = new Thread(() -> {
            try {
                /* 基本信息 */
                HttpURLConnection conn = getConnectionWithToken("/user/get_myself", "GET", "");
                System.out.println("get user conn established");

                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = is2String(in);
                    System.out.println(result);

                    myself = decodeUserInfo(result);
                    if (myself != null) myself_id = myself.id;
                }
                System.out.println("get user finished");

                /* following list */
                conn = getConnectionWithToken("/user/get_following", "GET", "");
                System.out.println("get following conn established");

                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = is2String(in);
                    System.out.println(result);

                    following = decodeIntegerSet(result);
                }
                System.out.println("get user following finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

    }

    public static User decodeUserInfo(String str) {
        try {
            User user = new User();
            JSONObject obj = new JSONObject(str);
            user.id = obj.getInt("id");
            user.nickname = obj.getString("nickname");
            user.name = obj.getString("name");
            user.bio = obj.getString("bio");
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void setUserInfo(String nickname, String bio) {
        Thread thread = new Thread(() -> {
            try {
                String params = "nickname=" + URLEncoder.encode(nickname, "UTF-8")
                        + "&bio=" + URLEncoder.encode(bio, "UTF-8");
                HttpURLConnection conn = getConnectionWithToken("/user/set_info", "POST", params);
                System.out.println("set info conn established");

                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = is2String(in);
                    System.out.println(result);
                }
                getMyself();
                System.out.println("set user info finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static boolean isFollow(int userid) {
        return following.contains(userid);
    }

    public static void followUser(int userid) {
        Thread thread = new Thread(() -> {
            try {
                String params = "follow_id=" + URLEncoder.encode(String.valueOf(userid), "UTF-8");
                HttpURLConnection conn = getConnectionWithToken("/user/follow", "POST", params);
                System.out.println("follow conn established");

                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    // TODO: 在此处对follow更新
                    following.add(userid);

                    Message msg = new Message();
                    msg.what = UserActivity.USER_FOLLOW;
                    msg.setTarget(user_handler);
                    msg.sendToTarget();
                }
                getMyself();
                fetchArticle(POST_LIST_TYPE.ALL);
                fetchArticle(POST_LIST_TYPE.FOLLOW);
                System.out.println("follow info finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

    }

    public static void unfollowUser(int userid) {
        Thread thread = new Thread(() -> {
            try {
                String params = "follow_id=" + URLEncoder.encode(String.valueOf(userid), "UTF-8");
                HttpURLConnection conn = getConnectionWithToken("/user/unfollow", "POST", params);
                System.out.println("unfollow conn established");

                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    // TODO: 在此处对follow更新
                    following.remove(userid);

                    Message msg = new Message();
                    msg.what = UserActivity.USER_FOLLOW;
                    msg.setTarget(user_handler);
                    msg.sendToTarget();
                }
                getMyself();
                fetchArticle(POST_LIST_TYPE.ALL);
                fetchArticle(POST_LIST_TYPE.FOLLOW);
                System.out.println("unfollow info finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

    }


    /* article */
    public static Article decodeArticle(String str) {
        try {
            JSONObject obj = new JSONObject(str); //arr.getJSONObject();
            return decodeArticle(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Article decodeArticle(JSONObject obj) {
        try {
            Article article = new Article();
            article.author_nickname = obj.getJSONObject("author").getString("nickname");
            article.author_username = obj.getJSONObject("author").getString("name");
            article.author_id = obj.getJSONObject("author").getInt("id");
            article.content = obj.getString("content");
            article.position = obj.getString("position");
            if (article.position.equals("null")) article.position = null;
            article.likes = obj.getInt("likes");
            article.time = obj.getString("time");
            System.out.println(article.author_nickname + " " + article.author_username + " " + article.content);
            return article;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    public static String wrapInt(int num) {
        System.out.println(num);
        if (num < 100) return String.valueOf(num);
        return "99+";
    }

    public static Article getArticle(int index, POST_LIST_TYPE type) {
        if (type == POST_LIST_TYPE.FOLLOW) {
            return follow_manager.getByIndex(index);
        }
        if (type == POST_LIST_TYPE.PERSON) {
            return person_manager.getByIndex(index);
        }
        return all_manager.getByIndex(index);
    }

    public static int getArticleCount(POST_LIST_TYPE type) {
        if (type == POST_LIST_TYPE.FOLLOW) {
            return follow_manager.count();
        }
        if (type == POST_LIST_TYPE.PERSON) {
            return person_manager.count();
        }
        return all_manager.count();
    }

    public static void fetchArticle(POST_LIST_TYPE type) {
        if (type == POST_LIST_TYPE.FOLLOW) {
            follow_manager.fetchArticle();
        }
        if (type == POST_LIST_TYPE.PERSON) {
            person_manager.fetchArticle();
        }
        all_manager.fetchArticle();
    }

    public static void makeArticle(String content, String location) {
        System.out.println("start make article");
        Thread thread = new Thread(() -> {
            try {
                String params = "content=" + URLEncoder.encode(content, "UTF-8");
                if (location != null) {
                    params = params.concat("&position=" + URLEncoder.encode(location, "UTF-8"));
                }
                System.out.println(params);
                HttpURLConnection conn = getConnectionWithToken("/article", "POST", params);
                System.out.println("make article conn established");

                if (conn.getResponseCode() == 200) {
                    Message msg = new Message();
                    msg.what = MainActivity.NEW_SUCCESS;
                    msg.setTarget(main_handler);
                    msg.sendToTarget();
                } else {
                    Message msg = new Message();
                    msg.what = MainActivity.NEW_FAIL;
                    msg.setTarget(main_handler);
                    msg.sendToTarget();
                }
                System.out.println("make article finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

}
