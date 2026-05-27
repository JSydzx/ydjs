package com.zjgsu.teamplatform.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 私聊会话视图对象。
 */
@Data
public class ChatConversationVO {
    private Long conversationId;
    private Long otherUserId;
    private String otherUserName;
    private String otherUserAvatar;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Integer unreadCount;
}
