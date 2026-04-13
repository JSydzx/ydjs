package com.zjgsu.teamplatform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 添加团队成员请求。
 */
@Data
public class TeamMemberAddRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
}
