package com.example.sixths;

import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.*;
import java.util.Arrays;

/* ref: https://spring.io/guides/gs/accessing-data-mysql/ */
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private String email;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public String getUserId() {
        return DigestUtils.sha256Hex("string-user-id" + id.toString()).substring(32);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

}
