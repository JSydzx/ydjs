package com.zjgsu.teamplatform.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        log.info("login: {}", request);

        Map<String, Object> response = new HashMap<>();
        
        // test
        if ("admin".equals(username) && "123456".equals(password)) {
            response.put("code", 200);
            response.put("message", "登录成功");

            Map<String, Object> user = new HashMap<>();
            user.put("id", 1);
            user.put("username", "admin");
            user.put("nickname", "管理员");
            user.put("avatar", "https://example.com/avatar.png");

            response.put("data", user);
        } else {
            response.put("code", 401);
            response.put("message", "用户名或密码错误");
            response.put("data", null);
        }

        return response;
    }

}
