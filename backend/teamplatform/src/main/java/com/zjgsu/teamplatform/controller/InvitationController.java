package com.zjgsu.teamplatform.controller;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.common.Result;
import com.zjgsu.teamplatform.dto.InvitationSendRequest;
import com.zjgsu.teamplatform.service.InvitationService;
import com.zjgsu.teamplatform.vo.InvitationVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 邀请接口。
 */
@RestController
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    /**
     * 发送邀请。
     */
    @PostMapping("/api/invitation/send")
    public Result<Void> send(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                             @Valid @RequestBody InvitationSendRequest request) {
        invitationService.send(userId, request);
        return Result.success(null);
    }

    /**
     * 查询我的邀请。
     */
    @GetMapping("/api/invitation/list")
    public Result<List<InvitationVO>> list(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId) {
        return Result.success(invitationService.myInvitations(userId));
    }

    /**
     * 查询团队邀请。
     */
    @GetMapping("/api/team/{id}/invitations")
    public Result<List<InvitationVO>> teamInvitations(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                                                      @PathVariable("id") Long teamId) {
        return Result.success(invitationService.teamInvitations(userId, teamId));
    }

    /**
     * 接受邀请。
     */
    @PostMapping("/api/invitation/{id}/accept")
    public Result<Void> accept(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                               @PathVariable("id") Long invitationId) {
        invitationService.accept(userId, invitationId);
        return Result.success(null);
    }

    /**
     * 拒绝邀请。
     */
    @PostMapping("/api/invitation/{id}/reject")
    public Result<Void> reject(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                               @PathVariable("id") Long invitationId) {
        invitationService.reject(userId, invitationId);
        return Result.success(null);
    }
}
