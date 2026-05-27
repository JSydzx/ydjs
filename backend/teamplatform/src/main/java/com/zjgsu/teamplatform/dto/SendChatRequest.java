package com.zjgsu.teamplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发送私聊消息请求。
 */
@Data
public class SendChatRequest {
    @NotNull(message = "接收方不能为空")
    private Long recipientId;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容不能超过2000")
    private String content;
}
