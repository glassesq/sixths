package com.example.sixths.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/* ref: https://spring.io/guides/gs/accessing-data-mysql/ */
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String nickname;

    @JsonIgnore
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY) // TODO: cascade
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY) // TODO: cascade
    private List<Article> articles;

    @ManyToMany() // TODO: cascade
    /*    @JoinTable(joinColumns = {@JoinColumn(name = "blocked_by_id")}, inverseJoinColumns = {@JoinColumn(name = "block_user_id")}) */
    @JsonIgnore
    private Set<User> blockTarget;

    @JsonIgnore
    @ManyToMany(mappedBy = "blockTarget", fetch = FetchType.LAZY) // TODO: cascade
    private Set<User> blockBy;

    @ManyToMany() // TODO: cascade
    /*    @JoinTable(joinColumns = {@JoinColumn(name = "blocked_by_id")}, inverseJoinColumns = {@JoinColumn(name = "block_user_id")}) */
    @JsonIgnore
    private Set<User> following;


    @JsonIgnore
    @ManyToMany(mappedBy = "target") // TODO: cascade
    private List<Notification> notification;

    @JsonIgnore
    @ManyToMany(mappedBy = "following", fetch = FetchType.LAZY) // TODO: cascade
    private Set<User> follower;

    @JsonIgnore
    @ManyToMany()
    private Set<Article> liking;

    @JsonIgnore
    private String email;

    private String profile;

    private String bio = "";

    public User() {
        this.nickname = "杜甫";
    }

    @JsonIgnore
    public List<Article> getArticles() {
        return articles;
    }

    @JsonIgnore
    public List<Comment> getComments() { return comments; }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) { this.bio = bio; }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) { this.profile = profile; }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public Set<User> getBlockTarget() {
        return blockTarget;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public Set<User> getFollower() {
        return follower;
    }

    public Set<Article> getLiking() {
        return liking;
    }

    public Set<User> getBlockBy() {
        return blockBy;
    }

    public List<Notification> getNotification() { return notification; }

    public int getFollower_num() { return follower.size(); }

    public int getFollowing_num() { return following.size(); }
}
