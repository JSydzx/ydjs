package com.zjgsu.teamplatform.mapper;

import com.zjgsu.teamplatform.entity.ChatConversation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 私聊会话数据访问层。
 */
@Mapper
public interface ChatConversationMapper {

    /**
     * 新增会话。
     */
    @Insert("INSERT INTO chat_conversation(user1_id, user2_id, last_message_id, last_message_at) " +
            "VALUES(#{user1Id}, #{user2Id}, #{lastMessageId}, #{lastMessageAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatConversation conversation);

    /**
     * 查询会话（按固定用户顺序）。
     */
    @Select("SELECT id, user1_id AS user1Id, user2_id AS user2Id, last_message_id AS lastMessageId, " +
            "last_message_at AS lastMessageAt, created_at AS createdAt, updated_at AS updatedAt " +
            "FROM chat_conversation WHERE user1_id = #{user1Id} AND user2_id = #{user2Id}")
    ChatConversation findByUserPair(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    /**
     * 查询用户所有会话。
     */
    @Select("SELECT id, user1_id AS user1Id, user2_id AS user2Id, last_message_id AS lastMessageId, " +
            "last_message_at AS lastMessageAt, created_at AS createdAt, updated_at AS updatedAt " +
            "FROM chat_conversation WHERE user1_id = #{userId} OR user2_id = #{userId} " +
            "ORDER BY last_message_at DESC, id DESC")
    List<ChatConversation> findByUserId(@Param("userId") Long userId);

    /**
     * 更新会话的最后消息信息。
     */
    @Update("UPDATE chat_conversation SET last_message_id = #{lastMessageId}, last_message_at = #{lastMessageAt} " +
            "WHERE id = #{id}")
    int updateLastMessage(@Param("id") Long id,
                          @Param("lastMessageId") Long lastMessageId,
                          @Param("lastMessageAt") LocalDateTime lastMessageAt);
}
