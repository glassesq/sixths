package com.example.sixths.service;

import java.util.ArrayList;

public class Article {

    public int id;

    public int author_id = 1;
    public String author_nickname = "nickname";
    public String author_username = "username";

    public boolean profile_fetched = false;
    public String author_profile = null;

    public boolean image_fetched = false;
    public String image = null;

    public boolean video_fetched = false;
    public String video = null;

    public boolean audio_fetched = false;
    public String audio = null;

    public String content = "content";
    public String time = "time";
    public String position = null;
    public String title = "title";
    public int likes = 0;
    public int comments = 0;
}
