package com.example.sixths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path="/user") // after application path
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping(path="/register")
    public @ResponseBody String registerUser(@RequestParam String name, @RequestParam String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);
        return "Saved";
    }

    @GetMapping(path="/get_all")
    public @ResponseBody Iterable<User> getAllUser() {
        return userRepository.findAll();
    }
}
