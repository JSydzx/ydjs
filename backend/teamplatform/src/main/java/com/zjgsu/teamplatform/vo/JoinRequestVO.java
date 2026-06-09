package com.zjgsu.teamplatform.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 入队申请视图对象，补充验收演示需要的队伍和申请人展示信息。
 */
@Data
public class JoinRequestVO {
    private Long id;
    private Long teamId;
    private String teamName;
    private Long userId;
    private String applicantName;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String reason;
}
