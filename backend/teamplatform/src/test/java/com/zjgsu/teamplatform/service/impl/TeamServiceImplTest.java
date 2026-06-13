package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.dto.TeamCreateRequest;
import com.zjgsu.teamplatform.dto.TeamMemberAddRequest;
import com.zjgsu.teamplatform.dto.TeamUpdateRequest;
import com.zjgsu.teamplatform.entity.Team;
import com.zjgsu.teamplatform.entity.TeamMember;
import com.zjgsu.teamplatform.entity.User;
import com.zjgsu.teamplatform.exception.BizException;
import com.zjgsu.teamplatform.mapper.TeamMapper;
import com.zjgsu.teamplatform.mapper.TeamMemberMapper;
import com.zjgsu.teamplatform.mapper.UserMapper;
import com.zjgsu.teamplatform.vo.TeamVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 团队服务单元测试，覆盖创建、查询、成员管理和权限校验。
 */
class TeamServiceImplTest {

    private TeamMapper teamMapper;
    private TeamMemberMapper teamMemberMapper;
    private UserMapper userMapper;
    private TeamServiceImpl teamService;

    /**
     * 初始化团队服务及其依赖。
     */
    @BeforeEach
    void setUp() {
        teamMapper = mock(TeamMapper.class);
        teamMemberMapper = mock(TeamMemberMapper.class);
        userMapper = mock(UserMapper.class);
        teamService = new TeamServiceImpl(teamMapper, teamMemberMapper, userMapper);
    }

    /**
     * 创建团队时会默认设置成员数、状态，并把创建者加入成员表。
     */
    @Test
    void create_whenCreatorExists_createsTeamAndCreatorMember() {
        TeamCreateRequest request = new TeamCreateRequest();
        request.setName("算法竞赛队");
        request.setDescription("准备比赛");
        request.setTag("算法");
        when(userMapper.findById(1L)).thenReturn(buildUser(1L, "Alice"));
        when(teamMapper.insert(any(Team.class))).thenAnswer(invocation -> {
            Team team = invocation.getArgument(0);
            team.setId(10L);
            return 1;
        });
        Team savedTeam = buildTeam(10L, 1L);
        when(teamMapper.findById(10L)).thenReturn(savedTeam);

        TeamVO teamVO = teamService.create(1L, request);

        ArgumentCaptor<TeamMember> memberCaptor = ArgumentCaptor.forClass(TeamMember.class);
        verify(teamMemberMapper).insert(memberCaptor.capture());
        assertThat(memberCaptor.getValue().getRole()).isEqualTo(Constants.MEMBER_ROLE_CREATOR);
        assertThat(teamVO.getName()).isEqualTo("算法竞赛队");
        assertThat(teamVO.getCreatorName()).isEqualTo("Alice");
    }

    /**
     * 创建团队时找不到创建者会直接失败。
     */
    @Test
    void create_whenCreatorMissing_throwsBizException() {
        when(userMapper.findById(1L)).thenReturn(null);

        assertThatThrownBy(() -> teamService.create(1L, new TeamCreateRequest()))
            .isInstanceOf(BizException.class);
        verify(teamMapper, never()).insert(any(Team.class));
    }

    /**
     * 查询列表会清理空白筛选条件，再转换为视图对象。
     */
    @Test
    void list_trimsBlankFiltersAndReturnsTeams() {
        when(teamMapper.findByFilters(null, "Java", true))
            .thenReturn(List.of(buildTeam(10L, 1L)));
        when(userMapper.findById(1L)).thenReturn(buildUser(1L, "Alice"));

        List<TeamVO> teams = teamService.list("   ", " Java ", true);

        assertThat(teams).hasSize(1);
        assertThat(teams.get(0).getCreatorName()).isEqualTo("Alice");
    }

    /**
     * 管理员可以更新团队信息。
     */
    @Test
    void update_whenAdmin_updatesTeam() {
        Team team = buildTeam(10L, 1L);
        TeamUpdateRequest request = new TeamUpdateRequest();
        request.setName("新队名");
        request.setMaxMembers(6);
        when(teamMapper.findById(10L)).thenReturn(team);
        when(teamMemberMapper.findByTeamAndUser(10L, 2L)).thenReturn(buildMember(10L, 2L, Constants.MEMBER_ROLE_ADMIN));
        when(userMapper.findById(1L)).thenReturn(buildUser(1L, "Alice"));

        TeamVO teamVO = teamService.update(2L, 10L, request);

        verify(teamMapper).update(team);
        assertThat(teamVO.getName()).isEqualTo("新队名");
        assertThat(teamVO.getMaxMembers()).isEqualTo(6);
    }

