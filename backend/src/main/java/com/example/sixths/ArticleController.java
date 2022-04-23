package com.example.sixths;


import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.ref.Reference;
import java.util.Optional;

@Controller
@RequestMapping(path = "/article")
public class ArticleController {
    @Autowired
    private ArticleRepository articleRepository;

    @PostMapping(path = "")
    public ResponseEntity<String> addArticle(@RequestParam String content) {
        Article article = new Article();
        article.setContent(content);
        articleRepository.save(article);
        return ResponseEntity.ok().body(article.getArticleid());
    }

    @GetMapping(path = "")
    public ResponseEntity<Article> getArticle(@RequestParam String articleid) throws Exception {
        int real_id = Article.decryptId(articleid);
        Optional<Article> article = articleRepository.findById(real_id);
        if(article.isPresent()) {
            return ResponseEntity.ok().body(article.get());// DO NOT use getById
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

    }

}
