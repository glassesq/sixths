package com.example.sixths.service;

public class Comment {

    public int id;
    public int article_id;

    public int author_id = 1;
    public String author_nickname = "nickname";
    public String author_username = "username";

    public boolean profile_fetched = false;
    public String author_profile = null;

    public String content = "content";
    public String time = "time";
}
