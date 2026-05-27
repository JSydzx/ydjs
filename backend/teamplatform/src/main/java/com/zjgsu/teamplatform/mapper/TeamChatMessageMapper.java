package com.zjgsu.teamplatform.mapper;

import com.zjgsu.teamplatform.entity.TeamChatMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 团队聊天消息数据访问层。
 */
@Mapper
public interface TeamChatMessageMapper {

    /**
     * 新增团队消息。
     */
    @Insert("INSERT INTO team_chat_message(team_id, sender_id, content) VALUES(#{teamId}, #{senderId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TeamChatMessage message);

    /**
     * 查询团队消息列表。
     */
    @Select("SELECT id, team_id AS teamId, sender_id AS senderId, content, created_at AS createdAt " +
            "FROM team_chat_message WHERE team_id = #{teamId} ORDER BY id DESC LIMIT #{limit} OFFSET #{offset}")
    List<TeamChatMessage> findByTeam(@Param("teamId") Long teamId,
                                     @Param("limit") Integer limit,
                                     @Param("offset") Integer offset);

    /**
     * 查询团队最后一条消息。
     */
    @Select("SELECT id, team_id AS teamId, sender_id AS senderId, content, created_at AS createdAt " +
            "FROM team_chat_message WHERE team_id = #{teamId} ORDER BY id DESC LIMIT 1")
    TeamChatMessage findLastByTeam(@Param("teamId") Long teamId);

    /**
     * 统计未读消息数量。
     */
    @Select("SELECT COUNT(1) FROM team_chat_message " +
            "WHERE team_id = #{teamId} AND sender_id <> #{userId} " +
            "AND (#{lastReadAt} IS NULL OR created_at > #{lastReadAt})")
    Integer countUnreadSince(@Param("teamId") Long teamId,
                             @Param("userId") Long userId,
                             @Param("lastReadAt") java.time.LocalDateTime lastReadAt);
}
