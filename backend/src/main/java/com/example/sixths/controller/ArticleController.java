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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping(path = "")
    public ResponseEntity<String> addArticle(HttpServletRequest req) {
        try {
            int userid = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
            String content = req.getParameter("content");
            String position = req.getParameter("position");
            int ret = articleService.addArticle(userid, content, position);
            return ResponseEntity.ok().body(String.valueOf(ret));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("fail");
        }
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
            if( req.getParameter("userid") != null ) {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(Integer.parseInt(req.getParameter("userid")));
                return articleService.getArticleList(userid, start, num,
                        list, true, false);
            }
            return articleService.getArticleList(userid, start, num,
                    null, false, true);
        } catch (Exception e) {
            return null;
        }
    }

}
