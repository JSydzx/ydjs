package com.zjgsu.teamplatform.controller;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.common.Result;
import com.zjgsu.teamplatform.dto.SendTeamChatRequest;
import com.zjgsu.teamplatform.service.TeamChatService;
import com.zjgsu.teamplatform.vo.TeamChatConversationVO;
import com.zjgsu.teamplatform.vo.TeamChatMessageVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 团队聊天接口。
 */
@RestController
@RequestMapping("/api/team/chat")
@RequiredArgsConstructor
public class TeamChatController {

    private final TeamChatService teamChatService;

    /**
     * 查询团队聊天列表。
     */
    @GetMapping("/list")
    public Result<List<TeamChatConversationVO>> list(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId) {
        return Result.success(teamChatService.listTeamChats(userId));
    }

    /**
     * 查询团队聊天记录。
     */
    @GetMapping("/{teamId}/messages")
    public Result<List<TeamChatMessageVO>> messages(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                                                    @PathVariable("teamId") Long teamId,
                                                    @RequestParam(value = "page", required = false) Integer page,
                                                    @RequestParam(value = "size", required = false) Integer size) {
        return Result.success(teamChatService.listMessages(userId, teamId, page, size));
    }

    /**
     * 发送团队消息。
     */
    @PostMapping("/{teamId}/send")
    public Result<TeamChatMessageVO> send(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                                          @PathVariable("teamId") Long teamId,
                                          @Valid @RequestBody SendTeamChatRequest request) {
        return Result.success(teamChatService.send(userId, teamId, request));
    }

    /**
     * 标记团队消息已读。
     */
    @PostMapping("/{teamId}/read")
    public Result<Void> markRead(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                                 @PathVariable("teamId") Long teamId) {
        teamChatService.markRead(userId, teamId);
        return Result.success(null);
    }
}
