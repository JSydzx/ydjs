package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.common.ErrorCode;
import com.zjgsu.teamplatform.entity.Notification;
import com.zjgsu.teamplatform.exception.BizException;
import com.zjgsu.teamplatform.mapper.NotificationMapper;
import com.zjgsu.teamplatform.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知服务实现。
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    /**
     * 查询用户通知。
     */
    @Override
    public List<Notification> list(Long userId) {
        return notificationMapper.findByUserId(userId);
    }

    /**
     * 标记通知已读。
     */
    @Override
    public void markRead(Long userId, Long notificationId) {
        int affected = notificationMapper.markRead(notificationId, userId);
        if (affected == 0) {
            throw new BizException(ErrorCode.NOT_FOUND, "通知不存在");
        }
    }

    /**
     * 删除通知。
     */
    @Override
    public void delete(Long userId, Long notificationId) {
        int affected = notificationMapper.deleteByIdAndUser(notificationId, userId);
        if (affected == 0) {
            throw new BizException(ErrorCode.NOT_FOUND, "通知不存在");
        }
    }

    /**
     * 推送通知。
     */
    @Override
    public void push(Long userId, String type, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setIsRead(false);
        notificationMapper.insert(notification);
    }
}
