package com.zjgsu.teamplatform.service;

import com.zjgsu.teamplatform.dto.SendChatRequest;
import com.zjgsu.teamplatform.vo.ChatConversationVO;
import com.zjgsu.teamplatform.vo.ChatMessageVO;

import java.util.List;

/**
 * 私聊服务。
 */
public interface ChatService {

    /**
     * 查询会话列表。
     */
    List<ChatConversationVO> listConversations(Long userId);

    /**
     * 查询会话消息列表。
     */
    List<ChatMessageVO> listMessages(Long userId, Long otherUserId, Integer page, Integer size);

    /**
     * 发送私聊消息。
     */
    ChatMessageVO send(Long userId, SendChatRequest request);

    /**
     * 标记与指定用户的消息已读。
     */
    void markRead(Long userId, Long otherUserId);
}
