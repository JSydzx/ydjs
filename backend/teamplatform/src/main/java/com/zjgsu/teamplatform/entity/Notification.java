package com.zjgsu.teamplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知实体。
 */
@Data
public class Notification {
    private Long id;
    private Long userId;
    private String message;
    private String type;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
