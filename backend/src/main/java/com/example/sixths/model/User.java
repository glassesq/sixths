package com.example.sixths.model;


import com.example.sixths.model.Article;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.*;
import java.util.List;

/* ref: https://spring.io/guides/gs/accessing-data-mysql/ */
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String openid;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY) // TODO: cascade
    private List<Article> articles;

    private String email;

    public User() {
    }

    @JsonIgnore
    public List<Article> getArticle() {
        return articles;
    }

    public Integer getId() {
        return id;
    }

    public String getOpenid() {
        if (openid == null)
            openid = DigestUtils.sha256Hex("string-open-id" + id.toString()).substring(32);
        return openid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

}
