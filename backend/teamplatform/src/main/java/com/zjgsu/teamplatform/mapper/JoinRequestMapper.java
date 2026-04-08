package com.zjgsu.teamplatform.mapper;

import com.zjgsu.teamplatform.entity.JoinRequest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 加入请求数据访问层。
 */
@Mapper
public interface JoinRequestMapper {

    /**
     * 新增加入请求。
     */
    @Insert("INSERT INTO join_request(team_id, user_id, status, reason) VALUES(#{teamId}, #{userId}, #{status}, #{reason})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(JoinRequest request);

    /**
     * 查询用户对团队的请求。
     */
    @Select("SELECT id, team_id AS teamId, user_id AS userId, status, requested_at AS requestedAt, processed_at AS processedAt, reason FROM join_request WHERE team_id = #{teamId} AND user_id = #{userId}")
    JoinRequest findByTeamAndUser(@Param("teamId") Long teamId, @Param("userId") Long userId);

    /**
     * 查询我的加入请求。
     */
    @Select("SELECT id, team_id AS teamId, user_id AS userId, status, requested_at AS requestedAt, processed_at AS processedAt, reason FROM join_request WHERE user_id = #{userId} ORDER BY id DESC")
    List<JoinRequest> findByUserId(@Param("userId") Long userId);

    /**
     * 查询团队加入请求。
     */
    @Select("SELECT id, team_id AS teamId, user_id AS userId, status, requested_at AS requestedAt, processed_at AS processedAt, reason FROM join_request WHERE team_id = #{teamId} ORDER BY id DESC")
    List<JoinRequest> findByTeamId(@Param("teamId") Long teamId);

    /**
     * 根据主键查询。
     */
    @Select("SELECT id, team_id AS teamId, user_id AS userId, status, requested_at AS requestedAt, processed_at AS processedAt, reason FROM join_request WHERE id = #{id}")
    JoinRequest findById(@Param("id") Long id);

    /**
     * 更新请求状态。
     */
    @Update("UPDATE join_request SET status = #{status}, processed_at = #{processedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("processedAt") LocalDateTime processedAt);
}
