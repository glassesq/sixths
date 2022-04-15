package com.example.sixths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

@Controller
@RequestMapping(path = "/user") // after application path
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping(path = "/register")
    public @ResponseBody
    String registerUser(@RequestParam String name, @RequestParam String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);
        return "Saved";
    }

    @GetMapping(path = "/get_all")
    public @ResponseBody Iterable<User> getAllUser() {
        return userRepository.findAll();
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestParam String name) {
        // TODO: using "name" as key only for test
        List<User> userList = userRepository.findByName(name);
        if( userList.size() == 0 ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user does not exist");
        }
        else if ( userList.size() == 1) {
            User user = userList.get(0);
            return ResponseEntity.ok().body(JWTUtils.genUserToken(user));
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("more than one user");
    }
}
