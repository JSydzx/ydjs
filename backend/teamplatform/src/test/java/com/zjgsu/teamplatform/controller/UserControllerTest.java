package com.zjgsu.teamplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.dto.LoginRequest;
import com.zjgsu.teamplatform.dto.LoginResponse;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

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

        Mockito.when(userService.register(any(RegisterRequest.class))).thenReturn(buildUserVO());

        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.username").value("alice"));
    }

    /**
     * 登录接口返回 token 和用户信息。
     */
    @Test
    void login_returnsTokenAndUser() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("alice");
        request.setPassword("secret123");

        LoginResponse response = new LoginResponse();
        response.setToken("token-1");
        response.setUser(buildUserVO());
        Mockito.when(userService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.token").value("token-1"))
            .andExpect(jsonPath("$.data.user.username").value("alice"));
    }

    /**
     * 获取资料接口从鉴权属性读取用户 ID。
     */
    @Test
    void profile_returnsSuccessResult() throws Exception {
        Mockito.when(userService.getProfile(1L)).thenReturn(buildUserVO());

        mockMvc.perform(get("/api/user/profile")
                .requestAttr(Constants.CURRENT_USER_ID, 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
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
        request.setMajor("软件工程");
        request.setGrade("大三");
        request.setSkills("Java, Vue");
        request.setBio("喜欢做项目");

        UserVO userVO = buildUserVO();
        userVO.setNickname("Alice2");
        userVO.setMajor("软件工程");
        Mockito.when(userService.updateProfile(eq(1L), any(UserProfileUpdateRequest.class))).thenReturn(userVO);

        mockMvc.perform(put("/api/user/profile")
                .requestAttr(Constants.CURRENT_USER_ID, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.nickname").value("Alice2"))
            .andExpect(jsonPath("$.data.major").value("软件工程"));
    }

    /**
     * 注册参数校验失败时不进入服务层。
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
