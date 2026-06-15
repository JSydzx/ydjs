package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.dto.InvitationSendRequest;
import com.zjgsu.teamplatform.entity.Invitation;
import com.zjgsu.teamplatform.entity.Team;
import com.zjgsu.teamplatform.entity.TeamMember;
import com.zjgsu.teamplatform.entity.User;
import com.zjgsu.teamplatform.exception.BizException;
import com.zjgsu.teamplatform.mapper.InvitationMapper;
import com.zjgsu.teamplatform.mapper.TeamMapper;
import com.zjgsu.teamplatform.mapper.TeamMemberMapper;
import com.zjgsu.teamplatform.mapper.UserMapper;
import com.zjgsu.teamplatform.service.NotificationService;
import com.zjgsu.teamplatform.vo.InvitationVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 邀请服务单元测试，覆盖发送邀请、查询邀请、接受和拒绝。
 */
class InvitationServiceImplTest {

    private InvitationMapper invitationMapper;
    private TeamMapper teamMapper;
    private TeamMemberMapper teamMemberMapper;
    private UserMapper userMapper;
    private NotificationService notificationService;
    private InvitationServiceImpl invitationService;

    /**
     * 初始化邀请服务及其依赖。
     */
    @BeforeEach
    void setUp() {
        invitationMapper = mock(InvitationMapper.class);
        teamMapper = mock(TeamMapper.class);
        teamMemberMapper = mock(TeamMemberMapper.class);
        userMapper = mock(UserMapper.class);
        notificationService = mock(NotificationService.class);
        invitationService = new InvitationServiceImpl(invitationMapper, teamMapper, teamMemberMapper, userMapper, notificationService);
    }

    /**
     * 团队管理员发送邀请时会校验重复邀请和成员关系，并通知被邀请人。
     */
    @Test
    void send_whenOperatorIsAdmin_insertsInvitationAndNotifiesUser() {
        InvitationSendRequest request = new InvitationSendRequest();
        request.setTeamId(10L);
        request.setUserId(2L);
        when(teamMapper.findById(10L)).thenReturn(buildTeam(10L, 1L));
        when(teamMemberMapper.findByTeamAndUser(10L, 3L)).thenReturn(buildMember(10L, 3L, Constants.MEMBER_ROLE_ADMIN));
        when(teamMemberMapper.findByTeamAndUser(10L, 2L)).thenReturn(null);
        when(invitationMapper.findPendingByTeamAndUser(10L, 2L)).thenReturn(null);

        invitationService.send(3L, request);

        verify(invitationMapper).insert(any(Invitation.class));
        verify(notificationService).push(eq(2L), eq(Constants.NOTIFICATION_TYPE_SYSTEM), anyString());
    }

    /**
     * 团队内已有成员不能再被邀请。
     */
    @Test
    void send_whenUserAlreadyMember_throwsBizException() {
        InvitationSendRequest request = new InvitationSendRequest();
        request.setTeamId(10L);
        request.setUserId(2L);
        when(teamMapper.findById(10L)).thenReturn(buildTeam(10L, 1L));
        when(teamMemberMapper.findByTeamAndUser(10L, 1L)).thenReturn(buildMember(10L, 1L, Constants.MEMBER_ROLE_CREATOR));
        when(teamMemberMapper.findByTeamAndUser(10L, 2L)).thenReturn(buildMember(10L, 2L, Constants.MEMBER_ROLE_MEMBER));

        assertThatThrownBy(() -> invitationService.send(1L, request))
            .isInstanceOf(BizException.class);
        verify(invitationMapper, never()).insert(any(Invitation.class));
    }

