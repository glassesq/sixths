package com.example.sixths.service;

import android.annotation.SuppressLint;

import com.example.sixths.adapter.PostListAdapter;

import java.util.ArrayList;

public class ArticleManager {
    public int allowSize = 10;
    private ArrayList<Article> article_list = new ArrayList<Article>();
    private PostListAdapter adapter = null;
    //        public void setAdapter(CardListAdapter adapter) {
//            this.adapter = adapter;
//        }
    //        @SuppressLint("NotifyDataSetChanged")
    public void setAdapter(PostListAdapter adapter) {
        this.adapter = adapter;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void showMoreArticle(int more) {
        allowSize += more;
        if (adapter != null) {
            adapter.notifyDataSetChanged(); // TODO
//                adapter.setIfMore(allowSize < article_list.size());
        }
    }

    public Article getByIndex(int index) {
        if (index < 0 || index >= count()) return null;
        return article_list.get(index);
    }

    public int count() {
        return Math.min(allowSize, article_list.size());
    }
}
