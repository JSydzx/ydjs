package com.zjgsu.teamplatform.service.impl;

import com.zjgsu.teamplatform.common.ErrorCode;
import com.zjgsu.teamplatform.dto.SendTeamChatRequest;
import com.zjgsu.teamplatform.entity.Team;
import com.zjgsu.teamplatform.entity.TeamChatMessage;
import com.zjgsu.teamplatform.entity.TeamChatRead;
import com.zjgsu.teamplatform.entity.TeamMember;
import com.zjgsu.teamplatform.entity.User;
import com.zjgsu.teamplatform.exception.BizException;
import com.zjgsu.teamplatform.mapper.TeamChatMessageMapper;
import com.zjgsu.teamplatform.mapper.TeamChatReadMapper;
import com.zjgsu.teamplatform.mapper.TeamMapper;
import com.zjgsu.teamplatform.mapper.TeamMemberMapper;
import com.zjgsu.teamplatform.mapper.UserMapper;
import com.zjgsu.teamplatform.service.TeamChatService;
import com.zjgsu.teamplatform.vo.TeamChatConversationVO;
import com.zjgsu.teamplatform.vo.TeamChatMessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 团队聊天服务实现。
 */
@Service
@RequiredArgsConstructor
public class TeamChatServiceImpl implements TeamChatService {

    private final TeamChatMessageMapper teamChatMessageMapper;
    private final TeamChatReadMapper teamChatReadMapper;
    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final UserMapper userMapper;

    /**
     * 查询团队聊天列表。
     */
    @Override
    public List<TeamChatConversationVO> listTeamChats(Long userId) {
        List<TeamMember> memberships = teamMemberMapper.findByUserId(userId);
        List<TeamChatConversationVO> result = new ArrayList<>();
        for (TeamMember member : memberships) {
            Team team = teamMapper.findById(member.getTeamId());
            if (team == null) {
                continue;
            }
            TeamChatConversationVO vo = new TeamChatConversationVO();
            vo.setTeamId(team.getId());
            vo.setTeamName(team.getName());

            TeamChatMessage lastMessage = teamChatMessageMapper.findLastByTeam(team.getId());
            if (lastMessage != null) {
                vo.setLastMessage(lastMessage.getContent());
                vo.setLastMessageAt(lastMessage.getCreatedAt());
            }

            TeamChatRead read = teamChatReadMapper.findByTeamAndUser(team.getId(), userId);
            LocalDateTime lastReadAt = read == null ? null : read.getLastReadAt();
            Integer unread = teamChatMessageMapper.countUnreadSince(team.getId(), userId, lastReadAt);
            vo.setUnreadCount(unread == null ? 0 : unread);

            result.add(vo);
        }
        return result;
    }

    /**
     * 查询团队聊天消息列表。
     */
    @Override
    public List<TeamChatMessageVO> listMessages(Long userId, Long teamId, Integer page, Integer size) {
        ensureTeamMember(userId, teamId);
        int pageNumber = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 20 : size;
        int offset = (pageNumber - 1) * pageSize;
        List<TeamChatMessage> messages = teamChatMessageMapper.findByTeam(teamId, pageSize, offset);
        Collections.reverse(messages);
        return toMessageVOs(messages, buildUserCache(messages));
    }

    /**
     * 发送团队消息。
     */
    @Override
    public TeamChatMessageVO send(Long userId, Long teamId, SendTeamChatRequest request) {
        ensureTeamMember(userId, teamId);
        TeamChatMessage message = new TeamChatMessage();
        message.setTeamId(teamId);
        message.setSenderId(userId);
        message.setContent(request.getContent());
        message.setCreatedAt(LocalDateTime.now());
        teamChatMessageMapper.insert(message);

        // 发送人默认已读，避免出现自己的未读提示
        upsertRead(userId, teamId, LocalDateTime.now());

        User sender = userMapper.findById(userId);
        return toMessageVO(message, sender);
    }

    /**
     * 标记团队消息已读。
     */
    @Override
    public void markRead(Long userId, Long teamId) {
        ensureTeamMember(userId, teamId);
        upsertRead(userId, teamId, LocalDateTime.now());
    }

    /**
     * 校验团队成员身份。
     */
    private void ensureTeamMember(Long userId, Long teamId) {
        TeamMember member = teamMemberMapper.findByTeamAndUser(teamId, userId);
        if (member == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "你不是该团队成员");
        }
    }

    /**
     * 保存团队已读时间。
     */
    private void upsertRead(Long userId, Long teamId, LocalDateTime readAt) {
        TeamChatRead read = teamChatReadMapper.findByTeamAndUser(teamId, userId);
        if (read == null) {
            TeamChatRead created = new TeamChatRead();
            created.setTeamId(teamId);
            created.setUserId(userId);
            created.setLastReadAt(readAt);
            teamChatReadMapper.insert(created);
        } else {
            teamChatReadMapper.updateLastRead(teamId, userId, readAt);
        }
    }

    /**
     * 组装团队消息视图对象列表。
     */
    private List<TeamChatMessageVO> toMessageVOs(List<TeamChatMessage> messages, Map<Long, User> userCache) {
        List<TeamChatMessageVO> result = new ArrayList<>();
        for (TeamChatMessage message : messages) {
            result.add(toMessageVO(message, userCache.get(message.getSenderId())));
        }
        return result;
    }

    /**
     * 组装团队消息视图对象。
     */
    private TeamChatMessageVO toMessageVO(TeamChatMessage message, User sender) {
        TeamChatMessageVO vo = new TeamChatMessageVO();
        vo.setId(message.getId());
        vo.setTeamId(message.getTeamId());
        vo.setSenderId(message.getSenderId());
        vo.setSenderName(formatUserName(sender));
        vo.setSenderAvatar(sender == null ? null : sender.getAvatar());
        vo.setContent(message.getContent());
        vo.setCreatedAt(message.getCreatedAt());
        return vo;
    }

    /**
     * 构建消息发送人缓存。
     */
    private Map<Long, User> buildUserCache(List<TeamChatMessage> messages) {
        Map<Long, User> cache = new HashMap<>();
        for (TeamChatMessage message : messages) {
            Long senderId = message.getSenderId();
            if (!cache.containsKey(senderId)) {
                cache.put(senderId, userMapper.findById(senderId));
            }
        }
        return cache;
    }

    /**
     * 格式化用户展示名。
     */
    private String formatUserName(User user) {
        if (user == null) {
            return null;
        }
        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            return user.getNickname();
        }
        return user.getUsername();
    }
}
