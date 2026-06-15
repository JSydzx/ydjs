package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.dto.SendTeamChatRequest;
import com.zjgsu.teamplatform.entity.Team;
import com.zjgsu.teamplatform.entity.TeamChatMessage;
import com.zjgsu.teamplatform.entity.TeamChatRead;
import com.zjgsu.teamplatform.entity.TeamMember;
import com.zjgsu.teamplatform.entity.User;
import com.zjgsu.teamplatform.exception.BizException;
import com.zjgsu.teamplatform.mapper.TeamChatMessageMapper;
import com.zjgsu.teamplatform.mapper.TeamChatReadMapper;
import com.zjgsu.teamplatform.mapper.TeamMapper;
import com.zjgsu.teamplatform.mapper.TeamMemberMapper;
import com.zjgsu.teamplatform.mapper.UserMapper;
import com.zjgsu.teamplatform.vo.TeamChatConversationVO;
import com.zjgsu.teamplatform.vo.TeamChatMessageVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
 * 团队聊天服务单元测试，覆盖会话列表、消息分页、发送和已读状态维护。
 */
class TeamChatServiceImplTest {

    private TeamChatMessageMapper teamChatMessageMapper;
    private TeamChatReadMapper teamChatReadMapper;
    private TeamMapper teamMapper;
    private TeamMemberMapper teamMemberMapper;
    private UserMapper userMapper;
    private TeamChatServiceImpl teamChatService;

    /**
     * 初始化团队聊天服务及其依赖。
     */
    @BeforeEach
    void setUp() {
        teamChatMessageMapper = mock(TeamChatMessageMapper.class);
        teamChatReadMapper = mock(TeamChatReadMapper.class);
        teamMapper = mock(TeamMapper.class);
        teamMemberMapper = mock(TeamMemberMapper.class);
        userMapper = mock(UserMapper.class);
        teamChatService = new TeamChatServiceImpl(teamChatMessageMapper, teamChatReadMapper, teamMapper, teamMemberMapper, userMapper);
    }

    /**
     * 查询团队聊天列表时会跳过不存在的团队，并计算每个团队的未读数。
     */
    @Test
    void listTeamChats_whenMembershipsExist_returnsReadableConversations() {
        when(teamMemberMapper.findByUserId(1L)).thenReturn(List.of(
            buildMember(10L, 1L),
            buildMember(99L, 1L)
        ));
        when(teamMapper.findById(10L)).thenReturn(buildTeam(10L, "Backend"));
        when(teamMapper.findById(99L)).thenReturn(null);
        TeamChatMessage lastMessage = buildMessage(100L, 10L, 2L, "latest");
        when(teamChatMessageMapper.findLastByTeam(10L)).thenReturn(lastMessage);
        when(teamChatReadMapper.findByTeamAndUser(10L, 1L)).thenReturn(buildRead(10L, 1L));
        when(teamChatMessageMapper.countUnreadSince(eq(10L), eq(1L), any(LocalDateTime.class))).thenReturn(4);

        List<TeamChatConversationVO> conversations = teamChatService.listTeamChats(1L);

        assertThat(conversations).hasSize(1);
        assertThat(conversations.get(0).getTeamName()).isEqualTo("Backend");
        assertThat(conversations.get(0).getLastMessage()).isEqualTo("latest");
        assertThat(conversations.get(0).getUnreadCount()).isEqualTo(4);
    }

    /**
     * 查询团队消息时会校验成员身份，修正分页参数，并转换发送人展示名。
     */
    @Test
    void listMessages_whenUserIsMember_returnsMessagesInChronologicalOrder() {
        TeamChatMessage newer = buildMessage(12L, 10L, 2L, "new");
        TeamChatMessage older = buildMessage(11L, 10L, 1L, "old");
        when(teamMemberMapper.findByTeamAndUser(10L, 1L)).thenReturn(buildMember(10L, 1L));
        when(teamChatMessageMapper.findByTeam(10L, 20, 0)).thenReturn(new ArrayList<>(List.of(newer, older)));
        when(userMapper.findById(1L)).thenReturn(buildUser(1L, "Alice"));
        when(userMapper.findById(2L)).thenReturn(buildUser(2L, "Bob"));

        List<TeamChatMessageVO> messages = teamChatService.listMessages(1L, 10L, null, 0);

        assertThat(messages).extracting(TeamChatMessageVO::getContent).containsExactly("old", "new");
        assertThat(messages.get(1).getSenderName()).isEqualTo("Bob");
    }

