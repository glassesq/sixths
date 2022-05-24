package com.example.sixths.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.sixths.R;
import com.example.sixths.activity.ArticleActivity;
import com.example.sixths.activity.LoginActivity;
import com.example.sixths.activity.MainActivity;
import com.example.sixths.activity.NewActivity;
import com.example.sixths.activity.UserActivity;
import com.example.sixths.adapter.CommentListAdapter;
import com.example.sixths.adapter.PostListAdapter;
import com.example.sixths.activity.RegisterActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashSet;

public class Service {

    public enum POST_LIST_TYPE {ALL, FOLLOW, PERSON, DRAFT}

    public static String publicPath = "";

    public static int myself_id = -1;
    public static User myself = new User();

    private static Handler login_handler = null;
    private static Handler register_handler = null;
    private static Handler main_handler = null;
    private static Handler user_handler = null;
    private static Handler article_handler = null;
    private static Handler new_handler = null;

    public static final int START_ARTICLE_NUM = 100;
    public static final int MORE_ARTICLE_NUM = 100;

    public static final int DEEP_FRESH = 0;
    public static final int FRESH = 1;
    public static final int COMMENT_FRESH = 2;
    public static final int COMMENT_DEEP_FRESH = 3;

    public static int COLOR_BLUE;
    public static int COLOR_BLACK;
    public static int COLOR_WHITE;
    public static int COLOR_RED;
    public static int COLOR_GREY;

    private static final ArticleManager all_manager = new ArticleManager();
    private static final ArticleManager follow_manager = new ArticleManager();
    private static final ArticleManager person_manager = new ArticleManager();
    private static final ArticleManager draft_manager = new ArticleManager();

    private static final CommentManager comment_manager = new CommentManager();

    private static String token = null;

    private static final String url = "http://10.0.2.2:8080";

    public static HashSet<Integer> following = new HashSet<>();
    public static HashSet<Integer> liking = new HashSet<>();