    /**
     * 查询我的邀请时会补全团队名和邀请人昵称。
     */
    @Test
    void myInvitations_whenInvitationsExist_returnsVOList() {
        when(invitationMapper.findByUserId(2L)).thenReturn(List.of(
            buildInvitation(100L, 1L, 10L, 2L, Constants.INVITATION_STATUS_PENDING)
        ));
        when(teamMapper.findById(10L)).thenReturn(buildTeam(10L, 1L));
        when(userMapper.findById(1L)).thenReturn(buildUser(1L, "Creator"));

        List<InvitationVO> invitations = invitationService.myInvitations(2L);

        assertThat(invitations).hasSize(1);
        assertThat(invitations.get(0).getTeamName()).isEqualTo("Team10");
        assertThat(invitations.get(0).getInviterName()).isEqualTo("Creator");
    }

    /**
     * 接受邀请时会添加成员、更新邀请状态、同步团队人数并通知邀请人。
     */
    @Test
    void accept_whenInvitationPending_addsMemberAndUpdatesTeamCount() {
        Invitation invitation = buildInvitation(100L, 1L, 10L, 2L, Constants.INVITATION_STATUS_PENDING);
        when(invitationMapper.findById(100L)).thenReturn(invitation);
        when(teamMapper.findById(10L)).thenReturn(buildTeam(10L, 1L));
        when(teamMemberMapper.findByTeamAndUser(10L, 2L)).thenReturn(null);
        when(teamMemberMapper.countByTeamId(10L)).thenReturn(2);

        invitationService.accept(2L, 100L);

        verify(teamMemberMapper).insert(any(TeamMember.class));
        verify(invitationMapper).updateStatus(100L, Constants.INVITATION_STATUS_ACCEPTED);
        verify(teamMapper).updateCurrentMembers(10L, 3);
        verify(notificationService).push(eq(1L), eq(Constants.NOTIFICATION_TYPE_SYSTEM), anyString());
    }

    /**
     * 非邀请接收人不能处理邀请。
     */
    @Test
    void accept_whenUserIsNotRecipient_throwsBizException() {
        when(invitationMapper.findById(100L))
            .thenReturn(buildInvitation(100L, 1L, 10L, 2L, Constants.INVITATION_STATUS_PENDING));

        assertThatThrownBy(() -> invitationService.accept(3L, 100L))
            .isInstanceOf(BizException.class);
        verify(teamMemberMapper, never()).insert(any(TeamMember.class));
    }

    /**
     * 拒绝邀请时只更新邀请状态并通知邀请人。
     */
    @Test
    void reject_whenInvitationPending_updatesStatusAndNotifiesInviter() {
        when(invitationMapper.findById(100L))
            .thenReturn(buildInvitation(100L, 1L, 10L, 2L, Constants.INVITATION_STATUS_PENDING));

        invitationService.reject(2L, 100L);

        verify(invitationMapper).updateStatus(100L, Constants.INVITATION_STATUS_REJECTED);
        verify(notificationService).push(eq(1L), eq(Constants.NOTIFICATION_TYPE_SYSTEM), anyString());
    }

    /**
     * 构造团队实体。
     */
    private Team buildTeam(Long teamId, Long creatorId) {
        Team team = new Team();
        team.setId(teamId);
        team.setName("Team" + teamId);
        team.setCreatorId(creatorId);
        team.setCurrentMembers(2);
        team.setMaxMembers(5);
        return team;
    }

    /**
     * 构造团队成员实体。
     */
    private TeamMember buildMember(Long teamId, Long userId, String role) {
        TeamMember member = new TeamMember();
        member.setTeamId(teamId);
        member.setUserId(userId);
        member.setRole(role);
        return member;
    }

    /**
     * 构造邀请实体。
     */
    private Invitation buildInvitation(Long invitationId, Long inviteId, Long teamId, Long userId, String status) {
        Invitation invitation = new Invitation();
        invitation.setId(invitationId);
        invitation.setInviteId(inviteId);
        invitation.setTeamId(teamId);
        invitation.setUserId(userId);
        invitation.setStatus(status);
        invitation.setCreatedAt(LocalDateTime.now());
        return invitation;
    }

    /**
     * 构造用户实体。
     */
    private User buildUser(Long userId, String nickname) {
        User user = new User();
        user.setId(userId);
        user.setUsername("user" + userId);
        user.setNickname(nickname);
        return user;
    }
}
