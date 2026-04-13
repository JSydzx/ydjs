package com.zjgsu.teamplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 团队成员实体。
 */
@Data
public class TeamMember {
    private Long id;
    private Long teamId;
    private Long userId;
    private String role;
    private LocalDateTime joinedAt;
}
