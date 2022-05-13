package com.example.sixths.service;

import com.example.sixths.model.Article;
import com.example.sixths.repository.ArticleRepository;
import com.example.sixths.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    public UserRepository userRepository;

    public String addArticle(String content) {
        Article article = new Article();
        article.setContent(content);
        article.setAuthor(userRepository.getById(1));
        articleRepository.save(article);
        return article.getArticleid();
    }

    public Article findByArticleid(String articleid) {
        int real_id = Article.decryptId(articleid);
        Optional<Article> article = articleRepository.findById(real_id); // use .get() in case of lazy fetch
        return article.orElse(null);
    }


    public String getArticleList(int userid, int start, int num/* 是否为关注列表 */) {
        // 找到userid的

        return "";
//        int real_id = Article.decryptId(articleid);
//        Optional<Article> article = articleRepository.findById(real_id); // use .get() in case of lazy fetch
//        return article.orElse(null);
    }

    // TODO: getArticleListForOneUser()

}
