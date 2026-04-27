package com.zjgsu.teamplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 加入请求实体。
 */
@Data
public class JoinRequest {
    private Long id;
    private Long teamId;
    private Long userId;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String reason;
}
