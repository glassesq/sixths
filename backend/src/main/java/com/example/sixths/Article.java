package com.example.sixths;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;


@Entity
public class Article {

    // generate articleid to user: secret-key: Arti-Sec, algorithm: DES
    @JsonIgnore
    static final SecretKeySpec sks = new SecretKeySpec("Arti-Sec".getBytes(), "DES");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public String articleid;

    public String content;

    public Article() {
        content = "";
    }

    @JsonIgnore
    public Integer getId() {
        return id;
    }

    public String getArticleid() {
        if( articleid == null ) articleid = encryptId();
        return articleid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String encryptId() {
        try {
            Cipher cipher = Cipher.getInstance("DES");
            String str_id = Integer.toHexString(id);
            str_id = "00000000".concat(str_id).substring(str_id.length()); // put 0 on the front of id in string
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            byte[] bytes = cipher.doFinal(str_id.getBytes());
            return new String(Base64.getUrlEncoder().encode(bytes), StandardCharsets.UTF_8); // use UrlEncoder in case "+" exception in url
        } catch (Exception e) {
            e.printStackTrace();
            return "fail when encrypt articleid";
        }
    }

    static public int decryptId(String str_id) {
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            byte[] bytes = cipher.doFinal(Base64.getUrlDecoder().decode(str_id)); // use UrlEncoder in case "+" exception in url
            str_id = new String(bytes);
            return Integer.parseInt(str_id);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}