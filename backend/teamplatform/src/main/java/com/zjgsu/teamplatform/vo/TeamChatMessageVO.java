package com.zjgsu.teamplatform.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 团队聊天消息视图对象。
 */
@Data
public class TeamChatMessageVO {
    private Long id;
    private Long teamId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private String content;
    private LocalDateTime createdAt;
}
