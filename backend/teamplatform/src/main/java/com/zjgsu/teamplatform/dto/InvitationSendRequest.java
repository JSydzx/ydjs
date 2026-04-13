package com.zjgsu.teamplatform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发送邀请请求。
 */
@Data
public class InvitationSendRequest {
    @NotNull(message = "团队ID不能为空")
    private Long teamId;

    @NotNull(message = "被邀请用户ID不能为空")
    private Long userId;
}
