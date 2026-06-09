package com.zjgsu.teamplatform.controller;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.common.Result;
import com.zjgsu.teamplatform.dto.TeamCreateRequest;
import com.zjgsu.teamplatform.dto.TeamMemberAddRequest;
import com.zjgsu.teamplatform.dto.TeamUpdateRequest;
import com.zjgsu.teamplatform.service.TeamService;
import com.zjgsu.teamplatform.vo.TeamVO;
import com.zjgsu.teamplatform.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 团队接口。
 */
@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /**
     * 创建团队。
     */
    @PostMapping("/create")
    public Result<TeamVO> create(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                                 @Valid @RequestBody TeamCreateRequest request) {
        return Result.success(teamService.create(userId, request));
    }

    /**
     * 查询团队列表，支持关键词、标签和未满员筛选。
     */
    @GetMapping("/list")
    public Result<List<TeamVO>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "tag", required = false) String tag,
                                     @RequestParam(value = "availableOnly", required = false) Boolean availableOnly) {
        return Result.success(teamService.list(keyword, tag, availableOnly));
    }

    /**
     * 查询团队详情。
     */
    @GetMapping("/{id}")
    public Result<TeamVO> detail(@PathVariable("id") Long id) {
        return Result.success(teamService.detail(id));
    }

    /**
     * 查询我的团队。
     */
    @GetMapping("/my")
    public Result<List<TeamVO>> myTeams(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId) {
        return Result.success(teamService.myTeams(userId));
    }

    /**
     * 更新团队。
     */
    @PutMapping("/{id}")
    public Result<TeamVO> update(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                                 @PathVariable("id") Long id,
                                 @Valid @RequestBody TeamUpdateRequest request) {
        return Result.success(teamService.update(userId, id, request));
    }

    /**
     * 删除团队。
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                               @PathVariable("id") Long id) {
        teamService.delete(userId, id);
        return Result.success(null);
    }

    /**
     * 查询团队成员。
     */
    @GetMapping("/{id}/members")
    public Result<List<UserVO>> members(@PathVariable("id") Long id) {
        return Result.success(teamService.members(id));
    }

    /**
     * 添加团队成员。
     */
    @PostMapping("/{id}/members")
    public Result<Void> addMember(@RequestAttribute(Constants.CURRENT_USER_ID) Long userId,
                                  @PathVariable("id") Long id,
                                  @Valid @RequestBody TeamMemberAddRequest request) {
        teamService.addMember(userId, id, request);
        return Result.success(null);
    }

    /**
     * 移除团队成员。
     */
    @DeleteMapping("/{id}/members/{userId}")
    public Result<Void> removeMember(@RequestAttribute(Constants.CURRENT_USER_ID) Long operatorId,
                                     @PathVariable("id") Long id,
                                     @PathVariable("userId") Long userId) {
        teamService.removeMember(operatorId, id, userId);
        return Result.success(null);
    }
}
