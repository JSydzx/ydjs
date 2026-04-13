package com.zjgsu.teamplatform.service;

import com.zjgsu.teamplatform.entity.Notification;

import java.util.List;

/**
 * 通知服务。
 */
public interface NotificationService {

    /**
     * 查询通知列表。
     */
    List<Notification> list(Long userId);

    /**
     * 标记已读。
     */
    void markRead(Long userId, Long notificationId);

    /**
     * 删除通知。
     */
    void delete(Long userId, Long notificationId);

    /**
     * 发送系统通知。
     */
    void push(Long userId, String type, String message);
}
