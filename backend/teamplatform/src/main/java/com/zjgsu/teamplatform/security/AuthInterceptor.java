package com.zjgsu.teamplatform.security;

import com.zjgsu.teamplatform.common.ErrorCode;
import com.zjgsu.teamplatform.common.Result;
import com.zjgsu.teamplatform.exception.BizException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * API 鉴权拦截器，统一从 Bearer Token 解析当前用户。
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    public static final String CURRENT_USER_ID = "currentUserId";

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    /**
     * 公开接口直接放行，受保护接口写入当前用户 ID。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isPublicRequest(request)) {
            return true;
        }
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            writeUnauthorized(response);
            return false;
        }
        try {
            Long userId = tokenService.parseUserId(authorization.substring(7));
            request.setAttribute(CURRENT_USER_ID, userId);
            return true;
        } catch (BizException | IllegalArgumentException e) {
            writeUnauthorized(response);
            return false;
        }
    }

    private boolean isPublicRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (!path.startsWith("/api/")) {
            return true;
        }
        if (path.equals("/api/user/register") || path.equals("/api/user/login") || path.equals("/api/health")) {
            return true;
        }
        if ("GET".equalsIgnoreCase(method) && path.equals("/api/team/list")) {
            return true;
        }
        if ("GET".equalsIgnoreCase(method) && path.matches("/api/team/\\d+")) {
            return true;
        }
        return "GET".equalsIgnoreCase(method) && path.matches("/api/team/\\d+/members");
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Result.failure(ErrorCode.UNAUTHORIZED, "登录已过期，请重新登录")));
    }
}
