package com.example.sixths.service;

import com.example.sixths.model.Article;
import com.example.sixths.model.User;
import com.example.sixths.repository.ArticleRepository;
import com.example.sixths.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    public UserRepository userRepository;

    public int addArticle(int userid, String content, String position, String title, String image) {
        Article article = new Article();
        article.setContent(content);
        article.setPosition(position);
        article.setTitle(title);
        article.setImage(image);
        article.setAuthor(userRepository.getById(userid));
        article.setTime(new Date());
        articleRepository.save(article);
        return article.getId();
    }

    public Article findById(int id) {
        Optional<Article> article = articleRepository.findById(id); // use .get() in case of lazy fetch
        return article.orElse(null);
    }


    public List<Article> getArticleList(int userid, int start, int num, List<Integer> targets, boolean enable_target, boolean enable_block/* 是否为关注列表 */) {
        User user = userRepository.findById(userid).orElse(null);
        if (user == null) return new ArrayList<Article>();

        Set<User> blockers = user.getBlockTarget();

        List<Article> all_list = articleRepository.findAllByOrderByTimeDesc(); // TODO: other order

        List<Article> ret_list = all_list.stream()
                .filter(
                        t -> ((!enable_target || targets.contains(t.getAuthor().getId()) // only show targets
                                && (!enable_block || !blockers.contains(t.getAuthor())) // filter those blocker
                        ))).collect(Collectors.toList());

        int real_num = Math.min(ret_list.size() - start, num);
        if (real_num < 0) return new ArrayList<Article>();
        return ret_list.subList(start, start + real_num);
    }

    // TODO: getArticleListForOneUser()

}
