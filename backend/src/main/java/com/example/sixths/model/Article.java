package com.example.sixths.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;


@Entity
public class Article {

    /**
     * generate articleid to user: secret-key: Arti-Sec, algorithm: DES
     **/
    @JsonIgnore
    static final SecretKeySpec sks = new SecretKeySpec("Arti-Sec".getBytes(), "DES");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne // ? fetchType
    @JoinColumn(name = "author_id")
    private User author; // the author of the post

    @JsonIgnore
    @ManyToMany(mappedBy = "likeArticles", fetch = FetchType.LAZY)
    private Set<User> liker;

    public Date time;

    public String content;

    public String position;

    public User getAuthor() {
        return author;
    }

    public Article() {
        content = "";
    }

    public Integer getId() {
        return id;
    }

    public int getLikes() {
        return liker.size();
    }

    public String getContent() {
        return content;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return sdf.format(time);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

/*    private String encryptId() {
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
    } */
}