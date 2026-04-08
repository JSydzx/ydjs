package com.zjgsu.teamplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邀请实体。
 */
@Data
public class Invitation {
    private Long id;
    private Long inviteId;
    private Long teamId;
    private Long userId;
    private String status;
    private LocalDateTime createdAt;
}
