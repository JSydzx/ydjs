package com.zjgsu.teamplatform.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 私聊消息视图对象。
 */
@Data
public class ChatMessageVO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long recipientId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
