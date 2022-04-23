package com.example.sixths.controller;


import com.example.sixths.model.Article;
import com.example.sixths.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(path = "/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping(path = "")
    public ResponseEntity<String> addArticle(@RequestParam String content) {
        String ret = articleService.addArticle(content);
        return ResponseEntity.ok().body(ret);
    }

    @GetMapping(path = "")
    public ResponseEntity<Article> getArticle(@RequestParam String articleid) throws Exception {
        Article article = articleService.findByArticleid(articleid);
        if(article != null) {
            return ResponseEntity.ok().body(article);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

    }

}
