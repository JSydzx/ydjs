package com.zjgsu.teamplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建加入请求。
 */
@Data
public class JoinRequestCreateRequest {
    @NotNull(message = "团队ID不能为空")
    private Long teamId;

    @NotBlank(message = "申请理由不能为空")
    @Size(max = 1000, message = "申请理由长度不能超过1000")
    private String reason;
}
