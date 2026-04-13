package com.zjgsu.teamplatform.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邀请视图对象。
 */
@Data
public class InvitationVO {
    private Long id;
    private Long teamId;
    private String teamName;
    private Long inviteId;
    private String inviterName;
    private Long userId;
    private String status;
    private LocalDateTime createdAt;
}
