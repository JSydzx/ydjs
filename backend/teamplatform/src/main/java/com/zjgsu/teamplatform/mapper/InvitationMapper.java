package com.zjgsu.teamplatform.mapper;

import com.zjgsu.teamplatform.entity.Invitation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 邀请数据访问层。
 */
@Mapper
public interface InvitationMapper {

    /**
     * 新增邀请。
     */
    @Insert("INSERT INTO invitation(invite_id, team_id, user_id, status) VALUES(#{inviteId}, #{teamId}, #{userId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Invitation invitation);

    /**
     * 查询我的邀请。
     */
    @Select("SELECT id, invite_id AS inviteId, team_id AS teamId, user_id, status, created_at AS createdAt FROM invitation WHERE user_id = #{userId} ORDER BY id DESC")
    List<Invitation> findByUserId(@Param("userId") Long userId);

    /**
     * 查询团队邀请。
     */
    @Select("SELECT id, invite_id AS inviteId, team_id AS teamId, user_id, status, created_at AS createdAt FROM invitation WHERE team_id = #{teamId} ORDER BY id DESC")
    List<Invitation> findByTeamId(@Param("teamId") Long teamId);

    /**
     * 查询单条邀请。
     */
    @Select("SELECT id, invite_id AS inviteId, team_id AS teamId, user_id, status, created_at AS createdAt FROM invitation WHERE id = #{id}")
    Invitation findById(@Param("id") Long id);

    /**
     * 查询重复邀请。
     */
    @Select("SELECT id, invite_id AS inviteId, team_id AS teamId, user_id, status, created_at AS createdAt FROM invitation WHERE team_id = #{teamId} AND user_id = #{userId} AND status = 'PENDING' LIMIT 1")
    Invitation findPendingByTeamAndUser(@Param("teamId") Long teamId, @Param("userId") Long userId);

    /**
     * 更新邀请状态。
     */
    @Update("UPDATE invitation SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
