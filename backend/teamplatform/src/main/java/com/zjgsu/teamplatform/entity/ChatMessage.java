package com.zjgsu.teamplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 私聊消息实体。
 */
@Data
public class ChatMessage {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
