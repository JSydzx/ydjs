package com.zjgsu.teamplatform.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 团队实体。
 */
@Data
public class Team {
    private Long id;
    private String name;
    private String description;
    private String tag;
    private Long creatorId;
    private Integer maxMembers;
    private Integer currentMembers;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
