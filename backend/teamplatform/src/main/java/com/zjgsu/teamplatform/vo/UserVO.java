package com.zjgsu.teamplatform.vo;

import lombok.Data;

/**
 * 用户视图对象。
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
}
