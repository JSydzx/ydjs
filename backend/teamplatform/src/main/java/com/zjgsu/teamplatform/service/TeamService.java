package com.zjgsu.teamplatform.service;

import com.zjgsu.teamplatform.dto.TeamCreateRequest;
import com.zjgsu.teamplatform.dto.TeamMemberAddRequest;
import com.zjgsu.teamplatform.dto.TeamUpdateRequest;
import com.zjgsu.teamplatform.vo.TeamVO;
import com.zjgsu.teamplatform.vo.UserVO;

import java.util.List;

/**
 * 团队服务。
 */
public interface TeamService {

    /**
     * 创建团队。
     */
    TeamVO create(Long userId, TeamCreateRequest request);

    /**
     * 获取团队列表。
     */
    List<TeamVO> list();

    /**
     * 获取团队详情。
     */
    TeamVO detail(Long teamId);

    /**
     * 获取我的团队。
     */
    List<TeamVO> myTeams(Long userId);

    /**
     * 更新团队。
     */
    TeamVO update(Long userId, Long teamId, TeamUpdateRequest request);

    /**
     * 删除团队。
     */
    void delete(Long userId, Long teamId);

    /**
     * 获取团队成员。
     */
    List<UserVO> members(Long teamId);

    /**
     * 添加团队成员。
     */
    void addMember(Long operatorId, Long teamId, TeamMemberAddRequest request);

    /**
     * 移除团队成员。
     */
    void removeMember(Long operatorId, Long teamId, Long userId);
}
