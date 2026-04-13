package com.zjgsu.teamplatform.mapper;

import com.zjgsu.teamplatform.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户数据访问层。
 */
@Mapper
public interface UserMapper {

    /**
     * 新增用户。
     */
    @Insert("INSERT INTO user(username, nickname, password, email, avatar) VALUES(#{username}, #{nickname}, #{password}, #{email}, #{avatar})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 根据用户名查询。
     */
    @Select("SELECT id, username, nickname, password, email, avatar, created_at AS createdAt, updated_at AS updatedAt FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * 根据主键查询。
     */
    @Select("SELECT id, username, nickname, password, email, avatar, created_at AS createdAt, updated_at AS updatedAt FROM user WHERE id = #{id}")
    User findById(@Param("id") Long id);

    /**
     * 更新用户资料。
     */
    @Update("UPDATE user SET nickname = #{nickname}, email = #{email}, avatar = #{avatar} WHERE id = #{id}")
    int updateProfile(User user);
}
