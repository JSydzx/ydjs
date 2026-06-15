package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.dto.JoinRequestCreateRequest;
import com.zjgsu.teamplatform.entity.JoinRequest;
import com.zjgsu.teamplatform.entity.Team;
import com.zjgsu.teamplatform.entity.TeamMember;
import com.zjgsu.teamplatform.entity.User;
import com.zjgsu.teamplatform.exception.BizException;
import com.zjgsu.teamplatform.mapper.JoinRequestMapper;
import com.zjgsu.teamplatform.mapper.TeamMapper;
import com.zjgsu.teamplatform.mapper.TeamMemberMapper;
import com.zjgsu.teamplatform.mapper.UserMapper;
import com.zjgsu.teamplatform.service.NotificationService;
import com.zjgsu.teamplatform.vo.JoinRequestVO;
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
 * 入队申请服务单元测试，覆盖申请创建、查询、审批和拒绝。
 */
class JoinRequestServiceImplTest {

    private JoinRequestMapper joinRequestMapper;
    private TeamMapper teamMapper;
    private TeamMemberMapper teamMemberMapper;
    private UserMapper userMapper;
    private NotificationService notificationService;
    private JoinRequestServiceImpl joinRequestService;

    /**
     * 初始化入队申请服务及其依赖。
     */
    @BeforeEach
    void setUp() {
        joinRequestMapper = mock(JoinRequestMapper.class);
        teamMapper = mock(TeamMapper.class);
        teamMemberMapper = mock(TeamMemberMapper.class);
        userMapper = mock(UserMapper.class);
        notificationService = mock(NotificationService.class);
        joinRequestService = new JoinRequestServiceImpl(joinRequestMapper, teamMapper, teamMemberMapper, userMapper, notificationService);
    }

    /**
     * 创建申请时会校验团队状态、重复申请和成员关系，并通知团队创建者。
     */
    @Test
    void create_whenTeamAcceptsApplications_insertsRequestAndNotifiesCreator() {
        JoinRequestCreateRequest request = new JoinRequestCreateRequest();
        request.setTeamId(10L);
        request.setReason("I can code");
        Team team = buildTeam(10L, 1L);
        JoinRequest savedRequest = buildJoinRequest(100L, 10L, 2L, Constants.REQUEST_STATUS_PENDING);
        when(teamMapper.findById(10L)).thenReturn(team);
        when(teamMemberMapper.findByTeamAndUser(10L, 2L)).thenReturn(null);
        when(joinRequestMapper.findByTeamAndUser(10L, 2L)).thenReturn(null);
        when(joinRequestMapper.insert(any(JoinRequest.class))).thenAnswer(invocation -> {
            JoinRequest inserted = invocation.getArgument(0);
            inserted.setId(100L);
            return 1;
        });
        when(joinRequestMapper.findById(100L)).thenReturn(savedRequest);
        when(userMapper.findById(2L)).thenReturn(buildUser(2L, "Applicant"));

        JoinRequestVO joinRequestVO = joinRequestService.create(2L, request);

        assertThat(joinRequestVO.getId()).isEqualTo(100L);
        assertThat(joinRequestVO.getTeamName()).isEqualTo("Team10");
        assertThat(joinRequestVO.getApplicantName()).isEqualTo("Applicant");
        verify(notificationService).push(eq(1L), eq(Constants.NOTIFICATION_TYPE_JOIN_REQUEST), anyString(), eq(10L));
    }

    /**
     * 已存在待处理申请时不能重复提交。
     */
    @Test
    void create_whenPendingRequestExists_throwsBizException() {
        JoinRequestCreateRequest request = new JoinRequestCreateRequest();
        request.setTeamId(10L);
        when(teamMapper.findById(10L)).thenReturn(buildTeam(10L, 1L));
        when(teamMemberMapper.findByTeamAndUser(10L, 2L)).thenReturn(null);
        when(joinRequestMapper.findByTeamAndUser(10L, 2L))
            .thenReturn(buildJoinRequest(100L, 10L, 2L, Constants.REQUEST_STATUS_PENDING));

        assertThatThrownBy(() -> joinRequestService.create(2L, request))
            .isInstanceOf(BizException.class);
        verify(joinRequestMapper, never()).insert(any(JoinRequest.class));
    }

