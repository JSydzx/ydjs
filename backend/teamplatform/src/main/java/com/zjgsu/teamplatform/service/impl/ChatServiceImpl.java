package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.common.ErrorCode;
import com.zjgsu.teamplatform.dto.SendChatRequest;
import com.zjgsu.teamplatform.entity.ChatConversation;
import com.zjgsu.teamplatform.entity.ChatMessage;
import com.zjgsu.teamplatform.entity.User;
import com.zjgsu.teamplatform.exception.BizException;
import com.zjgsu.teamplatform.mapper.ChatConversationMapper;
import com.zjgsu.teamplatform.mapper.ChatMessageMapper;
import com.zjgsu.teamplatform.mapper.UserMapper;
import com.zjgsu.teamplatform.service.ChatService;
import com.zjgsu.teamplatform.vo.ChatConversationVO;
import com.zjgsu.teamplatform.vo.ChatMessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 私聊服务实现。
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatConversationMapper chatConversationMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final UserMapper userMapper;

    /**
     * 查询会话列表。
     */
    @Override
    public List<ChatConversationVO> listConversations(Long userId) {
        List<ChatConversation> conversations = chatConversationMapper.findByUserId(userId);
        List<ChatConversationVO> result = new ArrayList<>();
        for (ChatConversation conversation : conversations) {
            Long otherUserId = userId.equals(conversation.getUser1Id()) ? conversation.getUser2Id() : conversation.getUser1Id();
            User otherUser = userMapper.findById(otherUserId);
            ChatConversationVO vo = new ChatConversationVO();
            vo.setConversationId(conversation.getId());
            vo.setOtherUserId(otherUserId);
            vo.setOtherUserName(formatUserName(otherUser));
            vo.setOtherUserAvatar(otherUser == null ? null : otherUser.getAvatar());

            if (conversation.getLastMessageId() != null) {
                ChatMessage lastMessage = chatMessageMapper.findById(conversation.getLastMessageId());
                if (lastMessage != null) {
                    vo.setLastMessage(lastMessage.getContent());
                    vo.setLastMessageAt(lastMessage.getCreatedAt());
                }
            }

            Integer unread = chatMessageMapper.countUnread(otherUserId, userId);
            vo.setUnreadCount(unread == null ? 0 : unread);
            result.add(vo);
        }
        return result;
    }

    /**
     * 查询会话消息列表。
     */
    @Override
    public List<ChatMessageVO> listMessages(Long userId, Long otherUserId, Integer page, Integer size) {
        if (userId.equals(otherUserId)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "不能与自己聊天");
        }
        User otherUser = userMapper.findById(otherUserId);
        if (otherUser == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        ChatConversation conversation = findConversation(userId, otherUserId);
        if (conversation == null) {
            return new ArrayList<>();
        }
        int pageNumber = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 20 : size;
        int offset = (pageNumber - 1) * pageSize;
        List<ChatMessage> messages = chatMessageMapper.findByConversation(conversation.getId(), pageSize, offset);
        Collections.reverse(messages);
        return toMessageVOs(messages, buildUserCache(userId, otherUserId));
    }

    /**
     * 发送私聊消息。
     */
    @Override
    public ChatMessageVO send(Long userId, SendChatRequest request) {
        if (userId.equals(request.getRecipientId())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "不能给自己发送消息");
        }
        User recipient = userMapper.findById(request.getRecipientId());
        if (recipient == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        ChatConversation conversation = findConversation(userId, request.getRecipientId());
        if (conversation == null) {
            // 通过固定顺序存储 user1/user2，避免重复会话
            conversation = createConversation(userId, request.getRecipientId());
        }

        ChatMessage message = new ChatMessage();
        message.setConversationId(conversation.getId());
        message.setSenderId(userId);
        message.setRecipientId(request.getRecipientId());
        message.setContent(request.getContent());
        message.setIsRead(false);
        LocalDateTime now = LocalDateTime.now();
        message.setCreatedAt(now);
        chatMessageMapper.insert(message);

        chatConversationMapper.updateLastMessage(conversation.getId(), message.getId(), now);

        Map<Long, User> userCache = buildUserCache(userId, request.getRecipientId());
        return toMessageVO(message, userCache.get(message.getSenderId()));
    }

    /**
     * 标记与指定用户的消息已读。
     */
    @Override
    public void markRead(Long userId, Long otherUserId) {
        User otherUser = userMapper.findById(otherUserId);
        if (otherUser == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        chatMessageMapper.markReadByRecipient(otherUserId, userId);
    }

    /**
     * 查询固定顺序的会话。
     */
    private ChatConversation findConversation(Long userId, Long otherUserId) {
        Long user1 = Math.min(userId, otherUserId);
        Long user2 = Math.max(userId, otherUserId);
        return chatConversationMapper.findByUserPair(user1, user2);
    }

    /**
     * 新建固定顺序的会话。
     */
    private ChatConversation createConversation(Long userId, Long otherUserId) {
        Long user1 = Math.min(userId, otherUserId);
        Long user2 = Math.max(userId, otherUserId);
        ChatConversation conversation = new ChatConversation();
        conversation.setUser1Id(user1);
        conversation.setUser2Id(user2);
        conversation.setLastMessageAt(LocalDateTime.now());
        chatConversationMapper.insert(conversation);
        return conversation;
    }

    /**
     * 组装消息视图对象列表。
     */
    private List<ChatMessageVO> toMessageVOs(List<ChatMessage> messages, Map<Long, User> userCache) {
        List<ChatMessageVO> result = new ArrayList<>();
        for (ChatMessage message : messages) {
            result.add(toMessageVO(message, userCache.get(message.getSenderId())));
        }
        return result;
    }

    /**
     * 组装消息视图对象。
     */
    private ChatMessageVO toMessageVO(ChatMessage message, User sender) {
        ChatMessageVO vo = new ChatMessageVO();
        vo.setId(message.getId());
        vo.setConversationId(message.getConversationId());
        vo.setSenderId(message.getSenderId());
        vo.setSenderName(formatUserName(sender));
        vo.setSenderAvatar(sender == null ? null : sender.getAvatar());
        vo.setRecipientId(message.getRecipientId());
        vo.setContent(message.getContent());
        vo.setIsRead(message.getIsRead());
        vo.setCreatedAt(message.getCreatedAt());
        return vo;
    }

    /**
     * 构建常用用户缓存。
     */
    private Map<Long, User> buildUserCache(Long userId, Long otherUserId) {
        Map<Long, User> cache = new HashMap<>();
        User user = userMapper.findById(userId);
        User otherUser = userMapper.findById(otherUserId);
        if (user != null) {
            cache.put(userId, user);
        }
        if (otherUser != null) {
            cache.put(otherUserId, otherUser);
        }
        return cache;
    }

    /**
     * 格式化用户展示名。
     */
    private String formatUserName(User user) {
        if (user == null) {
            return null;
        }
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        return user.getUsername();
    }
}
