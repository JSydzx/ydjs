package com.zjgsu.teamplatform.mapper;

import com.zjgsu.teamplatform.entity.Team;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 团队数据访问层。
 */
@Mapper
public interface TeamMapper {

    /**
     * 新增团队。
     */
    @Insert("INSERT INTO team(name, description, tag, creator_id, max_members, current_members, status) VALUES(#{name}, #{description}, #{tag}, #{creatorId}, #{maxMembers}, #{currentMembers}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Team team);

    /**
     * 根据主键查询团队。
     */
    @Select("SELECT id, name, description, tag, creator_id AS creatorId, max_members AS maxMembers, current_members AS currentMembers, status, created_at AS createdAt, updated_at AS updatedAt FROM team WHERE id = #{id}")
    Team findById(@Param("id") Long id);

    /**
     * 查询全部活跃团队。
     */
    @Select("SELECT id, name, description, tag, creator_id AS creatorId, max_members AS maxMembers, current_members AS currentMembers, status, created_at AS createdAt, updated_at AS updatedAt FROM team WHERE status = 'ACTIVE' ORDER BY id DESC")
    List<Team> findAllActive();

    /**
     * 查询我创建的团队。
     */
    @Select("SELECT id, name, description, tag, creator_id AS creatorId, max_members AS maxMembers, current_members AS currentMembers, status, created_at AS createdAt, updated_at AS updatedAt FROM team WHERE creator_id = #{creatorId} ORDER BY id DESC")
    List<Team> findByCreatorId(@Param("creatorId") Long creatorId);

    /**
     * 更新团队信息。
     */
    @Update("UPDATE team SET name = #{name}, description = #{description}, tag = #{tag}, max_members = #{maxMembers}, status = #{status} WHERE id = #{id}")
    int update(Team team);

    /**
     * 更新当前成员数量。
     */
    @Update("UPDATE team SET current_members = #{currentMembers} WHERE id = #{teamId}")
    int updateCurrentMembers(@Param("teamId") Long teamId, @Param("currentMembers") Integer currentMembers);

    /**
     * 删除团队。
     */
    @Delete("DELETE FROM team WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
