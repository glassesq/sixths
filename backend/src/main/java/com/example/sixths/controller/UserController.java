package com.example.sixths.controller;

import com.example.sixths.interceptor.LoginInterceptor;
import com.example.sixths.model.Notification;
import com.example.sixths.model.User;
import com.example.sixths.service.UserService;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(path = "/user") // after application path
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/register")
    public ResponseEntity<String> registerUser(@RequestParam String name, @RequestParam String email, @RequestParam String password) {
        Pair<String, String> ret = userService.addUser(name, email, password);
        if (ret.getValue0().equals("success")) {
            return ResponseEntity.ok().body(ret.getValue1());
        }
        return ResponseEntity.badRequest().body(ret.getValue1());
    }

    @GetMapping(path = "/get_all")
    public @ResponseBody
    Iterable<User> getAllUser() {
        return userService.getAllUser();
    }

    // TODO: to remove
    @PostMapping(path = "/test_login")
    public ResponseEntity<String> test_login(@RequestParam String name) {
        Pair<String, String> ret = userService.test_login(name);
        if (ret.getValue0().equals("success")) {
            return ResponseEntity.ok().body(ret.getValue1());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ret.getValue1());
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        Pair<String, String> ret = userService.login(email, password);
        if (ret.getValue0().equals("success")) {
            return ResponseEntity.ok().body(ret.getValue1());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ret.getValue1());
    }

    @PostMapping(path = "/signed_greeting")
    public @ResponseBody
    String signed_greeting(HttpServletRequest req) {
        String name = req.getAttribute(LoginInterceptor.NAME_KEY).toString();
        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        User user = userService.getUserById(id);
        if (user != null) {
            String email = user.getEmail();
            return String.format("hi %s! your email is %s .\n", name, email);
        }
        return "wrong!"; // TODO
    }


    @PostMapping(path = "/block")
    public ResponseEntity<String> blockUser(HttpServletRequest req) {
        int block_id = Integer.parseInt(req.getParameter("block_id"));
        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        String ret = userService.blockUser(id, block_id);
        if (ret.equals("success"))
            return ResponseEntity.ok().body(ret);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ret);
    }


    @PostMapping(path = "/unblock")
    public ResponseEntity<String> unblockUser(HttpServletRequest req) {
        int block_id = Integer.parseInt(req.getParameter("block_id"));
        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        String ret = userService.unblockUser(id, block_id);
        if (ret.equals("success"))
            return ResponseEntity.ok().body(ret);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ret);
    }


    @PostMapping(path = "/set_info")
    public ResponseEntity<String> set_info(HttpServletRequest req) {
        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        String nickname = req.getParameter("nickname");
        String password = req.getParameter("password");
        String bio = req.getParameter("bio");
        String profile = req.getParameter("profile");
        String ret = userService.setInfo(id, nickname, password, bio, profile);
        if (ret.equals("success")) {
            return ResponseEntity.ok().body(ret);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ret);
    }


    @GetMapping(path = "/get_info")
    public ResponseEntity<User> getUserInfo(HttpServletRequest req) {
//        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        int id = Integer.parseInt(req.getParameter("userid"));
        User ret = userService.getUserById(id);
        if (ret != null) {
            return ResponseEntity.ok().body(ret);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    @GetMapping(path = "/get_myself")
    public ResponseEntity<User> getMyselfInfo(HttpServletRequest req) {
        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        User ret = userService.getUserById(id);
        if (ret != null) {
            return ResponseEntity.ok().body(ret);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    @PostMapping(path = "/follow")
    public ResponseEntity<String> followUser(HttpServletRequest req) {
        int follow_id = Integer.parseInt(req.getParameter("follow_id"));
        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        String ret = userService.followUser(id, follow_id);
        if (ret.equals("success"))
            return ResponseEntity.ok().body(ret);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ret);
    }


    @PostMapping(path = "/unfollow")
    public ResponseEntity<String> unfollowUser(HttpServletRequest req) {
        int follow_id = Integer.parseInt(req.getParameter("follow_id"));
        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        String ret = userService.unfollowUser(id, follow_id);
        if (ret.equals("success"))
            return ResponseEntity.ok().body(ret);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ret);
    }


    @GetMapping(path = "/get_following")
    public ResponseEntity<List<Integer>> getFollowing(HttpServletRequest req) {
//        int follow_id = Integer.parseInt(req.getParameter("follow_id"));
        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        List<Integer> ret = userService.getFollowing(id);
        if (ret != null)
            return ResponseEntity.ok().body(ret);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ret);
    }


    @GetMapping(path = "/get_notification")
    public ResponseEntity<List<Notification>> getNotification(HttpServletRequest req) {
        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        List<Notification> ret = userService.getNotificationList(id);
        if (ret != null)
            return ResponseEntity.ok().body(ret);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }


    @PostMapping(path = "/set_notification")
    public ResponseEntity<String> setNotification(HttpServletRequest req) {
        int noti_id = Integer.parseInt(req.getParameter("noti_id"));
        userService.setNotification(noti_id);
        return ResponseEntity.ok().body("success");
    }
}
