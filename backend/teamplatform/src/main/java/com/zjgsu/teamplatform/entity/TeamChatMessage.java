package com.zjgsu.teamplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 团队聊天消息实体。
 */
@Data
public class TeamChatMessage {
    private Long id;
    private Long teamId;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;
}
