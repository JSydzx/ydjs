package com.zjgsu.teamplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 团队聊天已读状态实体。
 */
@Data
public class TeamChatRead {
    private Long id;
    private Long teamId;
    private Long userId;
    private LocalDateTime lastReadAt;
    private LocalDateTime updatedAt;
}