    /**
     * 查询我的申请时会批量转换团队和申请人展示信息。
     */
    @Test
    void myRequests_whenRequestsExist_returnsVOList() {
        when(joinRequestMapper.findByUserId(2L)).thenReturn(List.of(
            buildJoinRequest(100L, 10L, 2L, Constants.REQUEST_STATUS_PENDING),
            buildJoinRequest(101L, 11L, 2L, Constants.REQUEST_STATUS_REJECTED)
        ));
        when(teamMapper.findById(10L)).thenReturn(buildTeam(10L, 1L));
        when(teamMapper.findById(11L)).thenReturn(buildTeam(11L, 1L));
        when(userMapper.findById(2L)).thenReturn(buildUser(2L, "Applicant"));

        List<JoinRequestVO> requests = joinRequestService.myRequests(2L);

        assertThat(requests).hasSize(2);
        assertThat(requests).extracting(JoinRequestVO::getTeamName).containsExactly("Team10", "Team11");
    }

    /**
     * 管理员审批待处理申请时会添加成员、更新人数并通知申请人。
     */
    @Test
    void approve_whenOperatorIsAdmin_addsMemberAndUpdatesRequest() {
        JoinRequest joinRequest = buildJoinRequest(100L, 10L, 2L, Constants.REQUEST_STATUS_PENDING);
        Team team = buildTeam(10L, 1L);
        when(joinRequestMapper.findById(100L)).thenReturn(joinRequest);
        when(teamMapper.findById(10L)).thenReturn(team);
        when(teamMemberMapper.findByTeamAndUser(10L, 3L)).thenReturn(buildMember(10L, 3L, Constants.MEMBER_ROLE_ADMIN));
        when(teamMemberMapper.findByTeamAndUser(10L, 2L)).thenReturn(null);
        when(teamMemberMapper.countByTeamId(10L)).thenReturn(2);

        joinRequestService.approve(3L, 100L);

        verify(teamMemberMapper).insert(any(TeamMember.class));
        verify(joinRequestMapper).updateStatus(eq(100L), eq(Constants.REQUEST_STATUS_APPROVED), any(LocalDateTime.class));
        verify(teamMapper).updateCurrentMembers(10L, 3);
        verify(notificationService).push(eq(2L), eq(Constants.NOTIFICATION_TYPE_TEAM_UPDATE), anyString(), eq(10L));
    }

    /**
     * 普通成员不能查看或处理团队申请。
     */
    @Test
    void teamRequests_whenOperatorIsPlainMember_throwsBizException() {
        Team team = buildTeam(10L, 1L);
        when(teamMapper.findById(10L)).thenReturn(team);
        when(teamMemberMapper.findByTeamAndUser(10L, 4L)).thenReturn(buildMember(10L, 4L, Constants.MEMBER_ROLE_MEMBER));

        assertThatThrownBy(() -> joinRequestService.teamRequests(4L, 10L))
            .isInstanceOf(BizException.class);
        verify(joinRequestMapper, never()).findByTeamId(10L);
    }

    /**
     * 拒绝申请时会校验待处理状态并通知申请人。
     */
    @Test
    void reject_whenRequestPending_updatesStatusAndNotifiesApplicant() {
        JoinRequest joinRequest = buildJoinRequest(100L, 10L, 2L, Constants.REQUEST_STATUS_PENDING);
        Team team = buildTeam(10L, 1L);
        when(joinRequestMapper.findById(100L)).thenReturn(joinRequest);
        when(teamMapper.findById(10L)).thenReturn(team);
        when(teamMemberMapper.findByTeamAndUser(10L, 1L)).thenReturn(buildMember(10L, 1L, Constants.MEMBER_ROLE_CREATOR));

        joinRequestService.reject(1L, 100L);

        verify(joinRequestMapper).updateStatus(eq(100L), eq(Constants.REQUEST_STATUS_REJECTED), any(LocalDateTime.class));
        verify(notificationService).push(eq(2L), eq(Constants.NOTIFICATION_TYPE_TEAM_UPDATE), anyString(), eq(10L));
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
        team.setStatus(Constants.TEAM_STATUS_ACTIVE);
        return team;
    }

    /**
     * 构造入队申请实体。
     */
    private JoinRequest buildJoinRequest(Long requestId, Long teamId, Long userId, String status) {
        JoinRequest request = new JoinRequest();
        request.setId(requestId);
        request.setTeamId(teamId);
        request.setUserId(userId);
        request.setStatus(status);
        request.setReason("reason");
        request.setRequestedAt(LocalDateTime.now());
        return request;
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
