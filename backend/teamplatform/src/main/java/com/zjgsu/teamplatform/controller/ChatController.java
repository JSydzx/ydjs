package com.zjgsu.teamplatform.controller;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.common.Result;
import com.zjgsu.teamplatform.dto.SendChatRequest;
import com.zjgsu.teamplatform.service.ChatService;
import com.zjgsu.teamplatform.vo.ChatConversationVO;
import com.zjgsu.teamplatform.vo.ChatMessageVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 私聊接口。
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 查询私聊会话列表。
     */
    @GetMapping("/list")
    public Result<List<ChatConversationVO>> list(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.success(chatService.listConversations(userId));
    }

    /**
     * 查询与指定用户的聊天记录。
     */
    @GetMapping("/conversation/{otherUserId}")
    public Result<List<ChatMessageVO>> messages(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                                @PathVariable("otherUserId") Long otherUserId,
                                                @RequestParam(value = "page", required = false) Integer page,
                                                @RequestParam(value = "size", required = false) Integer size) {
        return Result.success(chatService.listMessages(userId, otherUserId, page, size));
    }

    /**
     * 发送私聊消息。
     */
    @PostMapping("/send")
    public Result<ChatMessageVO> send(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                      @Valid @RequestBody SendChatRequest request) {
        return Result.success(chatService.send(userId, request));
    }

    /**
     * 标记与指定用户的消息已读。
     */
    @PostMapping("/conversation/{otherUserId}/read")
    public Result<Void> markRead(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                 @PathVariable("otherUserId") Long otherUserId) {
        chatService.markRead(userId, otherUserId);
        return Result.success(null);
    }
}
