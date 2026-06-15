package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.dto.SendChatRequest;
import com.zjgsu.teamplatform.entity.ChatConversation;
import com.zjgsu.teamplatform.entity.ChatMessage;
import com.zjgsu.teamplatform.entity.User;
import com.zjgsu.teamplatform.exception.BizException;
import com.zjgsu.teamplatform.mapper.ChatConversationMapper;
import com.zjgsu.teamplatform.mapper.ChatMessageMapper;
import com.zjgsu.teamplatform.mapper.UserMapper;
import com.zjgsu.teamplatform.vo.ChatConversationVO;
import com.zjgsu.teamplatform.vo.ChatMessageVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 私聊服务单元测试，覆盖会话列表、消息分页、发送和已读标记。
 */
class ChatServiceImplTest {

    private ChatConversationMapper chatConversationMapper;
    private ChatMessageMapper chatMessageMapper;
    private UserMapper userMapper;
    private ChatServiceImpl chatService;

    /**
     * 每个用例使用独立 Mock，避免消息和会话状态互相影响。
     */
    @BeforeEach
    void setUp() {
        chatConversationMapper = mock(ChatConversationMapper.class);
        chatMessageMapper = mock(ChatMessageMapper.class);
        userMapper = mock(UserMapper.class);
        chatService = new ChatServiceImpl(chatConversationMapper, chatMessageMapper, userMapper);
    }

    /**
     * 查询会话列表时会补全对方信息、最后一条消息和未读数量。
     */
    @Test
    void listConversations_whenConversationHasLastMessage_returnsDisplayInfo() {
        ChatConversation conversation = buildConversation(10L, 1L, 2L);
        conversation.setLastMessageId(99L);
        ChatMessage lastMessage = buildMessage(99L, 10L, 2L, 1L, "hello");
        when(chatConversationMapper.findByUserId(1L)).thenReturn(List.of(conversation));
        when(userMapper.findById(2L)).thenReturn(buildUser(2L, "Bob"));
        when(chatMessageMapper.findById(99L)).thenReturn(lastMessage);
        when(chatMessageMapper.countUnread(2L, 1L)).thenReturn(3);

        List<ChatConversationVO> conversations = chatService.listConversations(1L);

        assertThat(conversations).hasSize(1);
        assertThat(conversations.get(0).getOtherUserName()).isEqualTo("Bob");
        assertThat(conversations.get(0).getLastMessage()).isEqualTo("hello");
        assertThat(conversations.get(0).getUnreadCount()).isEqualTo(3);
    }

    /**
     * 查询消息时会修正非法分页参数，并把数据库倒序结果恢复为时间正序。
     */
    @Test
    void listMessages_whenConversationExists_returnsReversedMessages() {
        ChatConversation conversation = buildConversation(10L, 1L, 2L);
        ChatMessage newer = buildMessage(102L, 10L, 2L, 1L, "new");
        ChatMessage older = buildMessage(101L, 10L, 1L, 2L, "old");
        when(userMapper.findById(2L)).thenReturn(buildUser(2L, "Bob"));
        when(userMapper.findById(1L)).thenReturn(buildUser(1L, "Alice"));
        when(chatConversationMapper.findByUserPair(1L, 2L)).thenReturn(conversation);
        when(chatMessageMapper.findByConversation(10L, 20, 0)).thenReturn(new ArrayList<>(List.of(newer, older)));

        List<ChatMessageVO> messages = chatService.listMessages(1L, 2L, 0, -1);

        assertThat(messages).extracting(ChatMessageVO::getContent).containsExactly("old", "new");
        assertThat(messages.get(0).getSenderName()).isEqualTo("Alice");
    }

    /**
     * 没有既有会话时发送消息会先创建固定用户顺序的会话，再写入消息。
     */
    @Test
    void send_whenConversationMissing_createsConversationAndMessage() {
        SendChatRequest request = new SendChatRequest();
        request.setRecipientId(5L);
        request.setContent("ping");
        when(userMapper.findById(5L)).thenReturn(buildUser(5L, "Eve"));
        when(userMapper.findById(9L)).thenReturn(buildUser(9L, "Nina"));
        when(chatConversationMapper.findByUserPair(5L, 9L)).thenReturn(null);
        when(chatConversationMapper.insert(any(ChatConversation.class))).thenAnswer(invocation -> {
            ChatConversation inserted = invocation.getArgument(0);
            inserted.setId(50L);
            return 1;
        });
        when(chatMessageMapper.insert(any(ChatMessage.class))).thenAnswer(invocation -> {
            ChatMessage inserted = invocation.getArgument(0);
            inserted.setId(500L);
            return 1;
        });

        ChatMessageVO message = chatService.send(9L, request);

        ArgumentCaptor<ChatConversation> conversationCaptor = ArgumentCaptor.forClass(ChatConversation.class);
        verify(chatConversationMapper).insert(conversationCaptor.capture());
        assertThat(conversationCaptor.getValue().getUser1Id()).isEqualTo(5L);
        assertThat(conversationCaptor.getValue().getUser2Id()).isEqualTo(9L);
        assertThat(message.getConversationId()).isEqualTo(50L);
        assertThat(message.getSenderName()).isEqualTo("Nina");
        verify(chatConversationMapper).updateLastMessage(eq(50L), eq(500L), any(LocalDateTime.class));
    }

    /**
     * 私聊不能发给自己，避免产生无意义会话和消息。
     */
    @Test
    void send_whenRecipientIsSelf_throwsBizException() {
        SendChatRequest request = new SendChatRequest();
        request.setRecipientId(1L);
        request.setContent("self");

        assertThatThrownBy(() -> chatService.send(1L, request))
            .isInstanceOf(BizException.class);
        verify(chatMessageMapper, never()).insert(any(ChatMessage.class));
    }

    /**
     * 标记已读前会确认对方用户存在，防止写入无效用户关系。
     */
    @Test
    void markRead_whenOtherUserExists_updatesUnreadMessages() {
        when(userMapper.findById(2L)).thenReturn(buildUser(2L, "Bob"));

        chatService.markRead(1L, 2L);

        verify(chatMessageMapper).markReadByRecipient(2L, 1L);
    }

    /**
     * 构造私聊会话实体。
     */
    private ChatConversation buildConversation(Long conversationId, Long user1Id, Long user2Id) {
        ChatConversation conversation = new ChatConversation();
        conversation.setId(conversationId);
        conversation.setUser1Id(user1Id);
        conversation.setUser2Id(user2Id);
        conversation.setLastMessageAt(LocalDateTime.now());
        return conversation;
    }

    /**
     * 构造私聊消息实体。
     */
    private ChatMessage buildMessage(Long messageId, Long conversationId, Long senderId, Long recipientId, String content) {
        ChatMessage message = new ChatMessage();
        message.setId(messageId);
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setContent(content);
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }

    /**
     * 构造用户实体。
     */
    private User buildUser(Long userId, String nickname) {
        User user = new User();
        user.setId(userId);
        user.setUsername("user" + userId);
        user.setNickname(nickname);
        user.setAvatar("avatar-" + userId);
        return user;
    }
}
