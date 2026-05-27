package com.zjgsu.teamplatform.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 团队聊天会话视图对象。
 */
@Data
public class TeamChatConversationVO {
    private Long teamId;
    private String teamName;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private Integer unreadCount;
}
