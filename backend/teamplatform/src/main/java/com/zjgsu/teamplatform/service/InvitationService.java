package com.zjgsu.teamplatform.service;

import com.zjgsu.teamplatform.dto.InvitationSendRequest;
import com.zjgsu.teamplatform.vo.InvitationVO;

import java.util.List;

/**
 * 邀请服务。
 */
public interface InvitationService {

    /**
     * 发送邀请。
     */
    void send(Long operatorId, InvitationSendRequest request);

    /**
     * 查询我的邀请。
     */
    List<InvitationVO> myInvitations(Long userId);

    /**
     * 查询团队邀请。
     */
    List<InvitationVO> teamInvitations(Long operatorId, Long teamId);

    /**
     * 接受邀请。
     */
    void accept(Long userId, Long invitationId);

    /**
     * 拒绝邀请。
     */
    void reject(Long userId, Long invitationId);
}
