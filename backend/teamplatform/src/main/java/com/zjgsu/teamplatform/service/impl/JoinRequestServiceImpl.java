package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.common.ErrorCode;
import com.zjgsu.teamplatform.dto.JoinRequestCreateRequest;
import com.zjgsu.teamplatform.entity.JoinRequest;
import com.zjgsu.teamplatform.entity.Team;
import com.zjgsu.teamplatform.entity.TeamMember;
import com.zjgsu.teamplatform.exception.BizException;
import com.zjgsu.teamplatform.mapper.JoinRequestMapper;
import com.zjgsu.teamplatform.mapper.TeamMapper;
import com.zjgsu.teamplatform.mapper.TeamMemberMapper;
import com.zjgsu.teamplatform.service.JoinRequestService;
import com.zjgsu.teamplatform.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 加入请求服务实现。
 */
@Service
@RequiredArgsConstructor
public class JoinRequestServiceImpl implements JoinRequestService {

    private final JoinRequestMapper joinRequestMapper;
    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final NotificationService notificationService;

    /**
     * 发起加入请求。
     */
    @Override
    public JoinRequest create(Long userId, JoinRequestCreateRequest request) {
        Team team = requireTeam(request.getTeamId());
        if (teamMemberMapper.findByTeamAndUser(team.getId(), userId) != null) {
            throw new BizException(ErrorCode.CONFLICT, "你已在团队中");
        }

        JoinRequest existing = joinRequestMapper.findByTeamAndUser(request.getTeamId(), userId);
        if (existing != null && Constants.REQUEST_STATUS_PENDING.equals(existing.getStatus())) {
            throw new BizException(ErrorCode.CONFLICT, "你已提交过待处理申请");
        }

        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setTeamId(request.getTeamId());
        joinRequest.setUserId(userId);
        joinRequest.setStatus(Constants.REQUEST_STATUS_PENDING);
        joinRequest.setReason(request.getReason());
        joinRequestMapper.insert(joinRequest);

        notificationService.push(team.getCreatorId(), Constants.NOTIFICATION_TYPE_JOIN_REQUEST,
                "你有新的入队申请，申请人ID=" + userId + "，团队ID=" + team.getId());
        return joinRequestMapper.findById(joinRequest.getId());
    }

    /**
     * 查询我的加入请求。
     */
    @Override
    public List<JoinRequest> myRequests(Long userId) {
        return joinRequestMapper.findByUserId(userId);
    }

    /**
     * 查询团队加入请求。
     */
    @Override
    public List<JoinRequest> teamRequests(Long operatorId, Long teamId) {
        Team team = requireTeam(teamId);
        checkTeamAdmin(operatorId, team);
        return joinRequestMapper.findByTeamId(teamId);
    }

    /**
     * 批准加入请求。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long operatorId, Long requestId) {
        JoinRequest joinRequest = requireJoinRequest(requestId);
        Team team = requireTeam(joinRequest.getTeamId());
        checkTeamAdmin(operatorId, team);

        if (!Constants.REQUEST_STATUS_PENDING.equals(joinRequest.getStatus())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "该申请已处理");
        }
        if (teamMemberMapper.findByTeamAndUser(team.getId(), joinRequest.getUserId()) != null) {
            throw new BizException(ErrorCode.CONFLICT, "申请用户已在团队中");
        }

        Integer count = teamMemberMapper.countByTeamId(team.getId());
        if (count >= team.getMaxMembers()) {
            throw new BizException(ErrorCode.BAD_REQUEST, "团队人数已满");
        }

        TeamMember member = new TeamMember();
        member.setTeamId(team.getId());
        member.setUserId(joinRequest.getUserId());
        member.setRole(Constants.MEMBER_ROLE_MEMBER);
        teamMemberMapper.insert(member);

        joinRequestMapper.updateStatus(joinRequest.getId(), Constants.REQUEST_STATUS_APPROVED, LocalDateTime.now());
        teamMapper.updateCurrentMembers(team.getId(), count + 1);

        notificationService.push(joinRequest.getUserId(), Constants.NOTIFICATION_TYPE_SYSTEM,
                "你的入队申请已通过，团队ID=" + team.getId());
    }

    /**
     * 拒绝加入请求。
     */
    @Override
    public void reject(Long operatorId, Long requestId) {
        JoinRequest joinRequest = requireJoinRequest(requestId);
        Team team = requireTeam(joinRequest.getTeamId());
        checkTeamAdmin(operatorId, team);

        if (!Constants.REQUEST_STATUS_PENDING.equals(joinRequest.getStatus())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "该申请已处理");
        }

        joinRequestMapper.updateStatus(joinRequest.getId(), Constants.REQUEST_STATUS_REJECTED, LocalDateTime.now());
        notificationService.push(joinRequest.getUserId(), Constants.NOTIFICATION_TYPE_SYSTEM,
                "你的入队申请已被拒绝，团队ID=" + team.getId());
    }

    /**
     * 校验团队存在。
     */
    private Team requireTeam(Long teamId) {
        Team team = teamMapper.findById(teamId);
        if (team == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "团队不存在");
        }
        return team;
    }

    /**
     * 校验请求存在。
     */
    private JoinRequest requireJoinRequest(Long requestId) {
        JoinRequest joinRequest = joinRequestMapper.findById(requestId);
        if (joinRequest == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "申请不存在");
        }
        return joinRequest;
    }

    /**
     * 校验团队管理权限。
     */
    private void checkTeamAdmin(Long userId, Team team) {
        TeamMember member = teamMemberMapper.findByTeamAndUser(team.getId(), userId);
        if (member == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "你不是团队成员");
        }
        boolean isCreator = userId.equals(team.getCreatorId());
        boolean isAdmin = Constants.MEMBER_ROLE_ADMIN.equals(member.getRole());
        if (!isCreator && !isAdmin) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "仅管理员可执行该操作");
        }
    }
}
