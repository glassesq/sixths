package com.example.sixths.controller;

import com.example.sixths.interceptor.LoginInterceptor;
import com.example.sixths.model.User;
import com.example.sixths.service.UserService;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(path = "/user") // after application path
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/register")
    public ResponseEntity<String> registerUser(@RequestParam String name, @RequestParam String email) {
        Pair<String, String> ret = userService.addUser(name, email);
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

    // TODO: annotation
    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestParam String name) {
        Pair<String, String> ret = userService.login(name);
        if (ret.getValue0().equals("success")) {
            return ResponseEntity.ok().body(ret.getValue1());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ret.getValue1());
    }

    @PostMapping(path = "/signed_greeting")
    public @ResponseBody String signed_greeting(HttpServletRequest req) {
        String name = req.getAttribute(LoginInterceptor.NAME_KEY).toString();
        int id = Integer.parseInt(req.getAttribute(LoginInterceptor.ID_KEY).toString());
        User user = userService.getUserById(id);
        if( user != null ) {
            String email = user.getEmail();
            return String.format("hi %s! your email is %s .\n", name, email);
        }
        return "wrong!"; // TODO
    }
}
