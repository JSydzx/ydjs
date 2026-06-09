package com.zjgsu.teamplatform.service;

import com.zjgsu.teamplatform.dto.JoinRequestCreateRequest;
import com.zjgsu.teamplatform.vo.JoinRequestVO;

import java.util.List;

/**
 * 加入请求服务。
 */
public interface JoinRequestService {

    /**
     * 发起加入请求。
     */
    JoinRequestVO create(Long userId, JoinRequestCreateRequest request);

    /**
     * 查询我的加入请求。
     */
    List<JoinRequestVO> myRequests(Long userId);

    /**
     * 查询团队加入请求。
     */
    List<JoinRequestVO> teamRequests(Long operatorId, Long teamId);

    /**
     * 批准加入请求。
     */
    void approve(Long operatorId, Long requestId);

    /**
     * 拒绝加入请求。
     */
    void reject(Long operatorId, Long requestId);
}
