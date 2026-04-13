package com.zjgsu.teamplatform.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 团队视图对象。
 */
@Data
public class TeamVO {
    private Long id;
    private String name;
    private String description;
    private String tag;
    private Long creatorId;
    private String creatorName;
    private Integer currentMembers;
    private Integer maxMembers;
    private String status;
    private LocalDateTime createdAt;
}
