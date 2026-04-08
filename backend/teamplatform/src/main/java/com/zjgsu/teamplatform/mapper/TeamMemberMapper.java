package com.zjgsu.teamplatform.mapper;

import com.zjgsu.teamplatform.entity.TeamMember;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 团队成员数据访问层。
 */
@Mapper
public interface TeamMemberMapper {

    /**
     * 新增团队成员。
     */
    @Insert("INSERT INTO team_member(team_id, user_id, role) VALUES(#{teamId}, #{userId}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TeamMember teamMember);

    /**
     * 查询团队指定成员。
     */
    @Select("SELECT id, team_id AS teamId, user_id AS userId, role, joined_at AS joinedAt FROM team_member WHERE team_id = #{teamId} AND user_id = #{userId}")
    TeamMember findByTeamAndUser(@Param("teamId") Long teamId, @Param("userId") Long userId);

    /**
     * 查询团队全部成员。
     */
    @Select("SELECT id, team_id AS teamId, user_id AS userId, role, joined_at AS joinedAt FROM team_member WHERE team_id = #{teamId} ORDER BY id ASC")
    List<TeamMember> findByTeamId(@Param("teamId") Long teamId);

    /**
     * 统计团队成员数量。
     */
    @Select("SELECT COUNT(1) FROM team_member WHERE team_id = #{teamId}")
    Integer countByTeamId(@Param("teamId") Long teamId);

    /**
     * 删除团队指定成员。
     */
    @Delete("DELETE FROM team_member WHERE team_id = #{teamId} AND user_id = #{userId}")
    int deleteByTeamAndUser(@Param("teamId") Long teamId, @Param("userId") Long userId);

    /**
     * 删除团队全部成员。
     */
    @Delete("DELETE FROM team_member WHERE team_id = #{teamId}")
    int deleteByTeamId(@Param("teamId") Long teamId);
}
