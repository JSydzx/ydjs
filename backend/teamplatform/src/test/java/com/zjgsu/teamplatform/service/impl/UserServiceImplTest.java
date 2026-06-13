package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.dto.LoginRequest;
import com.zjgsu.teamplatform.dto.RegisterRequest;
import com.zjgsu.teamplatform.dto.UserProfileUpdateRequest;
import com.zjgsu.teamplatform.entity.User;
import com.zjgsu.teamplatform.exception.BizException;
import com.zjgsu.teamplatform.mapper.UserMapper;
import com.zjgsu.teamplatform.security.TokenService;
import com.zjgsu.teamplatform.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 用户服务单元测试，覆盖注册、登录、资料读取和资料更新的核心分支。
 */
class UserServiceImplTest {

    private UserMapper userMapper;
    private TokenService tokenService;
    private UserServiceImpl userService;

    /**
     * 每个用例使用独立 Mock，避免状态互相影响。
     */
    @BeforeEach
    void setUp() {
        userMapper = mock(UserMapper.class);
        tokenService = mock(TokenService.class);
        userService = new UserServiceImpl(userMapper, tokenService);
    }

    /**
     * 新用户注册时会写入 BCrypt 密码并返回脱敏后的视图对象。
     */
    @Test
    void register_whenUsernameAvailable_insertsUserAndReturnsVO() {
        RegisterRequest request = buildRegisterRequest();
        when(userMapper.findByUsername("alice")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User insertedUser = invocation.getArgument(0);
            insertedUser.setId(1L);
            return 1;
        });
        when(userMapper.findById(1L)).thenReturn(buildUser(1L));

        UserVO userVO = userService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(captor.capture());
        assertThat(new BCryptPasswordEncoder().matches("secret123", captor.getValue().getPassword())).isTrue();
        assertThat(userVO.getUsername()).isEqualTo("alice");
        assertThat(userVO.getEmail()).isEqualTo("alice@example.com");
    }

    /**
     * 已存在用户名不能重复注册。
     */
    @Test
    void register_whenUsernameExists_throwsBizException() {
        when(userMapper.findByUsername("alice")).thenReturn(buildUser(1L));

        assertThatThrownBy(() -> userService.register(buildRegisterRequest()))
            .isInstanceOf(BizException.class);
        verify(userMapper, never()).insert(any(User.class));
    }

    /**
     * 登录成功后返回令牌和用户基本资料。
     */
    @Test
    void login_whenPasswordMatches_returnsTokenAndUser() {
        User user = buildUser(1L);
        user.setPassword(new BCryptPasswordEncoder().encode("secret123"));
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("secret123");
        when(userMapper.findByUsername("alice")).thenReturn(user);
        when(tokenService.generateToken(1L)).thenReturn("token-1");

        assertThat(userService.login(request).getToken()).isEqualTo("token-1");
        assertThat(userService.login(request).getUser().getUsername()).isEqualTo("alice");
    }

    /**
     * 密码错误时拒绝登录并且不会签发令牌。
     */
    @Test
    void login_whenPasswordInvalid_throwsBizException() {
        User user = buildUser(1L);
        user.setPassword(new BCryptPasswordEncoder().encode("right-password"));
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("wrong-password");
        when(userMapper.findByUsername("alice")).thenReturn(user);

        assertThatThrownBy(() -> userService.login(request))
            .isInstanceOf(BizException.class);
        verify(tokenService, never()).generateToken(1L);
    }

    /**
     * 用户存在时可以读取个人资料。
     */
    @Test
    void getProfile_whenUserExists_returnsVO() {
        when(userMapper.findById(1L)).thenReturn(buildUser(1L));

        assertThat(userService.getProfile(1L).getNickname()).isEqualTo("Alice");
    }

    /**
     * 更新资料只覆盖请求中提供的字段，空字段保持原值。
     */
    @Test
    void updateProfile_whenPartialRequest_updatesProvidedFields() {
        User existingUser = buildUser(1L);
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setNickname("Alice New");
        request.setSkills("Java,Spring");
        when(userMapper.findById(1L)).thenReturn(existingUser);

        UserVO userVO = userService.updateProfile(1L, request);

        verify(userMapper).updateProfile(existingUser);
        assertThat(userVO.getNickname()).isEqualTo("Alice New");
        assertThat(userVO.getEmail()).isEqualTo("alice@example.com");
        assertThat(userVO.getSkills()).isEqualTo("Java,Spring");
    }

    /**
     * 构造注册请求。
     */
    private RegisterRequest buildRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setNickname("Alice");
        request.setPassword("secret123");
        request.setEmail("alice@example.com");
        return request;
    }

    /**
     * 构造用户实体。
     */
    private User buildUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setUsername("alice");
        user.setNickname("Alice");
        user.setPassword("encoded-password");
        user.setEmail("alice@example.com");
        user.setMajor("Software");
        user.setGrade("Junior");
        user.setBio("Builder");
        return user;
    }
}
