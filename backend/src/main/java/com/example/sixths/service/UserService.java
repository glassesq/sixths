package com.example.sixths.service;

import com.example.sixths.repository.ArticleRepository;
import com.example.sixths.utils.JWTUtils;
import com.example.sixths.model.User;
import com.example.sixths.repository.UserRepository;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    public UserRepository userRepository;

    public Pair<String, String> addUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        // TODO: check emails
        userRepository.save(user);
        String token = JWTUtils.genUserToken(user);
        return new Pair<>("success", token);
    }

    public Iterable<User> getAllUser() {
        return userRepository.findAll();
    }

    public Pair<String, String> login(String name) {
        List<User> userList = userRepository.findByName(name);
        if (userList.size() == 0) {
            return new Pair<>("fail", "not exist");
        } else if (userList.size() > 1) {
            return new Pair<>("fail", "more than one");
        }
        String token = JWTUtils.genUserToken(userList.get(0));
        return new Pair<>("success", token);
    }

    public User getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public String blockUser(int userid, int block_id) {
        User user = getUserById(userid);
        if (user == null) return "invalid userid";
        User block_user = getUserById(block_id);
        if( user.addBlock(block_user) ) return "success";
        return "fail";
    }
}
