package com.example.demo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 5780595058487857777L;

    private String id;
    private String name;

    private String telPhone;
    private String email;
    private String sex;
    private String username;
    private String realname;

}