    private static final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == DEEP_FRESH) {
                deepfresh();
            } else if (msg.what == FRESH) {
                fresh();
            } else if (msg.what == COMMENT_FRESH) {
                commentfresh();
            } else if (msg.what == COMMENT_DEEP_FRESH) {
                commentDeepFresh();
            }
        }
    };

    public static void fresh() {
        all_manager.fresh();
        follow_manager.fresh();
        person_manager.fresh();
    }

    public static void deepfresh() {
        fetchArticle(POST_LIST_TYPE.ALL);
        fetchArticle(POST_LIST_TYPE.FOLLOW);
        fetchArticle(POST_LIST_TYPE.PERSON);
    }

    public static void commentfresh() {
        comment_manager.fresh();
    }

    public static void commentDeepFresh() {
        comment_manager.fetchComment();
    }

    public static void initStorage(String path) {
        publicPath = path.concat("/statics");
        File dir = new File(publicPath);
        dir.mkdirs();
    }

    public static void initColor(Context context) {
        COLOR_BLACK = ContextCompat.getColor(context, R.color.black);
        COLOR_WHITE = ContextCompat.getColor(context, R.color.white);
        COLOR_BLUE = ContextCompat.getColor(context, R.color.blue);
        COLOR_RED = ContextCompat.getColor(context, R.color.red);
        COLOR_GREY = ContextCompat.getColor(context, R.color.grey);
    }

    public static void setFollow() {
        follow_manager.setFollow();
    }

    public static void setDraft() {
        draft_manager.setDraft();
    }

    public static void setToken(String _token) {
        /* check 是否 存在token && token有效 */
        // TODO
        token = _token;
    }

    public static void setPerson(int id) {
        person_manager.setPerson(id);
    }

    public static void setCommentArticle(int id) {
        comment_manager.setArticle(id);
    }

    public static String getToken() {
        return token;
    }

    public static boolean checkToken() {
        /* check 是否 存在token && token有效 */
        // TODO
        return token != null;
    }

    public static void setNewHandler(Handler handler) {
        new_handler = handler;
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

    public static void setArticleHandler(Handler handler) {
        article_handler = handler;
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

    public static void setCommentAdapter(CommentListAdapter adapter) {
        comment_manager.setAdapter(adapter);
    }

    /* 网络工具 */
    public static String checkStr(JSONObject obj, String name) {
        try {
            if (obj.isNull(name)) return null;
            return obj.getString(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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

    public static void sendMessage(Handler _handler, int _what) {
        if (_handler == null) return;
        Message msg = new Message();
        System.out.println("message send");
        msg.what = _what;
        msg.setTarget(_handler);
        msg.sendToTarget();
    }

    public static void sendMessage(Handler _handler, int _what, String data) {
        if (_handler == null) return;
        System.out.println("message send");
        Message msg = new Message();
        msg.what = _what;
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        msg.setData(bundle);
        msg.setTarget(_handler);
        msg.sendToTarget();
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
                    fetchImage(myself);
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

                /* liking list */
                conn = getConnectionWithToken("/article/get_liking", "GET", "");
                System.out.println("get liking conn established");

                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = is2String(in);
                    System.out.println(result);

                    liking = decodeIntegerSet(result);
                    System.out.println("article fresh");

                    sendMessage(article_handler, ArticleActivity.ARTICLE_FRESH);
                }

                sendMessage(handler, FRESH);
                System.out.println("get myself finished");
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

            user.profile = checkStr(obj, "profile");
            System.out.println("profile:" + user.profile);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setUserInfo(String nickname, String bio, String profile) {
        Thread thread = new Thread(() -> {
            try {
                String params = "nickname=" + URLEncoder.encode(nickname, "UTF-8")
                        + "&bio=" + URLEncoder.encode(bio, "UTF-8");
                if (profile != null) {
                    params = params.concat("&profile=" + URLEncoder.encode(profile, "UTF-8"));
                }
                System.out.println(profile);
                System.out.println(params);
                HttpURLConnection conn = getConnectionWithToken("/user/set_info", "POST", params);
                System.out.println("set info conn established");

                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = is2String(in);
                    System.out.println(result);
                }
                getMyself();
                sendMessage(handler, DEEP_FRESH);
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

    public static boolean isLike(int articleid) {
        return liking.contains(articleid);
    }

    public static void followUser(int userid) {
        Thread thread = new Thread(() -> {
            try {
                String params = "follow_id=" + URLEncoder.encode(String.valueOf(userid), "UTF-8");
                HttpURLConnection conn = getConnectionWithToken("/user/follow", "POST", params);
                System.out.println("follow conn established");
                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    getMyself();

                    sendMessage(handler, DEEP_FRESH);
                    sendMessage(main_handler, MainActivity.FRESH_PROFILE);
                    sendMessage(user_handler, UserActivity.USER_FOLLOW);
                }
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
                    getMyself();

                    sendMessage(handler, DEEP_FRESH);

                    sendMessage(user_handler, UserActivity.USER_UNFOLLOW);
                }

                System.out.println("unfollow info finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

    }

    /* article */
    public static Article decodeArticle(JSONObject obj, Article article) {
        try {
            article.author_nickname = obj.getJSONObject("author").getString("nickname");
            article.author_username = obj.getJSONObject("author").getString("name");
            article.author_id = obj.getJSONObject("author").getInt("id");

            article.id = obj.getInt("id");

            article.title = checkStr(obj, "title");
            article.content = obj.getString("content");
            article.position = checkStr(obj, "position");

            article.image = checkStr(obj, "image");
            article.image_fetched = checkFile(article.image);
            article.audio = checkStr(obj, "audio");
            article.audio_fetched = checkFile(article.audio);
            article.video = checkStr(obj, "video");
            article.video_fetched = checkFile(article.video);
            article.author_profile = checkStr(obj.getJSONObject("author"), "profile");
            article.profile_fetched = checkFile(article.author_profile);

            // fetchMedia(article);
            article.comments = obj.getInt("comment_num");
            article.likes = obj.getInt("likes");
            article.time = obj.getString("time");
            return article;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
            decodeArticle(obj, article);
            return article;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Article getArticle(int index, POST_LIST_TYPE type) {
        if (type == POST_LIST_TYPE.FOLLOW) {
            return follow_manager.getByIndex(index);
        }
        if (type == POST_LIST_TYPE.PERSON) {
            return person_manager.getByIndex(index);
        }
        if (type == POST_LIST_TYPE.DRAFT) {
            return draft_manager.getByIndex(index);
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
        if (type == POST_LIST_TYPE.DRAFT) {
            return draft_manager.count();
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
        if (type == POST_LIST_TYPE.DRAFT) {
            draft_manager.fetchArticle();
        }
        all_manager.fetchArticle();
    }

    public static void makeArticle(int article_id, String content, String location, String title, String image, String video, String audio) {
        System.out.println("start make article");
        Thread thread = new Thread(() -> {
            try {
                String params = "content=" + URLEncoder.encode(content, "UTF-8");
                if (location != null) {
                    params = params.concat("&position=" + URLEncoder.encode(location, "UTF-8"));
                }
                if (title != null) {
                    params = params.concat("&title=" + URLEncoder.encode(title, "UTF-8"));
                }
                if (image != null) {
                    params = params.concat("&image=" + URLEncoder.encode(image, "UTF-8"));
                }
                if (video != null) {
                    params = params.concat("&video=" + URLEncoder.encode(video, "UTF-8"));
                }
                if (audio != null) {
                    params = params.concat("&audio=" + URLEncoder.encode(audio, "UTF-8"));
                }
                if (article_id >= 0) {
                    params = params.concat("&article_id=" +
                            URLEncoder.encode(String.valueOf(article_id), "UTF-8"));
                }
                System.out.println(params);
                HttpURLConnection conn = getConnectionWithToken("/article", "POST", params);
                System.out.println("make article conn established");

                if (conn.getResponseCode() == 200) {
                    sendMessage(main_handler, MainActivity.NEW_SUCCESS);
                } else {
                    sendMessage(main_handler, MainActivity.NEW_FAIL);
                }
                System.out.println("make article finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }


    public static void makeDraft(int article_id, String content, String location, String title, String image, String video, String audio) {
        System.out.println("start make article draft");
        Thread thread = new Thread(() -> {
            try {
                String params = "content=" + URLEncoder.encode(content, "UTF-8");
                if (location != null) {
                    params = params.concat("&position=" + URLEncoder.encode(location, "UTF-8"));
                }
                if (title != null) {
                    params = params.concat("&title=" + URLEncoder.encode(title, "UTF-8"));
                }
                if (image != null) {
                    params = params.concat("&image=" + URLEncoder.encode(image, "UTF-8"));
                }
                if (video != null) {
                    params = params.concat("&video=" + URLEncoder.encode(video, "UTF-8"));
                }
                if (audio != null) {
                    params = params.concat("&audio=" + URLEncoder.encode(audio, "UTF-8"));
                }
                if (article_id >= 0) {
                    params = params.concat("&article_id=" +
                            URLEncoder.encode(String.valueOf(article_id), "UTF-8"));
                }
                System.out.println(params);
                HttpURLConnection conn = getConnectionWithToken("/article/draft", "POST", params);
                System.out.println("make article draft conn established");

                if (conn.getResponseCode() == 200) {
                    System.out.println("draft save");
                    sendMessage(main_handler, MainActivity.DRAFT);
                }
                System.out.println("make article draft finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void switchLike(int article_id) {
        if (isLike(article_id)) {
            unlikeArticle(article_id);
        } else {
            likeArticle(article_id);
        }
    }

    public static void likeArticle(int article_id) {
        Thread thread = new Thread(() -> {
            try {
                String params = "article_id=" + URLEncoder.encode(String.valueOf(article_id), "UTF-8");
                HttpURLConnection conn = getConnectionWithToken("/article/like", "POST", params);
                System.out.println("article conn established");
                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    getMyself();
                    sendMessage(handler, DEEP_FRESH);
                }
                System.out.println("article info finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void unlikeArticle(int article_id) {
        Thread thread = new Thread(() -> {
            try {
                String params = "article_id=" + URLEncoder.encode(String.valueOf(article_id), "UTF-8");
                HttpURLConnection conn = getConnectionWithToken("/article/unlike", "POST", params);
                System.out.println("article conn established");
                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    getMyself();
                    sendMessage(handler, DEEP_FRESH);
                }
                System.out.println("article info finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void getArticleInfo(int article_id) {
        System.out.println(article_id);
        System.out.println("start get article");
        Thread thread = new Thread(() -> {
            try {
                String params = "article_id=" + URLEncoder.encode(String.valueOf(article_id), "UTF-8");
                HttpURLConnection conn = getConnectionWithToken("/article/get_info", "GET", params);

                System.out.println("get article conn established");

                System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();

                    String result = is2String(in);
                    System.out.println(result);

                    sendMessage(article_handler, ArticleActivity.ARTICLE_SUCCESS, result);
                    sendMessage(new_handler, NewActivity.ARTICLE_SUCCESS, result);
                }
                System.out.println("get article finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    /* Display Utils */

    @NonNull
    public static String wrapInt(int num) {
        if (num < 100) return String.valueOf(num);
        return "99+";
    }

    /* image / video / audio */
    public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        /* https://stackoverflow.com/questions/26114661/how-to-upload-image-in-base64-on-server */
        System.out.println(bitmap.getByteCount());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.getEncoder().encodeToString(byteFormat);
        System.out.println(imgString.length());
        return imgString;
    }

    public static Bitmap getBitmap(InputStream input) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeStream(input, null, options);
    }

    public static File is2File(InputStream input, String type, String format) {
        try {
            Date stamp = new Date();
            String name = type + "_" + token.substring(0, 5) + String.valueOf(stamp.getTime()) + "." + format;

            File file = new File(publicPath, name);
            if (file.exists()) file.delete();
            file.createNewFile();

            FileOutputStream out = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];
            while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File is2File(InputStream input, String filepath) {
        try {
            String path = publicPath;
            String[] paths = filepath.split("/");
            File file;
            for (int i = 0; i < paths.length - 1; i++) {
                file = new File(path, paths[i]);
                if (!file.exists()) {
                    file.mkdirs();
                }
                path = path.concat("/" + paths[i]);
            }
            file = new File(path, paths[paths.length - 1]);

            if (file.exists()) file.delete();
            file.createNewFile();

            System.out.println("create: " + file.getPath());
            System.out.println("create ok");

            FileOutputStream out = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];
            while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            input.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File makeEmptyFile(String type, String format) {
        try {
            Date stamp = new Date();
            String name = "empty_" + type + "_" + token.substring(0, 5) + String.valueOf(stamp.getTime()) + "." + format;

            File file = new File(publicPath, name);
            if (file.exists()) file.delete();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkFile(String src) {
        if (src == null) return true;
        File file = new File(publicPath, src);
        return file.isFile() && file.exists();
    }

    public static boolean fetchResourceFromSrc(String src) {
        try {
            String webpath = url + "/res/" + src;
            InputStream input = new URL(webpath).openConnection().getInputStream();
            is2File(input, src);
            System.out.println("inputstream: " + webpath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void fetchImage(User user) {
        if (user == null) return;
        System.out.println("fetch image");
        if (user.profile_fetched) return;
        if (user.profile == null || checkFile(user.profile)) {
            user.profile_fetched = true;
            sendMessage(user_handler, UserActivity.USER_PHOTO);
            sendMessage(main_handler, MainActivity.FRESH_PROFILE);
        }
        if (user.profile_fetched) return;

        Thread thread = new Thread(() -> {
            try {
                if (!user.profile_fetched) {
                    if (fetchResourceFromSrc(user.profile)) {
                        user.profile_fetched = true;
                    }
                }
                sendMessage(user_handler, UserActivity.USER_PHOTO);
                sendMessage(main_handler, MainActivity.FRESH_PROFILE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void fetchImage(Article article) {
        if (article == null) return;
        if (article.author_profile == null || checkFile(article.author_profile)) {
            article.profile_fetched = true;
        }
        if (article.image == null || checkFile(article.image)) {
            article.image_fetched = true;
        }
        if (article.profile_fetched && article.image_fetched) {
            sendMessage(article_handler, ArticleActivity.ARTICLE_RESOURCE);
            sendMessage(new_handler, NewActivity.ARTICLE_RESOURCE);
            sendMessage(main_handler, MainActivity.FRESH);
            return;
        }
        Thread thread = new Thread(() -> {
            try {
                if (!article.profile_fetched) {
                    if (fetchResourceFromSrc(article.author_profile)) {
                        article.profile_fetched = true;
                    }
                }
                if (!article.image_fetched) {
                    if (fetchResourceFromSrc(article.image)) {
                        article.image_fetched = true;
                    }
                }
                sendMessage(main_handler, MainActivity.FRESH);
                sendMessage(new_handler, NewActivity.ARTICLE_RESOURCE);
                sendMessage(article_handler, ArticleActivity.ARTICLE_RESOURCE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void fetchImage(Comment comment) {
        if (comment == null) return;
        System.out.println("fetch image");
        if (comment.profile_fetched) return;
        if (comment.author_profile == null || checkFile(comment.author_profile)) {
            comment.profile_fetched = true;
            sendMessage(handler, COMMENT_FRESH);
        }
        if (comment.profile_fetched) return;

        Thread thread = new Thread(() -> {
            try {
                if (!comment.profile_fetched) {
                    if (fetchResourceFromSrc(comment.author_profile)) {
                        comment.profile_fetched = true;
                    }
                }
                sendMessage(handler, COMMENT_FRESH);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void fetchMedia(Article article) {
        if (article == null) return;
        System.out.println("fetch video & audio");
        if (article.video == null || checkFile(article.video)) {
            article.video_fetched = true;
        }
        if (article.audio == null || checkFile(article.audio)) {
            article.audio_fetched = true;
        }
        if (article.audio_fetched && article.video_fetched) {
            sendMessage(article_handler, ArticleActivity.ARTICLE_RESOURCE);
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                if (!article.audio_fetched) {
                    if (fetchResourceFromSrc(article.audio)) {
                        article.audio_fetched = true;
                        System.out.println("audio got");
                    }
                }
                if (!article.video_fetched) {
                    if (fetchResourceFromSrc(article.video)) {
                        article.video_fetched = true;
                        System.out.println("video got");
                    }
                }
                sendMessage(new_handler, NewActivity.ARTICLE_RESOURCE);
                sendMessage(article_handler, ArticleActivity.ARTICLE_RESOURCE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static Bitmap getImageBitmap(String src) {
        try {
            System.out.println("get image");
            File file = new File(publicPath + "/" + src);
            System.out.println(publicPath + "/" + src);
            System.out.println(file.isFile());
            System.out.println(file.exists());
            InputStream is = new FileInputStream(file);
            return getBitmap(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // /data/user/0/com.example.sixths/files/statics/images/image_1652884612878.jpeg
        // /data/user/0/com.example.sixths/files/statics/image_1652884612878.jpeg
        return null;
    }

    public static Uri getResourceUri(String src) {
        try {
            File file = new File(publicPath + "/" + src);
            return Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

/*    public static Uri getImageUri(String src) {
        return getResourceUri(src);
    } */

    public static void uploadImage(int what, Handler handler, InputStream input, String type) {
        String[] types = type.split("/");
        uploadResource(what, handler, input, types[0], types[1]);
    }

    public static void uploadVideo(int what, Handler handler, InputStream input) {
        // TODO: format
        uploadResource(what, handler, input, "video", "flv");
    }

    public static void uploadAudio(int what, Handler handler, InputStream input) {
        // TODO: format
        uploadResource(what, handler, input, "audio", "aac");
    }

    private static void uploadResource(int what, Handler handler, InputStream input,
                                       String type, String format) {
        Thread thread = new Thread(() -> {
            try {
                File file = null;

                file = is2File(input, type, format);

                if (file == null) return;

                MultipartUtility multipart = new MultipartUtility(url + "/resource/upload", "UTF-8");

                multipart.addFormField("type", type);
                multipart.addFilePart(type, file);

                List<String> response = multipart.finish();
                String s = "";
                for (String line : response) {
                    System.out.println(line);
                    s = s.concat(line);
                }

                if (handler != null) {
                    sendMessage(handler, what, s);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    /* comment */
    public static Comment decodeComment(String str) {
        try {
            JSONObject obj = new JSONObject(str); //arr.getJSONObject();
            return decodeComment(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Comment decodeComment(JSONObject obj) {
        try {
            Comment comment = new Comment();
            comment.author_nickname = obj.getJSONObject("author").getString("nickname");
            comment.author_username = obj.getJSONObject("author").getString("name");
            comment.author_id = obj.getJSONObject("author").getInt("id");

            comment.id = obj.getInt("id");
            comment.article_id = obj.getInt("article_id");

            comment.content = obj.getString("content");
            comment.author_profile = checkStr(obj.getJSONObject("author"), "profile");

            comment.time = obj.getString("time");
            return comment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Comment getComment(int index) {
        return comment_manager.getByIndex(index);
    }

    public static int getCommentCount() {
        return comment_manager.count();
    }

    public static void fetchComment() {
        comment_manager.fetchComment();
    }

    public static void makeComment(int article_id, String content) {
        System.out.println("start make comment");
        Thread thread = new Thread(() -> {
            try {
                String params = "content=" + URLEncoder.encode(content, "UTF-8")
                        + "&article_id=" + URLEncoder.encode(String.valueOf(article_id), "UTF-8");
                System.out.println(params);
                HttpURLConnection conn = getConnectionWithToken("/article/add_comment", "POST", params);
                System.out.println("make comment conn established");

                if (conn.getResponseCode() == 200) {
                    sendMessage(handler, COMMENT_FRESH);
                    sendMessage(handler, DEEP_FRESH);
                }
                System.out.println("make comment finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static void removeComment(int comment_id) {
        Thread thread = new Thread(() -> {
            try {
                String params = "comment_id=" + URLEncoder.encode(String.valueOf(comment_id), "UTF-8");
                System.out.println(params);
                HttpURLConnection conn = getConnectionWithToken("/article/remove_comment", "POST", params);
                System.out.println("make comment conn established");

                if (conn.getResponseCode() == 200) {
                    sendMessage(handler, COMMENT_DEEP_FRESH);
                    sendMessage(article_handler, ArticleActivity.ARTICLE_FRESH);
                }
                System.out.println("make comment finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
