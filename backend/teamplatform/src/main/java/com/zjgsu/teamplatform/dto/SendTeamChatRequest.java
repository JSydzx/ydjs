package com.zjgsu.teamplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发送团队消息请求。
 */
@Data
public class SendTeamChatRequest {
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容不能超过2000")
    private String content;
}
