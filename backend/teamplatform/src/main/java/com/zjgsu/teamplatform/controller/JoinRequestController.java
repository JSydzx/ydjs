package com.zjgsu.teamplatform.controller;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.common.Result;
import com.zjgsu.teamplatform.dto.JoinRequestCreateRequest;
import com.zjgsu.teamplatform.entity.JoinRequest;
import com.zjgsu.teamplatform.service.JoinRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 加入请求接口。
 */
@RestController
@RequiredArgsConstructor
public class JoinRequestController {

    private final JoinRequestService joinRequestService;

    /**
     * 发起加入请求。
     */
    @PostMapping("/api/join/request")
    public Result<JoinRequest> create(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                      @Valid @RequestBody JoinRequestCreateRequest request) {
        return Result.success(joinRequestService.create(userId, request));
    }

    /**
     * 查看我的加入请求。
     */
    @GetMapping("/api/join/requests")
    public Result<List<JoinRequest>> myRequests(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.success(joinRequestService.myRequests(userId));
    }

    /**
     * 查看团队加入请求。
     */
    @GetMapping("/api/team/{id}/join-requests")
    public Result<List<JoinRequest>> teamRequests(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                                  @PathVariable("id") Long teamId) {
        return Result.success(joinRequestService.teamRequests(userId, teamId));
    }

    /**
     * 批准加入请求。
     */
    @PostMapping("/api/join/{requestId}/approve")
    public Result<Void> approve(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                @PathVariable("requestId") Long requestId) {
        joinRequestService.approve(userId, requestId);
        return Result.success(null);
    }

    /**
     * 拒绝加入请求。
     */
    @PostMapping("/api/join/{requestId}/reject")
    public Result<Void> reject(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                               @PathVariable("requestId") Long requestId) {
        joinRequestService.reject(userId, requestId);
        return Result.success(null);
    }
}
