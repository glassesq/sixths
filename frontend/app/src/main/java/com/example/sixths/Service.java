package com.example.sixths;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.common.base.Strings;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Service {

    private static Handler login_handler = null;
    private static Handler register_handler = null;

    private static String token = null;

    private static final String url = "http://10.0.2.2:8080";

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


    private static String is2String(InputStream is) throws Exception {
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

    private static HttpURLConnection getConnection(String _url, String params) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(_url).openConnection();
        conn.setRequestMethod("POST");
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
                HttpURLConnection conn = getConnection(url + "/user/login", params);

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
                HttpURLConnection conn = getConnection(url + "/user/register", params);

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
}
