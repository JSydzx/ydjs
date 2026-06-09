package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.common.ErrorCode;
import com.zjgsu.teamplatform.dto.LoginRequest;
import com.zjgsu.teamplatform.dto.LoginResponse;
import com.zjgsu.teamplatform.dto.RegisterRequest;
import com.zjgsu.teamplatform.dto.UserProfileUpdateRequest;
import com.zjgsu.teamplatform.entity.User;
import com.zjgsu.teamplatform.exception.BizException;
import com.zjgsu.teamplatform.mapper.UserMapper;
import com.zjgsu.teamplatform.security.TokenService;
import com.zjgsu.teamplatform.service.UserService;
import com.zjgsu.teamplatform.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册，密码入库前使用 BCrypt 加密。
     */
    @Override
    public UserVO register(RegisterRequest request) {
        User exist = userMapper.findByUsername(request.getUsername());
        if (exist != null) {
            throw new BizException(ErrorCode.CONFLICT, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setNickname(request.getNickname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setAvatar(null);
        userMapper.insert(user);
        return toVO(userMapper.findById(user.getId()));
    }

    /**
     * 用户登录，校验密码后返回后端签发的 Token。
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }
        LoginResponse response = new LoginResponse();
        response.setToken(tokenService.generateToken(user.getId()));
        response.setUser(toVO(user));
        return response;
    }

    /**
     * 获取当前用户资料。
     */
    @Override
    public UserVO getProfile(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return toVO(user);
    }

    /**
     * 更新用户资料，空字段保持原值。
     */
    @Override
    public UserVO updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getMajor() != null) {
            user.setMajor(request.getMajor());
        }
        if (request.getGrade() != null) {
            user.setGrade(request.getGrade());
        }
        if (request.getSkills() != null) {
            user.setSkills(request.getSkills());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        userMapper.updateProfile(user);
        return toVO(userMapper.findById(userId));
    }

    /**
     * 实体转用户视图对象，避免向前端暴露密码。
     */
    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setMajor(user.getMajor());
        vo.setGrade(user.getGrade());
        vo.setSkills(user.getSkills());
        vo.setBio(user.getBio());
        return vo;
    }
}
