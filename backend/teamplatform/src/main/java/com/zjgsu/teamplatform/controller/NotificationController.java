package com.zjgsu.teamplatform.controller;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.common.Result;
import com.zjgsu.teamplatform.entity.Notification;
import com.zjgsu.teamplatform.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通知接口。
 */
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 查询通知列表。
     */
    @GetMapping("/list")
    public Result<List<Notification>> list(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.success(notificationService.list(userId));
    }

    /**
     * 标记通知已读。
     */
    @PostMapping("/{id}/read")
    public Result<Void> read(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                             @PathVariable("id") Long id) {
        notificationService.markRead(userId, id);
        return Result.success(null);
    }

    /**
     * 删除通知。
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                               @PathVariable("id") Long id) {
        notificationService.delete(userId, id);
        return Result.success(null);
    }
}
