package com.example.sixths.service;

import com.example.sixths.model.Article;
import com.example.sixths.model.Comment;
import com.example.sixths.model.Notification;
import com.example.sixths.model.User;
import com.example.sixths.repository.ArticleRepository;
import com.example.sixths.repository.CommentRepository;
import com.example.sixths.repository.NotificationRepository;
import com.example.sixths.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private CommentRepository commentRepository;

    public int addArticle(int userid, String content, String position, String title, String image, String video, String audio, boolean draft) {
        Article article = new Article();
        article.setContent(content);
        article.setPosition(position);
        article.setTitle(title);
        article.setImage(image);
        article.setVideo(video);
        article.setAudio(audio);
        article.setDraft(draft);
        Date time = new Date();
        article.setAuthor(userRepository.getById(userid));
        article.setTime(time);
        articleRepository.save(article);
        if (!draft) {
            makeNoti("new", userid, article.getId(), time);
        }
        return article.getId();
    }


    public int modifyArticle(int article_id, String content, String position, String title, String image, String video, String audio, boolean draft) {
        Article article = findById(article_id);
        if (article == null) return -1;
        article.setContent(content);
        article.setPosition(position);
        article.setTitle(title);
        article.setImage(image);
        article.setVideo(video);
        article.setAudio(audio);
        Date time = new Date();
        if (draft && !article.draft) {
            makeNoti("new", article.getAuthor().getId(), article.getId(), time);
        }
        article.setDraft(draft);
        article.setTime(time);
        articleRepository.save(article);
        return article.getId();
    }

    public Article findById(int id) {
        Optional<Article> article = articleRepository.findById(id); // use .get() in case of lazy fetch
        return article.orElse(null);
    }


    public Pattern getPattern(String pattern) {
        if (pattern == null) return null;
        String match = "\\E[\\s\\S]*\\Q";
        String _pattern = pattern.replace(" ", "");
        _pattern = _pattern.replace("", match);
        int length = _pattern.length();
        _pattern = _pattern.substring(2, 2 + (length - 4));
        System.out.println("pattern here");
        System.out.println("pattern:" + _pattern);
        return Pattern.compile(_pattern);
    }

    public boolean fuzzyMatch(String text, Pattern pattern) {
        if (pattern == null) return false;
        return pattern.matcher(text).find();
    }

    public List<Article> getArticleList(int userid, int start, int num, List<Integer> targets,
                                        boolean enable_target, boolean enable_block, boolean draft,
                                        boolean search_title, boolean search_content, boolean search_user,
                                        boolean filter_text, boolean filter_image, boolean filter_audio,
                                        boolean filter_video, String text) {


        User user = userRepository.findById(userid).orElse(null);
        if (user == null) return new ArrayList<Article>();

        Set<User> blockers = user.getBlockTarget();

        List<Article> all_list = articleRepository.findAllByOrderByTimeDesc(); // TODO: other order

        System.out.println(all_list.size());

        Pattern pattern = getPattern(text);

        System.out.println("pattern got");

        List<Article> ret_list = all_list.stream()
                .filter(
                        t -> (
                                (!enable_target || targets.contains(t.getAuthor().getId())) // only show targets
                                        && (!enable_block || !blockers.contains(t.getAuthor())) // filter those blocker
                                        && (t.getDraft() == draft) // not showing draft
                                        && (!search_title || fuzzyMatch(t.title, pattern)) //e fuzzy match
                                        && (!search_content || fuzzyMatch(t.content, pattern)) // fuzzy match
                                        && (!search_user || fuzzyMatch(t.getAuthor().getNickname(), pattern)) // fuzzy match
                                        && (!filter_text || (t.content != null && !t.content.isEmpty())) // type filter
                                        && (!filter_audio || t.audio != null) // type filter
                                        && (!filter_video || t.video != null) // type filter
                                        && (!filter_image || t.image != null) // type filter
                        )
                ).collect(Collectors.toList());


        System.out.println(ret_list.size());

        int real_num = Math.min(ret_list.size() - start, num);
        if (real_num < 0) return new ArrayList<Article>();
        return ret_list.subList(start, start + real_num);
    }

    public String likeArticle(int user_id, int article_id) {
        System.out.println(user_id);
        System.out.println(article_id);
        User user = userRepository.findById(user_id).orElse(null);
        if (user == null) return "invalid user id";
        Article article = articleRepository.findById(article_id).orElse(null);
        if (article == null) return "invalid article id";
        user.getLiking().add(article);
        userRepository.save(user);
        makeNoti("like", user_id, article_id, new Date());
        return "success";

    }

    public String unlikeArticle(int user_id, int article_id) {
        User user = userRepository.findById(user_id).orElse(null);
        if (user == null) return "invalid user id";
        Article article = articleRepository.findById(article_id).orElse(null);
        if (article == null) return "invalid article id";
        user.getLiking().remove(article);
        userRepository.save(user);
        return "success";

    }

    public List<Integer> getLiking(int userid) {
        User user = userRepository.findById(userid).orElse(null);
        if (user == null) return null;
        Set<Article> articles = user.getLiking();
        List<Integer> ret = new ArrayList<>();
        for (Article _article : articles) {
            ret.add(_article.getId());
        }
        return ret;
    }

    public List<Comment> getComments(int article_id) {
        Article article = findById(article_id);
        if (article == null) return null;
        return article.getCommentList();
    }

    public String addComment(int user_id, int article_id, String content) {
        User user = userRepository.findById(user_id).orElse(null);
        if (user == null) return "invalid user";
        Article article = findById(article_id);
        if (article == null) return "invalid author";

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setArticle(article);
        comment.setAuthor(user);
        Date time = new Date();
        comment.setTime(time);
        commentRepository.save(comment);

        makeNoti("comment", user_id, article_id, time);
        return "success";
    }


    public String removeComment(int userid, int comment_id) {
        Comment comment = commentRepository.findById(comment_id).orElse(null);
        if (comment == null) return "invalid comment";
        if (comment.getAuthor().getId() != userid) return "not your comment";
        commentRepository.delete(comment);
        return "success";
    }

    public void makeNoti(String type, int user_id, int id, Date time) {
        // makeNoti("new", article.getAuthor().getId(), article.getId(), time );
        User user = userRepository.findById(user_id).orElse(null);
        if (user == null) return;

        if (type.equals("new")) {
            Set<User> u = user.getFollower();
            u.remove(user);
            if (u.isEmpty()) return;

            Notification noti = new Notification();
            noti.setArticle_id(id);
            noti.setContent("您关注的" + user.getNickname() + "(@" + user.getName() + ")" + "更新了新的动态");
            noti.setType(type);
            noti.setTime(time);
            noti.setTarget(u);
            notificationRepository.save(noti);

        } else if (type.equals("comment")) {

            Article article = findById(id);
            if (article.getAuthor().getId() == user_id) return;

            Notification noti = new Notification();
            noti.setType(type);
            noti.setTime(time);

            noti.setArticle_id(id);
            noti.setContent(user.getNickname() + "(@" + user.getName() + ")" + "评论了您的动态");

            HashSet<User> u = new HashSet<>();
            u.add(article.getAuthor());
            noti.setTarget(u);
            System.out.println("size:" + u.size());
            notificationRepository.save(noti);
        } else if (type.equals("like")) {

            Article article = findById(id);
            if (article.getAuthor().getId() == user_id) return;

            Notification noti = new Notification();
            noti.setType(type);
            noti.setTime(time);

            noti.setArticle_id(id);
            noti.setContent(user.getNickname() + "(@" + user.getName() + ")" + "点赞了您的动态");

            HashSet<User> u = new HashSet<>();
            u.add(article.getAuthor());
            noti.setTarget(u);

            notificationRepository.save(noti);
        }
    }

}