    /**
     * 普通成员不能执行团队管理操作。
     */
    @Test
    void update_whenMemberIsNotAdmin_throwsBizException() {
        Team team = buildTeam(10L, 1L);
        when(teamMapper.findById(10L)).thenReturn(team);
        when(teamMemberMapper.findByTeamAndUser(10L, 3L)).thenReturn(buildMember(10L, 3L, Constants.MEMBER_ROLE_MEMBER));

        assertThatThrownBy(() -> teamService.update(3L, 10L, new TeamUpdateRequest()))
            .isInstanceOf(BizException.class);
        verify(teamMapper, never()).update(any(Team.class));
    }

    /**
     * 添加成员时会校验容量并同步当前成员数。
     */
    @Test
    void addMember_whenTeamHasSpace_insertsMemberAndUpdatesCount() {
        Team team = buildTeam(10L, 1L);
        TeamMemberAddRequest request = new TeamMemberAddRequest();
        request.setUserId(4L);
        when(teamMapper.findById(10L)).thenReturn(team);
        when(teamMemberMapper.findByTeamAndUser(10L, 1L)).thenReturn(buildMember(10L, 1L, Constants.MEMBER_ROLE_CREATOR));
        when(teamMemberMapper.findByTeamAndUser(10L, 4L)).thenReturn(null);
        when(teamMemberMapper.countByTeamId(10L)).thenReturn(2);

        teamService.addMember(1L, 10L, request);

        verify(teamMemberMapper).insert(any(TeamMember.class));
        verify(teamMapper).updateCurrentMembers(10L, 3);
    }

    /**
     * 团队满员时不能继续添加成员。
     */
    @Test
    void addMember_whenTeamFull_throwsBizException() {
        Team team = buildTeam(10L, 1L);
        team.setMaxMembers(2);
        TeamMemberAddRequest request = new TeamMemberAddRequest();
        request.setUserId(4L);
        when(teamMapper.findById(10L)).thenReturn(team);
        when(teamMemberMapper.findByTeamAndUser(10L, 1L)).thenReturn(buildMember(10L, 1L, Constants.MEMBER_ROLE_CREATOR));
        when(teamMemberMapper.findByTeamAndUser(10L, 4L)).thenReturn(null);
        when(teamMemberMapper.countByTeamId(10L)).thenReturn(2);

        assertThatThrownBy(() -> teamService.addMember(1L, 10L, request))
            .isInstanceOf(BizException.class);
        verify(teamMemberMapper, never()).insert(any(TeamMember.class));
    }

    /**
     * 移除成员后会按数据库最新计数同步团队人数。
     */
    @Test
    void removeMember_whenMemberExists_deletesAndUpdatesCount() {
        Team team = buildTeam(10L, 1L);
        when(teamMapper.findById(10L)).thenReturn(team);
        when(teamMemberMapper.findByTeamAndUser(10L, 1L)).thenReturn(buildMember(10L, 1L, Constants.MEMBER_ROLE_CREATOR));
        when(teamMemberMapper.deleteByTeamAndUser(10L, 4L)).thenReturn(1);
        when(teamMemberMapper.countByTeamId(10L)).thenReturn(2);

        teamService.removeMember(1L, 10L, 4L);

        verify(teamMapper).updateCurrentMembers(10L, 2);
    }

    /**
     * 构造团队实体。
     */
    private Team buildTeam(Long teamId, Long creatorId) {
        Team team = new Team();
        team.setId(teamId);
        team.setName("算法竞赛队");
        team.setDescription("准备比赛");
        team.setTag("算法");
        team.setCreatorId(creatorId);
        team.setCurrentMembers(2);
        team.setMaxMembers(5);
        team.setStatus(Constants.TEAM_STATUS_ACTIVE);
        return team;
    }

    /**
     * 构造成员实体。
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
        user.setEmail("user" + userId + "@example.com");
        return user;
    }
}
