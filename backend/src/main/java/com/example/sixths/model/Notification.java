package com.example.sixths.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.crypto.spec.SecretKeySpec;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Integer article_id;

    @JsonIgnore
    @ManyToMany()
    private Set<User> target;

    public Date time;

    public String content;

    public String type;

    public boolean checked;

    public boolean getChecked() {
        return checked;
    }

    public void setChecked() {
        checked = true;
    }

    public Notification() {
        content = "";
        checked = false;
    }

    public Integer getId() {
        return id;
    }

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int id) {
        this.article_id = id;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public void setTarget(Set<User> target) {
        this.target = new HashSet<>(target);
    }

    public Set<User> getTarget() {
        return target;
    }

}