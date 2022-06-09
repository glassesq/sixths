package com.example.sixths.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


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
    @ManyToMany(mappedBy = "liking")
    private Set<User> liker;

    public boolean draft;

    public Date time;

    public String title;

    public String image;

    public String content;

    public String position;

    public String video;

    public String audio;

    @JsonIgnore
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY) // TODO: cascade
    private List<Comment> comments;

    @JsonIgnore
    public List<Comment> getCommentList() {
        return comments;
    }

    public boolean getDraft() {
        return draft;
    }

    public void setDraft(boolean d) {
        this.draft = d;
    }

    public int getComment_num() {
        return comments.size();
    }

    public User getAuthor() {
        return author;
    }

    public Article() {
        content = "";
    }


    @JsonIgnore
    public Date getRealTime() {
        return time;
    }

    public Set<User> getLiker() {
        return liker;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null) this.title = title;
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