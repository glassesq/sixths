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

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY) // TODO: cascade
    private List<Article> articles;

    @ManyToMany(fetch = FetchType.LAZY) // TODO: cascade
    /*    @JoinTable(joinColumns = {@JoinColumn(name = "blocked_by_id")}, inverseJoinColumns = {@JoinColumn(name = "block_user_id")}) */
    private List<User> block_users;

    @ManyToMany(mappedBy = "block_users") // TODO: cascade
    private List<User> blocked_by;

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

    public boolean addBlock(User block_user) {
        try {
            if (block_user == null || block_user.getId().equals(this.getId())) return false;
//     //   User check = this.block_users.stream().filter(t -> t.getId() == block_id).findFirst().orElse(null);
            // if (this.block_users.contains(block_user)) return false;
            this.block_users.add(block_user);
            return true;
        }catch (Exception e) {
            return false;
        }
    }
}
