package com.example.sixths.repository;

import com.example.sixths.model.Article;
import com.example.sixths.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    public List<Comment> findAllByOrderByTimeDesc(); // Asc
}
