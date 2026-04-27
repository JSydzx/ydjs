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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
