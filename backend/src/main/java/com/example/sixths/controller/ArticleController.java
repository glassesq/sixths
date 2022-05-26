package com.example.sixths.controller;


import com.example.sixths.interceptor.LoginInterceptor;
import com.example.sixths.model.Article;
import com.example.sixths.model.Comment;
import com.example.sixths.model.User;
import com.example.sixths.service.ArticleService;
import com.example.sixths.service.UserService;
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

    @Autowired
    private UserService userService;

    @PostMapping(path = "")
    public ResponseEntity<String> addArticle(HttpServletRequest req) {
        try {
            int userid = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
            String content = req.getParameter("content");
            String position = req.getParameter("position");
            String title = req.getParameter("title");
            String image = req.getParameter("image");
            String video = req.getParameter("video");
            String audio = req.getParameter("audio");
            int ret;
            if (req.getParameter("article_id") == null) {
                ret = articleService.addArticle(userid, content, position, title, image, video, audio, false);
            } else {
                int article_id = Integer.parseInt(req.getParameter("article_id"));
                ret = articleService.modifyArticle(article_id, content, position, title, image, video, audio, false);
            }
            return ResponseEntity.ok().body(String.valueOf(ret));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("fail");
        }
    }

    @PostMapping(path = "/draft")
    public ResponseEntity<String> addDraft(HttpServletRequest req) {
        try {
            int userid = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
            String content = req.getParameter("content");
            String position = req.getParameter("position");
            String title = req.getParameter("title");
            String image = req.getParameter("image");
            String video = req.getParameter("video");
            String audio = req.getParameter("audio");
            int ret;
            if (req.getParameter("article_id") == null) {
                ret = articleService.addArticle(userid, content, position, title, image, video, audio, true);
            } else {
                int article_id = Integer.parseInt(req.getParameter("article_id"));
                ret = articleService.modifyArticle(article_id, content, position, title, image, video, audio, true);
            }
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

    public boolean getBool(String str) {
        if (str != null && str.equals("true")) return true;
        return false;
    }

    /* 根据用户,偏好和分页，返回一个列表 */
    @GetMapping(path = "/get_list")
    public @ResponseBody
    List<Article> getArticleList(HttpServletRequest req) {
        try {
            int userid = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
            int start = Integer.parseInt(req.getParameter("start"));
            int num = Integer.parseInt(req.getParameter("num"));

            if (req.getParameter("userid") != null) {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(Integer.parseInt(req.getParameter("userid")));
                return articleService.getArticleList(userid, start, num,
                        list, true, false, false,
                        false, false, false,
                        false, false, false, false, null);
            }
            if (req.getParameter("follow") != null) {
                List<Integer> list = userService.getFollowing(userid);
                return articleService.getArticleList(userid, start, num,
                        list, true, false, false,
                        false, false, false,
                        false, false, false, false, null);
            }
            if (req.getParameter("draft") != null) {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(userid);
                return articleService.getArticleList(userid, start, num,
                        list, true, false, true,
                        false, false, false,
                        false, false, false, false, null);
            }

            boolean search_title = getBool(req.getParameter("search_title"));
            boolean search_content = getBool(req.getParameter("search_content"));
            boolean search_user = getBool(req.getParameter("search_user"));

            boolean filter_text = getBool(req.getParameter("filter_text"));
            boolean filter_image = getBool(req.getParameter("filter_image"));
            boolean filter_video = getBool(req.getParameter("filter_video"));
            boolean filter_audio = getBool(req.getParameter("filter_audio"));

            System.out.println("here");

            String text = req.getParameter("search_text");
            System.out.println("check" + search_title + search_content + search_user + filter_audio + filter_text + filter_video + filter_image);
            return articleService.getArticleList(userid, start, num,
                    null, false, true, false,
                    search_title, search_content, search_user,
                    filter_text, filter_image, filter_audio, filter_video, text);
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping(path = "/like")
    public ResponseEntity<String> likeArticle(HttpServletRequest req) {
        int article_id = Integer.parseInt(req.getParameter("article_id"));
        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        String ret = articleService.likeArticle(id, article_id);
        if (ret.equals("success"))
            return ResponseEntity.ok().body(ret);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ret);
    }

    @PostMapping(path = "/unlike")
    public ResponseEntity<String> unlikeArticle(HttpServletRequest req) {
        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        int article_id = Integer.parseInt(req.getParameter("article_id"));
        String ret = articleService.unlikeArticle(id, article_id);
        if (ret.equals("success"))
            return ResponseEntity.ok().body(ret);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ret);
    }


    @GetMapping(path = "/get_liking")
    public ResponseEntity<List<Integer>> getLikingList(HttpServletRequest req) {
        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        List<Integer> ret = articleService.getLiking(id);
        if (ret != null)
            return ResponseEntity.ok().body(ret);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping(path = "/get_info")
    public ResponseEntity<Article> getUserInfo(HttpServletRequest req) {
        int id = Integer.parseInt(req.getParameter("article_id"));
        Article ret = articleService.findById(id);
        if (ret != null) {
            return ResponseEntity.ok().body(ret);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    @PostMapping(path = "/add_comment")
    public ResponseEntity<String> addComment(HttpServletRequest req) {
        int user_id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        int article_id = Integer.parseInt(req.getParameter("article_id"));
        String content = req.getParameter("content");
        String ret = articleService.addComment(user_id, article_id, content);
        if (ret != null) {
            return ResponseEntity.ok().body(ret);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    @PostMapping(path = "/remove_comment")
    public ResponseEntity<String> removeComment(HttpServletRequest req) {
        int user_id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        int article_id = Integer.parseInt(req.getParameter("comment_id"));
        String ret = articleService.removeComment(user_id, article_id);
        if (ret != null) {
            return ResponseEntity.ok().body(ret);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping(path = "/get_comments")
    public ResponseEntity<List<Comment>> getComments(HttpServletRequest req) {
        int id = Integer.parseInt(req.getParameter("article_id"));
        List<Comment> ret = articleService.getComments(id);
        if (ret != null) {
            return ResponseEntity.ok().body(ret);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

}
