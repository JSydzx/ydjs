package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.common.ErrorCode;
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
import com.zjgsu.teamplatform.service.JoinRequestService;
import com.zjgsu.teamplatform.service.NotificationService;
import com.zjgsu.teamplatform.vo.JoinRequestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 入队申请服务实现。
 */
@Service
@RequiredArgsConstructor
public class JoinRequestServiceImpl implements JoinRequestService {

    private final JoinRequestMapper joinRequestMapper;
    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    /**
     * 发起入队申请，关闭招募和满员团队不允许继续申请。
     */
    @Override
    public JoinRequestVO create(Long userId, JoinRequestCreateRequest request) {
        Team team = requireTeam(request.getTeamId());
        if (!Constants.TEAM_STATUS_ACTIVE.equals(team.getStatus())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "团队已关闭招募");
        }
        if (team.getCurrentMembers() >= team.getMaxMembers()) {
            throw new BizException(ErrorCode.BAD_REQUEST, "团队人数已满");
        }
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

        User applicant = userMapper.findById(userId);
        notificationService.push(team.getCreatorId(), Constants.NOTIFICATION_TYPE_JOIN_REQUEST,
                displayName(applicant, userId) + " 申请加入团队「" + team.getName() + "」", team.getId());
        return toVO(joinRequestMapper.findById(joinRequest.getId()));
    }

    /**
     * 查询我的入队申请。
     */
    @Override
    public List<JoinRequestVO> myRequests(Long userId) {
        return toVOList(joinRequestMapper.findByUserId(userId));
    }

    /**
     * 查询团队入队申请，仅团队管理员可查看。
     */
    @Override
    public List<JoinRequestVO> teamRequests(Long operatorId, Long teamId) {
        Team team = requireTeam(teamId);
        checkTeamAdmin(operatorId, team);
        return toVOList(joinRequestMapper.findByTeamId(teamId));
    }

    /**
     * 批准入队申请，并同步团队人数和申请人通知。
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
        if (!Constants.TEAM_STATUS_ACTIVE.equals(team.getStatus())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "团队已关闭招募");
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

        notificationService.push(joinRequest.getUserId(), Constants.NOTIFICATION_TYPE_TEAM_UPDATE,
                "你的入队申请已通过，团队「" + team.getName() + "」欢迎你", team.getId());
    }

    /**
     * 拒绝入队申请，并通知申请人。
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
        notificationService.push(joinRequest.getUserId(), Constants.NOTIFICATION_TYPE_TEAM_UPDATE,
                "你的入队申请已被拒绝，团队「" + team.getName() + "」", team.getId());
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
     * 校验申请存在。
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

    /**
     * 批量转换申请视图。
     */
    private List<JoinRequestVO> toVOList(List<JoinRequest> requests) {
        List<JoinRequestVO> result = new ArrayList<>();
        for (JoinRequest request : requests) {
            result.add(toVO(request));
        }
        return result;
    }

    /**
     * 转换入队申请视图对象。
     */
    private JoinRequestVO toVO(JoinRequest request) {
        Team team = teamMapper.findById(request.getTeamId());
        User user = userMapper.findById(request.getUserId());
        JoinRequestVO vo = new JoinRequestVO();
        vo.setId(request.getId());
        vo.setTeamId(request.getTeamId());
        vo.setTeamName(team == null ? null : team.getName());
        vo.setUserId(request.getUserId());
        vo.setApplicantName(displayName(user, request.getUserId()));
        vo.setStatus(request.getStatus());
        vo.setRequestedAt(request.getRequestedAt());
        vo.setProcessedAt(request.getProcessedAt());
        vo.setReason(request.getReason());
        return vo;
    }

    /**
     * 优先显示昵称，缺失时回退到用户名。
     */
    private String displayName(User user, Long userId) {
        if (user == null) {
            return "用户" + userId;
        }
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        return user.getUsername();
    }
}
