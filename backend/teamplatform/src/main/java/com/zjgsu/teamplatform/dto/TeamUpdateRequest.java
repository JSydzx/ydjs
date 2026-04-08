package com.zjgsu.teamplatform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新团队请求。
 */
@Data
public class TeamUpdateRequest {
    @Size(max = 100, message = "团队名称长度不能超过100")
    private String name;

    @Size(max = 1000, message = "团队描述长度不能超过1000")
    private String description;

    @Size(max = 255, message = "标签长度不能超过255")
    private String tag;

    @Min(value = 1, message = "团队人数上限至少为1")
    @Max(value = 200, message = "团队人数上限不能超过200")
    private Integer maxMembers;

    @Size(max = 20, message = "状态长度不能超过20")
    private String status;
}
