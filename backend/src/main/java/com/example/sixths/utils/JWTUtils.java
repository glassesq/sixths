package com.example.sixths.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sixths.model.User;

import java.util.Calendar;
import java.util.Date;

/* ref: https://blog.csdn.net/qq_43948583/article/details/104437752 */
public class JWTUtils {
    public static String genUserToken(User user) {
        Calendar currentTime = Calendar.getInstance();
        System.out.print("gen user token: ");
        System.out.println(currentTime);
        // TODO: only generate token once a day.
        return JWT.create().withAudience(user.getId().toString())
                .withIssuedAt(new Date())
                .withClaim("name", user.getName())
                .sign(Algorithm.HMAC256("big-secret"));
    }

    public static boolean verifyUserToken(String token) {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256("big-secret")).build();
            jwt = verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getId(String token) {
        try {
            DecodedJWT jwt = null;
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256("big-secret")).build();
            jwt = verifier.verify(token);
            return Integer.parseInt(jwt.getAudience().get(0));
        } catch (Exception e) {
            return -1;
        }
    }

    public static Claim getClaim(String token, String key) {
        return JWT.decode(token).getClaim(key);
    }
}
