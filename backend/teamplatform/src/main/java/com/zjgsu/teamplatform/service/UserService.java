package com.zjgsu.teamplatform.service;

import com.zjgsu.teamplatform.dto.LoginRequest;
import com.zjgsu.teamplatform.dto.RegisterRequest;
import com.zjgsu.teamplatform.dto.UserProfileUpdateRequest;
import com.zjgsu.teamplatform.vo.UserVO;

/**
 * 用户服务。
 */
public interface UserService {

    /**
     * 用户注册。
     */
    UserVO register(RegisterRequest request);

    /**
     * 用户登录。
     */
    UserVO login(LoginRequest request);

    /**
     * 获取用户资料。
     */
    UserVO getProfile(Long userId);

    /**
     * 更新用户资料。
     */
    UserVO updateProfile(Long userId, UserProfileUpdateRequest request);
}
