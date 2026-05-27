package com.zjgsu.teamplatform.mapper;

import com.zjgsu.teamplatform.entity.ChatMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 私聊消息数据访问层。
 */
@Mapper
public interface ChatMessageMapper {

    /**
     * 新增消息。
     */
    @Insert("INSERT INTO chat_message(conversation_id, sender_id, recipient_id, content, is_read) " +
            "VALUES(#{conversationId}, #{senderId}, #{recipientId}, #{content}, #{isRead})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatMessage message);

    /**
     * 根据主键查询消息。
     */
    @Select("SELECT id, conversation_id AS conversationId, sender_id AS senderId, recipient_id AS recipientId, " +
            "content, is_read AS isRead, created_at AS createdAt FROM chat_message WHERE id = #{id}")
    ChatMessage findById(@Param("id") Long id);

    /**
     * 查询会话消息列表。
     */
    @Select("SELECT id, conversation_id AS conversationId, sender_id AS senderId, recipient_id AS recipientId, " +
            "content, is_read AS isRead, created_at AS createdAt FROM chat_message " +
            "WHERE conversation_id = #{conversationId} ORDER BY id DESC LIMIT #{limit} OFFSET #{offset}")
    List<ChatMessage> findByConversation(@Param("conversationId") Long conversationId,
                                         @Param("limit") Integer limit,
                                         @Param("offset") Integer offset);

    /**
     * 统计未读消息数量。
     */
    @Select("SELECT COUNT(1) FROM chat_message WHERE sender_id = #{senderId} AND recipient_id = #{recipientId} " +
            "AND is_read = FALSE")
    Integer countUnread(@Param("senderId") Long senderId, @Param("recipientId") Long recipientId);

    /**
     * 批量标记消息已读。
     */
    @Update("UPDATE chat_message SET is_read = TRUE WHERE sender_id = #{senderId} AND recipient_id = #{recipientId} " +
            "AND is_read = FALSE")
    int markReadByRecipient(@Param("senderId") Long senderId, @Param("recipientId") Long recipientId);
}
