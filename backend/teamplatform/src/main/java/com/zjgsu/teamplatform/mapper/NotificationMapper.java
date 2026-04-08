package com.zjgsu.teamplatform.mapper;

import com.zjgsu.teamplatform.entity.Notification;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 通知数据访问层。
 */
@Mapper
public interface NotificationMapper {

    /**
     * 新增通知。
     */
    @Insert("INSERT INTO notification(user_id, message, type, is_read) VALUES(#{userId}, #{message}, #{type}, #{isRead})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    /**
     * 查询用户通知。
     */
    @Select("SELECT id, user_id AS userId, message, type, is_read AS isRead, created_at AS createdAt FROM notification WHERE user_id = #{userId} ORDER BY id DESC")
    List<Notification> findByUserId(@Param("userId") Long userId);

    /**
     * 标记通知已读。
     */
    @Update("UPDATE notification SET is_read = TRUE WHERE id = #{id} AND user_id = #{userId}")
    int markRead(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 删除通知。
     */
    @Delete("DELETE FROM notification WHERE id = #{id} AND user_id = #{userId}")
    int deleteByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);
}
