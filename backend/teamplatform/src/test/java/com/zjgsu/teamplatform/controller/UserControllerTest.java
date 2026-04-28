package com.zjgsu.teamplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.dto.LoginRequest;
import com.zjgsu.teamplatform.dto.RegisterRequest;
import com.zjgsu.teamplatform.dto.UserProfileUpdateRequest;
import com.zjgsu.teamplatform.service.UserService;
import com.zjgsu.teamplatform.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ContextConfiguration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 用户接口单元测试。
 */
@WebMvcTest(UserController.class)
@ContextConfiguration(classes = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    /**
     * 注册接口返回成功结构。
     */
    @Test
    void register_returnsSuccessResult() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setNickname("Alice");
        request.setPassword("secret123");
        request.setEmail("alice@example.com");

        UserVO userVO = buildUserVO();
        Mockito.when(userService.register(any(RegisterRequest.class))).thenReturn(userVO);

        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("成功"))
            .andExpect(jsonPath("$.data.username").value("alice"));
    }

    /**
     * 登录接口返回成功结构。
     */
    @Test
    void login_returnsSuccessResult() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("secret123");

        UserVO userVO = buildUserVO();
        Mockito.when(userService.login(any(LoginRequest.class))).thenReturn(userVO);

        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("成功"))
            .andExpect(jsonPath("$.data.username").value("alice"));
    }

    /**
     * 获取资料接口返回成功结构。
     */
    @Test
    void profile_returnsSuccessResult() throws Exception {
        UserVO userVO = buildUserVO();
        Mockito.when(userService.getProfile(1L)).thenReturn(userVO);

        mockMvc.perform(get("/api/user/profile")
                .header(Constants.HEADER_USER_ID, "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("成功"))
            .andExpect(jsonPath("$.data.username").value("alice"));
    }

    /**
     * 更新资料接口返回成功结构。
     */
    @Test
    void updateProfile_returnsSuccessResult() throws Exception {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setNickname("Alice2");
        request.setEmail("alice2@example.com");
        request.setAvatar("https://example.com/avatar.png");

        UserVO userVO = buildUserVO();
        userVO.setNickname("Alice2");
        userVO.setEmail("alice2@example.com");
        userVO.setAvatar("https://example.com/avatar.png");
        Mockito.when(userService.updateProfile(eq(1L), any(UserProfileUpdateRequest.class))).thenReturn(userVO);

        mockMvc.perform(put("/api/user/profile")
                .header(Constants.HEADER_USER_ID, "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("成功"))
            .andExpect(jsonPath("$.data.nickname").value("Alice2"));
    }

    /**
     * 注册参数校验失败时返回 400。
     */
    @Test
    void register_whenInvalidRequest_returnsBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("");
        request.setNickname("Alice");
        request.setPassword("secret123");
        request.setEmail("alice@example.com");

        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    /**
     * 构造用户视图对象。
     */
    private UserVO buildUserVO() {
        UserVO userVO = new UserVO();
        userVO.setId(1L);
        userVO.setUsername("alice");
        userVO.setNickname("Alice");
        userVO.setEmail("alice@example.com");
        userVO.setAvatar(null);
        return userVO;
    }
}
