package com.zjgsu.teamplatform.controller;

import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.common.Result;
import com.zjgsu.teamplatform.dto.LoginRequest;
import com.zjgsu.teamplatform.dto.RegisterRequest;
import com.zjgsu.teamplatform.dto.UserProfileUpdateRequest;
import com.zjgsu.teamplatform.service.UserService;
import com.zjgsu.teamplatform.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口。
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册。
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(userService.register(request));
    }

    /**
     * 用户登录。
     */
    @PostMapping("/login")
    public Result<UserVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(userService.login(request));
    }

    /**
     * 获取用户资料。
     */
    @GetMapping("/profile")
    public Result<UserVO> profile(@RequestHeader(Constants.HEADER_USER_ID) Long userId) {
        return Result.success(userService.getProfile(userId));
    }

    /**
     * 更新用户资料。
     */
    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@RequestHeader(Constants.HEADER_USER_ID) Long userId,
                                        @Valid @RequestBody UserProfileUpdateRequest request) {
        return Result.success(userService.updateProfile(userId, request));
    }
}
