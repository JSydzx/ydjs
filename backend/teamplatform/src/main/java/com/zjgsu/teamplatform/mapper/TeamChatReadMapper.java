package com.zjgsu.teamplatform.mapper;

import com.zjgsu.teamplatform.entity.TeamChatRead;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * 团队聊天已读状态数据访问层。
 */
@Mapper
public interface TeamChatReadMapper {

    /**
     * 查询用户在团队中的已读状态。
     */
    @Select("SELECT id, team_id AS teamId, user_id AS userId, last_read_at AS lastReadAt, updated_at AS updatedAt " +
            "FROM team_chat_read WHERE team_id = #{teamId} AND user_id = #{userId}")
    TeamChatRead findByTeamAndUser(@Param("teamId") Long teamId, @Param("userId") Long userId);

    /**
     * 新增已读状态。
     */
    @Insert("INSERT INTO team_chat_read(team_id, user_id, last_read_at) VALUES(#{teamId}, #{userId}, #{lastReadAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TeamChatRead read);

    /**
     * 更新已读时间。
     */
    @Update("UPDATE team_chat_read SET last_read_at = #{lastReadAt} WHERE team_id = #{teamId} AND user_id = #{userId}")
    int updateLastRead(@Param("teamId") Long teamId,
                       @Param("userId") Long userId,
                       @Param("lastReadAt") LocalDateTime lastReadAt);
}
