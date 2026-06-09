package com.zjgsu.teamplatform.dto;

import com.zjgsu.teamplatform.vo.UserVO;
import lombok.Data;

/**
 * 登录成功响应，包含后端签发的访问令牌和用户信息。
 */
@Data
public class LoginResponse {
    private String token;
    private UserVO user;
}
