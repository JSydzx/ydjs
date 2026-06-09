package com.zjgsu.teamplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户资料更新请求。
 */
@Data
public class UserProfileUpdateRequest {
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(max = 255, message = "头像地址长度不能超过255")
    private String avatar;

    @Size(max = 100, message = "专业长度不能超过100")
    private String major;

    @Size(max = 50, message = "年级长度不能超过50")
    private String grade;

    @Size(max = 255, message = "技能标签长度不能超过255")
    private String skills;

    @Size(max = 500, message = "个人简介长度不能超过500")
    private String bio;
}
