package com.zjgsu.teamplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zjgsu.teamplatform.common.Constants;
import com.zjgsu.teamplatform.dto.TeamCreateRequest;
import com.zjgsu.teamplatform.dto.TeamMemberAddRequest;
import com.zjgsu.teamplatform.service.TeamService;
import com.zjgsu.teamplatform.vo.TeamVO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 团队接口测试，使用 MockMvc 覆盖主要 HTTP 入口。
 */
@WebMvcTest(TeamController.class)
@ContextConfiguration(classes = TeamController.class)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamService teamService;

    /**
     * 创建团队接口返回统一成功结构。
     */
    @Test
    void create_returnsSuccessResult() throws Exception {
        TeamCreateRequest request = new TeamCreateRequest();
        request.setName("算法竞赛队");
        request.setDescription("准备比赛");
        request.setTag("算法");
        request.setMaxMembers(5);
        Mockito.when(teamService.create(eq(1L), any(TeamCreateRequest.class))).thenReturn(buildTeamVO());

        mockMvc.perform(post("/api/team/create")
                .requestAttr(Constants.CURRENT_USER_ID, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.name").value("算法竞赛队"));
    }

    /**
     * 团队列表接口透传筛选参数。
     */
    @Test
    void list_returnsTeams() throws Exception {
        Mockito.when(teamService.list("算法", "比赛", true)).thenReturn(List.of(buildTeamVO()));

        mockMvc.perform(get("/api/team/list")
                .param("keyword", "算法")
                .param("tag", "比赛")
                .param("availableOnly", "true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].creatorName").value("Alice"));
    }

    /**
     * 团队详情接口返回指定团队。
     */
    @Test
    void detail_returnsTeam() throws Exception {
        Mockito.when(teamService.detail(10L)).thenReturn(buildTeamVO());

        mockMvc.perform(get("/api/team/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(10));
    }

    /**
     * 添加成员接口调用服务层并返回成功。
     */
    @Test
    void addMember_returnsSuccessResult() throws Exception {
        TeamMemberAddRequest request = new TeamMemberAddRequest();
        request.setUserId(2L);

        mockMvc.perform(post("/api/team/10/members")
                .requestAttr(Constants.CURRENT_USER_ID, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(teamService).addMember(eq(1L), eq(10L), any(TeamMemberAddRequest.class));
    }

    /**
     * 删除团队接口会调用服务层删除逻辑。
     */
    @Test
    void deleteTeam_returnsSuccessResult() throws Exception {
        mockMvc.perform(delete("/api/team/10")
                .requestAttr(Constants.CURRENT_USER_ID, 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));

        verify(teamService).delete(1L, 10L);
    }

    /**
     * 创建团队参数不合法时不会进入服务层。
     */
    @Test
    void create_whenInvalidRequest_returnsBadRequest() throws Exception {
        TeamCreateRequest request = new TeamCreateRequest();
        request.setName("");

        mockMvc.perform(post("/api/team/create")
                .requestAttr(Constants.CURRENT_USER_ID, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(teamService);
    }

    /**
     * 构造团队视图对象。
     */
    private TeamVO buildTeamVO() {
        TeamVO teamVO = new TeamVO();
        teamVO.setId(10L);
        teamVO.setName("算法竞赛队");
        teamVO.setCreatorId(1L);
        teamVO.setCreatorName("Alice");
        teamVO.setCurrentMembers(1);
        teamVO.setMaxMembers(5);
        teamVO.setStatus(Constants.TEAM_STATUS_ACTIVE);
        return teamVO;
    }
}
