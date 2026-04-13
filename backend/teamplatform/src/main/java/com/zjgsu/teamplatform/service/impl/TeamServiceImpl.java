package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.common.ErrorCode;
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
import com.zjgsu.teamplatform.service.TeamService;
import com.zjgsu.teamplatform.vo.TeamVO;
import com.zjgsu.teamplatform.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 团队服务实现。
 */
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final UserMapper userMapper;

    /**
     * 创建团队并自动加入创建者。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeamVO create(Long userId, TeamCreateRequest request) {
        User creator = userMapper.findById(userId);
        if (creator == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        Team team = new Team();
        team.setName(request.getName());
        team.setDescription(request.getDescription());
        team.setTag(request.getTag());
        team.setCreatorId(userId);
        team.setMaxMembers(request.getMaxMembers() == null ? 10 : request.getMaxMembers());
        team.setCurrentMembers(1);
        team.setStatus(Constants.TEAM_STATUS_ACTIVE);
        teamMapper.insert(team);

        TeamMember member = new TeamMember();
        member.setTeamId(team.getId());
        member.setUserId(userId);
        member.setRole(Constants.MEMBER_ROLE_CREATOR);
        teamMemberMapper.insert(member);

        return toVO(teamMapper.findById(team.getId()));
    }

    /**
     * 查询团队列表。
     */
    @Override
    public List<TeamVO> list() {
        List<Team> teams = teamMapper.findAllActive();
        List<TeamVO> result = new ArrayList<>();
        for (Team team : teams) {
            result.add(toVO(team));
        }
        return result;
    }

    /**
     * 查询团队详情。
     */
    @Override
    public TeamVO detail(Long teamId) {
        Team team = requireTeam(teamId);
        return toVO(team);
    }

    /**
     * 查询我的团队。
     */
    @Override
    public List<TeamVO> myTeams(Long userId) {
        List<Team> teams = teamMapper.findByCreatorId(userId);
        List<TeamVO> result = new ArrayList<>();
        for (Team team : teams) {
            result.add(toVO(team));
        }
        return result;
    }

    /**
     * 更新团队信息。
     */
    @Override
    public TeamVO update(Long userId, Long teamId, TeamUpdateRequest request) {
        Team team = requireTeam(teamId);
        checkTeamAdmin(userId, team);

        if (request.getName() != null) {
            team.setName(request.getName());
        }
        if (request.getDescription() != null) {
            team.setDescription(request.getDescription());
        }
        if (request.getTag() != null) {
            team.setTag(request.getTag());
        }
        if (request.getMaxMembers() != null) {
            if (request.getMaxMembers() < team.getCurrentMembers()) {
                throw new BizException(ErrorCode.BAD_REQUEST, "团队人数上限不能小于当前人数");
            }
            team.setMaxMembers(request.getMaxMembers());
        }
        if (request.getStatus() != null) {
            team.setStatus(request.getStatus());
        }
        teamMapper.update(team);
        return toVO(teamMapper.findById(teamId));
    }

    /**
     * 删除团队。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long teamId) {
        Team team = requireTeam(teamId);
        if (!userId.equals(team.getCreatorId())) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "仅团队创建者可删除团队");
        }
        teamMemberMapper.deleteByTeamId(teamId);
        teamMapper.deleteById(teamId);
    }

    /**
     * 查询团队成员。
     */
    @Override
    public List<UserVO> members(Long teamId) {
        requireTeam(teamId);
        List<TeamMember> members = teamMemberMapper.findByTeamId(teamId);
        List<UserVO> result = new ArrayList<>();
        for (TeamMember member : members) {
            User user = userMapper.findById(member.getUserId());
            if (user != null) {
                UserVO vo = new UserVO();
                vo.setId(user.getId());
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname());
                vo.setEmail(user.getEmail());
                vo.setAvatar(user.getAvatar());
                result.add(vo);
            }
        }
        return result;
    }

    /**
     * 添加团队成员。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMember(Long operatorId, Long teamId, TeamMemberAddRequest request) {
        Team team = requireTeam(teamId);
        checkTeamAdmin(operatorId, team);

        if (teamMemberMapper.findByTeamAndUser(teamId, request.getUserId()) != null) {
            throw new BizException(ErrorCode.CONFLICT, "用户已在团队中");
        }

        Integer count = teamMemberMapper.countByTeamId(teamId);
        if (count >= team.getMaxMembers()) {
            throw new BizException(ErrorCode.BAD_REQUEST, "团队人数已满");
        }

        TeamMember teamMember = new TeamMember();
        teamMember.setTeamId(teamId);
        teamMember.setUserId(request.getUserId());
        teamMember.setRole(Constants.MEMBER_ROLE_MEMBER);
        teamMemberMapper.insert(teamMember);
        teamMapper.updateCurrentMembers(teamId, count + 1);
    }

    /**
     * 移除团队成员。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long operatorId, Long teamId, Long userId) {
        Team team = requireTeam(teamId);
        checkTeamAdmin(operatorId, team);

        if (userId.equals(team.getCreatorId())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "不能移除团队创建者");
        }

        int affected = teamMemberMapper.deleteByTeamAndUser(teamId, userId);
        if (affected == 0) {
            throw new BizException(ErrorCode.NOT_FOUND, "团队成员不存在");
        }

        Integer count = teamMemberMapper.countByTeamId(teamId);
        teamMapper.updateCurrentMembers(teamId, count);
    }

    /**
     * 加载团队并确保存在。
     */
    private Team requireTeam(Long teamId) {
        Team team = teamMapper.findById(teamId);
        if (team == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "团队不存在");
        }
        return team;
    }

    /**
     * 校验团队管理权限。
     */
    private void checkTeamAdmin(Long userId, Team team) {
        TeamMember operatorMember = teamMemberMapper.findByTeamAndUser(team.getId(), userId);
        if (operatorMember == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "你不是该团队成员");
        }
        boolean isCreator = userId.equals(team.getCreatorId());
        boolean isAdmin = Constants.MEMBER_ROLE_ADMIN.equals(operatorMember.getRole());
        if (!isCreator && !isAdmin) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "仅管理员可执行该操作");
        }
    }

    /**
     * 实体转视图对象。
     */
    private TeamVO toVO(Team team) {
        User creator = userMapper.findById(team.getCreatorId());
        TeamVO vo = new TeamVO();
        vo.setId(team.getId());
        vo.setName(team.getName());
        vo.setDescription(team.getDescription());
        vo.setTag(team.getTag());
        vo.setCreatorId(team.getCreatorId());
        vo.setCreatorName(creator == null ? null : creator.getNickname());
        vo.setCurrentMembers(team.getCurrentMembers());
        vo.setMaxMembers(team.getMaxMembers());
        vo.setStatus(team.getStatus());
        vo.setCreatedAt(team.getCreatedAt());
        return vo;
    }
}
