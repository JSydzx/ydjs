import { http } from './http'

export interface ChatConversationVO {
  conversationId: number
  otherUserId: number
  otherUserName: string | null
  otherUserAvatar: string | null
  lastMessage: string | null
  lastMessageAt: string | null
  unreadCount: number
}

export interface ChatMessageVO {
  id: number
  conversationId: number
  senderId: number
  senderName: string | null
  senderAvatar: string | null
  recipientId: number
  content: string
  isRead: boolean
  createdAt: string
}

export interface TeamChatConversationVO {
  teamId: number
  teamName: string
  lastMessage: string | null
  lastMessageAt: string | null
  unreadCount: number
}

export interface TeamChatMessageVO {
  id: number
  teamId: number
  senderId: number
  senderName: string | null
  senderAvatar: string | null
  content: string
  createdAt: string
}

/** 查询私聊会话列表。 */
export const getChatConversations = (): Promise<ChatConversationVO[]> => http.get('/chat/list')

/** 查询私聊消息列表。 */
export const getChatMessages = (otherUserId: number, page?: number, size?: number): Promise<ChatMessageVO[]> =>
  http.get(`/chat/conversation/${otherUserId}`, { params: { page, size } })

/** 发送私聊消息。 */
export const sendChatMessage = (recipientId: number, content: string): Promise<ChatMessageVO> =>
  http.post('/chat/send', { recipientId, content })

/** 标记私聊已读。 */
export const markChatRead = (otherUserId: number): Promise<void> => http.post(`/chat/conversation/${otherUserId}/read`)

/** 查询团队聊天列表。 */
export const getTeamChatConversations = (): Promise<TeamChatConversationVO[]> => http.get('/team/chat/list')

/** 查询团队聊天消息列表。 */
export const getTeamChatMessages = (teamId: number, page?: number, size?: number): Promise<TeamChatMessageVO[]> =>
  http.get(`/team/chat/${teamId}/messages`, { params: { page, size } })

/** 发送团队消息。 */
export const sendTeamChatMessage = (teamId: number, content: string): Promise<TeamChatMessageVO> =>
  http.post(`/team/chat/${teamId}/send`, { content })

/** 标记团队聊天已读。 */
export const markTeamChatRead = (teamId: number): Promise<void> => http.post(`/team/chat/${teamId}/read`)
