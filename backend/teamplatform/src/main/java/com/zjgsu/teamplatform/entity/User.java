package com.zjgsu.teamplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体。
 */
@Data
public class User {
    private Long id;
    private String username;
    private String nickname;
    private String password;
    private String email;
    private String avatar;
    private String major;
    private String grade;
    private String skills;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