    /**
     * 发送团队消息时会写入消息，并为发送人新建已读记录。
     */
    @Test
    void send_whenReadRecordMissing_insertsMessageAndReadRecord() {
        SendTeamChatRequest request = new SendTeamChatRequest();
        request.setContent("hello team");
        when(teamMemberMapper.findByTeamAndUser(10L, 1L)).thenReturn(buildMember(10L, 1L));
        when(teamChatReadMapper.findByTeamAndUser(10L, 1L)).thenReturn(null);
        when(userMapper.findById(1L)).thenReturn(buildUser(1L, "Alice"));
        when(teamChatMessageMapper.insert(any(TeamChatMessage.class))).thenAnswer(invocation -> {
            TeamChatMessage inserted = invocation.getArgument(0);
            inserted.setId(88L);
            return 1;
        });

        TeamChatMessageVO message = teamChatService.send(1L, 10L, request);

        assertThat(message.getId()).isEqualTo(88L);
        assertThat(message.getSenderName()).isEqualTo("Alice");
        verify(teamChatReadMapper).insert(any(TeamChatRead.class));
    }

    /**
     * 已有已读记录时发送消息只更新最后读取时间，避免重复记录。
     */
    @Test
    void markRead_whenReadRecordExists_updatesReadTime() {
        when(teamMemberMapper.findByTeamAndUser(10L, 1L)).thenReturn(buildMember(10L, 1L));
        when(teamChatReadMapper.findByTeamAndUser(10L, 1L)).thenReturn(buildRead(10L, 1L));

        teamChatService.markRead(1L, 10L);

        verify(teamChatReadMapper).updateLastRead(eq(10L), eq(1L), any(LocalDateTime.class));
        verify(teamChatReadMapper, never()).insert(any(TeamChatRead.class));
    }

    /**
     * 非团队成员不能读取团队聊天内容。
     */
    @Test
    void listMessages_whenUserIsNotMember_throwsBizException() {
        when(teamMemberMapper.findByTeamAndUser(10L, 1L)).thenReturn(null);

        assertThatThrownBy(() -> teamChatService.listMessages(1L, 10L, 1, 10))
            .isInstanceOf(BizException.class);
        verify(teamChatMessageMapper, never()).findByTeam(eq(10L), any(), any());
    }

    /**
     * 构造团队实体。
     */
    private Team buildTeam(Long teamId, String name) {
        Team team = new Team();
        team.setId(teamId);
        team.setName(name);
        return team;
    }

    /**
     * 构造团队成员实体。
     */
    private TeamMember buildMember(Long teamId, Long userId) {
        TeamMember member = new TeamMember();
        member.setTeamId(teamId);
        member.setUserId(userId);
        member.setRole(Constants.MEMBER_ROLE_MEMBER);
        return member;
    }

    /**
     * 构造团队聊天消息实体。
     */
    private TeamChatMessage buildMessage(Long messageId, Long teamId, Long senderId, String content) {
        TeamChatMessage message = new TeamChatMessage();
        message.setId(messageId);
        message.setTeamId(teamId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }

    /**
     * 构造团队聊天已读实体。
     */
    private TeamChatRead buildRead(Long teamId, Long userId) {
        TeamChatRead read = new TeamChatRead();
        read.setTeamId(teamId);
        read.setUserId(userId);
        read.setLastReadAt(LocalDateTime.now().minusMinutes(5));
        return read;
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
