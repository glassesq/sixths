package com.example.sixths.repository;

import com.example.sixths.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    public List<Notification> findAllByOrderByTimeDesc(); // Asc

}
