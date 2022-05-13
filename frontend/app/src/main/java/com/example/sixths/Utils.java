package com.example.sixths;

import android.annotation.SuppressLint;

import com.google.common.base.Strings;

public class Utils {
    private static String token = null;

    public static boolean checkToken() {
        /* check 是否 存在token && token有效 */
        // TODO
        return token != null;
    }

    public static boolean signIn(String username, String password) {
        /* check 是否 存在token && token有效 */
        if( username != null && password != null) {
            // TODO
            token = "token";
            return true;
        }
        return false;
    }
}
