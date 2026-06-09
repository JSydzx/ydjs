package com.zjgsu.teamplatform.controller;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.common.Result;
import com.zjgsu.teamplatform.dto.JoinRequestCreateRequest;
import com.zjgsu.teamplatform.service.JoinRequestService;
import com.zjgsu.teamplatform.vo.JoinRequestVO;
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
 * 入队申请接口。
 */
@RestController
@RequiredArgsConstructor
public class JoinRequestController {

    private final JoinRequestService joinRequestService;

    /**
     * 发起入队申请。
     */
    @PostMapping("/api/join/request")
    public Result<JoinRequestVO> create(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                                        @Valid @RequestBody JoinRequestCreateRequest request) {
        return Result.success(joinRequestService.create(userId, request));
    }

    /**
     * 查看我的入队申请。
     */
    @GetMapping("/api/join/requests")
    public Result<List<JoinRequestVO>> myRequests(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId) {
        return Result.success(joinRequestService.myRequests(userId));
    }

    /**
     * 查看团队入队申请。
     */
    @GetMapping("/api/team/{id}/join-requests")
    public Result<List<JoinRequestVO>> teamRequests(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                                                    @PathVariable("id") Long teamId) {
        return Result.success(joinRequestService.teamRequests(userId, teamId));
    }

    /**
     * 批准入队申请。
     */
    @PostMapping("/api/join/{requestId}/approve")
    public Result<Void> approve(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                                @PathVariable("requestId") Long requestId) {
        joinRequestService.approve(userId, requestId);
        return Result.success(null);
    }

    /**
     * 拒绝入队申请。
     */
    @PostMapping("/api/join/{requestId}/reject")
    public Result<Void> reject(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                               @PathVariable("requestId") Long requestId) {
        joinRequestService.reject(userId, requestId);
        return Result.success(null);
    }
}
