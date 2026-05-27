package com.zjgsu.teamplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 私聊会话实体。
 */
@Data
public class ChatConversation {
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private Long lastMessageId;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
