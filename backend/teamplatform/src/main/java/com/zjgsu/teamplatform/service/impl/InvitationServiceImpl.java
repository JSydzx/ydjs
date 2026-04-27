package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.common.ErrorCode;
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
import com.zjgsu.teamplatform.service.InvitationService;
import com.zjgsu.teamplatform.service.NotificationService;
import com.zjgsu.teamplatform.vo.InvitationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 邀请服务实现。
 */
@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationMapper invitationMapper;
    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    /**
     * 发送邀请。
     */
    @Override
    public void send(Long operatorId, InvitationSendRequest request) {
        Team team = requireTeam(request.getTeamId());
        checkTeamAdmin(operatorId, team);

        if (teamMemberMapper.findByTeamAndUser(team.getId(), request.getUserId()) != null) {
            throw new BizException(ErrorCode.CONFLICT, "用户已在团队中");
        }

        Invitation pending = invitationMapper.findPendingByTeamAndUser(request.getTeamId(), request.getUserId());
        if (pending != null) {
            throw new BizException(ErrorCode.CONFLICT, "该用户已有待处理邀请");
        }

        Invitation invitation = new Invitation();
        invitation.setInviteId(operatorId);
        invitation.setTeamId(request.getTeamId());
        invitation.setUserId(request.getUserId());
        invitation.setStatus(Constants.INVITATION_STATUS_PENDING);
        invitationMapper.insert(invitation);

        notificationService.push(request.getUserId(), Constants.NOTIFICATION_TYPE_SYSTEM,
                "你收到了新的团队邀请，团队ID=" + request.getTeamId());
    }

    /**
     * 查询我的邀请。
     */
    @Override
    public List<InvitationVO> myInvitations(Long userId) {
        List<Invitation> invitations = invitationMapper.findByUserId(userId);
        List<InvitationVO> result = new ArrayList<>();
        for (Invitation invitation : invitations) {
            result.add(toVO(invitation));
        }
        return result;
    }

    /**
     * 查询团队邀请。
     */
    @Override
    public List<InvitationVO> teamInvitations(Long operatorId, Long teamId) {
        Team team = requireTeam(teamId);
        checkTeamAdmin(operatorId, team);

        List<Invitation> invitations = invitationMapper.findByTeamId(teamId);
        List<InvitationVO> result = new ArrayList<>();
        for (Invitation invitation : invitations) {
            result.add(toVO(invitation));
        }
        return result;
    }

    /**
     * 接受邀请。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void accept(Long userId, Long invitationId) {
        Invitation invitation = requireInvitation(invitationId);
        if (!userId.equals(invitation.getUserId())) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "只能处理自己的邀请");
        }
        if (!Constants.INVITATION_STATUS_PENDING.equals(invitation.getStatus())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "该邀请已处理");
        }

        Team team = requireTeam(invitation.getTeamId());
        if (teamMemberMapper.findByTeamAndUser(team.getId(), userId) != null) {
            throw new BizException(ErrorCode.CONFLICT, "你已在团队中");
        }

        Integer count = teamMemberMapper.countByTeamId(team.getId());
        if (count >= team.getMaxMembers()) {
            throw new BizException(ErrorCode.BAD_REQUEST, "团队人数已满");
        }

        TeamMember member = new TeamMember();
        member.setTeamId(team.getId());
        member.setUserId(userId);
        member.setRole(Constants.MEMBER_ROLE_MEMBER);
        teamMemberMapper.insert(member);

        invitationMapper.updateStatus(invitationId, Constants.INVITATION_STATUS_ACCEPTED);
        teamMapper.updateCurrentMembers(team.getId(), count + 1);

        notificationService.push(invitation.getInviteId(), Constants.NOTIFICATION_TYPE_SYSTEM,
                "你发送的邀请已被接受，邀请ID=" + invitationId);
    }

    /**
     * 拒绝邀请。
     */
    @Override
    public void reject(Long userId, Long invitationId) {
        Invitation invitation = requireInvitation(invitationId);
        if (!userId.equals(invitation.getUserId())) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "只能处理自己的邀请");
        }
        if (!Constants.INVITATION_STATUS_PENDING.equals(invitation.getStatus())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "该邀请已处理");
        }
        invitationMapper.updateStatus(invitationId, Constants.INVITATION_STATUS_REJECTED);
        notificationService.push(invitation.getInviteId(), Constants.NOTIFICATION_TYPE_SYSTEM,
                "你发送的邀请已被拒绝，邀请ID=" + invitationId);
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
     * 校验邀请存在。
     */
    private Invitation requireInvitation(Long invitationId) {
        Invitation invitation = invitationMapper.findById(invitationId);
        if (invitation == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "邀请不存在");
        }
        return invitation;
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
     * 实体转视图对象。
     */
    private InvitationVO toVO(Invitation invitation) {
        Team team = teamMapper.findById(invitation.getTeamId());
        User inviter = userMapper.findById(invitation.getInviteId());

        InvitationVO vo = new InvitationVO();
        vo.setId(invitation.getId());
        vo.setTeamId(invitation.getTeamId());
        vo.setTeamName(team == null ? null : team.getName());
        vo.setInviteId(invitation.getInviteId());
        vo.setInviterName(inviter == null ? null : inviter.getNickname());
        vo.setUserId(invitation.getUserId());
        vo.setStatus(invitation.getStatus());
        vo.setCreatedAt(invitation.getCreatedAt());
        return vo;
    }
}
