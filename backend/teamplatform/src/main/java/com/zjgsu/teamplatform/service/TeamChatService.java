package com.zjgsu.teamplatform.service;

import com.zjgsu.teamplatform.dto.SendTeamChatRequest;
import com.zjgsu.teamplatform.vo.TeamChatConversationVO;
import com.zjgsu.teamplatform.vo.TeamChatMessageVO;

import java.util.List;

/**
 * 团队聊天服务。
 */
public interface TeamChatService {

    /**
     * 查询团队聊天列表。
     */
    List<TeamChatConversationVO> listTeamChats(Long userId);

    /**
     * 查询团队聊天消息列表。
     */
    List<TeamChatMessageVO> listMessages(Long userId, Long teamId, Integer page, Integer size);

    /**
     * 发送团队消息。
     */
    TeamChatMessageVO send(Long userId, Long teamId, SendTeamChatRequest request);

    /**
     * 标记团队消息已读。
     */
    void markRead(Long userId, Long teamId);
}
