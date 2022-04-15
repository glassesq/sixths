package com.example.sixths;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Calendar;
import java.util.Date;

/* ref: https://blog.csdn.net/qq_43948583/article/details/104437752 */
public class JWTUtils {
    public static String genUserToken(User user) {
        Calendar currentTime = Calendar.getInstance();
        System.out.println(currentTime);

        return JWT.create().withAudience(user.getUserId())
                .withIssuedAt(new Date())
                .withClaim("name", user.getName())
                .withClaim("id", user.getId())
                .sign(Algorithm.HMAC256(user.getUserId()));
    }

    public static boolean verifyUserToken(String token, String secret_userid) {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret_userid)).build();
            jwt = verifier.verify(token);
            System.out.println(jwt.getAudience());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
