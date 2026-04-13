package com.zjgsu.teamplatform.controller;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.common.Result;
import com.zjgsu.teamplatform.dto.InvitationSendRequest;
import com.zjgsu.teamplatform.service.InvitationService;
import com.zjgsu.teamplatform.vo.InvitationVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 邀请接口。
 */
@RestController
public class InvitationController {

    private final InvitationService invitationService;

    /**
     * 构造器注入。
     */
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    /**
     * 发送邀请。
     */
    @PostMapping("/api/invitation/send")
    public Result<Void> send(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                             @Valid @RequestBody InvitationSendRequest request) {
        invitationService.send(userId, request);
        return Result.success(null);
    }

    /**
     * 查询我的邀请。
     */
    @GetMapping("/api/invitation/list")
    public Result<List<InvitationVO>> list(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.success(invitationService.myInvitations(userId));
    }

    /**
     * 查询团队邀请。
     */
    @GetMapping("/api/team/{id}/invitations")
    public Result<List<InvitationVO>> teamInvitations(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                                      @PathVariable("id") Long teamId) {
        return Result.success(invitationService.teamInvitations(userId, teamId));
    }

    /**
     * 接受邀请。
     */
    @PostMapping("/api/invitation/{id}/accept")
    public Result<Void> accept(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                               @PathVariable("id") Long invitationId) {
        invitationService.accept(userId, invitationId);
        return Result.success(null);
    }

    /**
     * 拒绝邀请。
     */
    @PostMapping("/api/invitation/{id}/reject")
    public Result<Void> reject(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                               @PathVariable("id") Long invitationId) {
        invitationService.reject(userId, invitationId);
        return Result.success(null);
    }
}
