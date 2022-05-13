package com.example.sixths.controller;


import com.example.sixths.interceptor.LoginInterceptor;
import com.example.sixths.model.Article;
import com.example.sixths.model.User;
import com.example.sixths.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(path = "/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping(path = "")
    public ResponseEntity<String> addArticle(@RequestParam String content) {
        int ret = articleService.addArticle(content);
        return ResponseEntity.ok().body(String.valueOf(ret));
    }

    @GetMapping(path = "")
    public ResponseEntity<Article> getArticle(@RequestParam String id) throws Exception {
        Article article = articleService.findById(Integer.parseInt(id));
        if (article != null) {
            return ResponseEntity.ok().body(article);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    /* 根据用户,偏好和分页，返回一个列表 */
    @GetMapping(path = "/get_list")
    public @ResponseBody
    List<Article> getArticleList(HttpServletRequest req) {
        try {
            int userid = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
            int start = Integer.parseInt(req.getParameter("start"));
            int num = Integer.parseInt(req.getParameter("num"));
            return articleService.getArticleList(userid, start, num,
                    null, false, true);
        } catch (Exception e) {
            return null;
        }
    }
}
