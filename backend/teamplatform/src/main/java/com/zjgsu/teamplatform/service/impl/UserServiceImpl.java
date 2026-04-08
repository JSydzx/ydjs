package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.common.ErrorCode;
import com.zjgsu.teamplatform.dto.LoginRequest;
import com.zjgsu.teamplatform.dto.RegisterRequest;
import com.zjgsu.teamplatform.dto.UserProfileUpdateRequest;
import com.zjgsu.teamplatform.entity.User;
import com.zjgsu.teamplatform.exception.BizException;
import com.zjgsu.teamplatform.mapper.UserMapper;
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
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册。
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
     * 用户登录。
     */
    @Override
    public UserVO login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }
        return toVO(user);
    }

    /**
     * 获取用户资料。
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
     * 更新用户资料。
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
        userMapper.updateProfile(user);
        return toVO(userMapper.findById(userId));
    }

    /**
     * 实体转视图对象。
     */
    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        return vo;
    }
}
