package com.example.sixths.repository;

import com.example.sixths.model.Article;
import com.example.sixths.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    public List<Article> findAllByOrderByTimeDesc(); // Asc

}
