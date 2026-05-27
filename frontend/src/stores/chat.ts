import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
  getChatConversations,
  getChatMessages,
  getTeamChatConversations,
  getTeamChatMessages,
  markChatRead,
  markTeamChatRead,
  sendChatMessage,
  sendTeamChatMessage,
  type ChatConversationVO,
  type ChatMessageVO,
  type TeamChatConversationVO,
  type TeamChatMessageVO,
} from '../api/chat'

export const useChatStore = defineStore('chat', () => {
  const chatList = ref<ChatConversationVO[]>([])
  const teamChatList = ref<TeamChatConversationVO[]>([])
  const messageMap = ref<Record<number, ChatMessageVO[]>>({})
  const teamMessageMap = ref<Record<number, TeamChatMessageVO[]>>({})
  const loading = ref(false)

  /** 加载私聊会话列表。 */
  async function loadChatList() {
    loading.value = true
    try {
      chatList.value = await getChatConversations()
    } finally {
      loading.value = false
    }
  }

  /** 加载团队聊天列表。 */
  async function loadTeamChatList() {
    loading.value = true
    try {
      teamChatList.value = await getTeamChatConversations()
    } finally {
      loading.value = false
    }
  }

  /** 加载私聊消息列表。 */
  async function loadChatMessages(otherUserId: number, page?: number, size?: number) {
    const messages = await getChatMessages(otherUserId, page, size)
    messageMap.value[otherUserId] = messages
    return messages
  }

  /** 加载团队聊天消息列表。 */
  async function loadTeamChatMessages(teamId: number, page?: number, size?: number) {
    const messages = await getTeamChatMessages(teamId, page, size)
    teamMessageMap.value[teamId] = messages
    return messages
  }

  /** 发送私聊消息并刷新会话列表。 */
  async function sendChat(otherUserId: number, content: string) {
    const message = await sendChatMessage(otherUserId, content)
    await loadChatList()
    return message
  }

  /** 发送团队消息并刷新团队会话列表。 */
  async function sendTeamChat(teamId: number, content: string) {
    const message = await sendTeamChatMessage(teamId, content)
    await loadTeamChatList()
    return message
  }

  /** 标记私聊已读并更新列表。 */
  async function readChat(otherUserId: number) {
    await markChatRead(otherUserId)
    const target = chatList.value.find(item => item.otherUserId === otherUserId)
    if (target) {
      target.unreadCount = 0
    }
  }

  /** 标记团队聊天已读并更新列表。 */
  async function readTeamChat(teamId: number) {
    await markTeamChatRead(teamId)
    const target = teamChatList.value.find(item => item.teamId === teamId)
    if (target) {
      target.unreadCount = 0
    }
  }

  return {
    chatList,
    teamChatList,
    messageMap,
    teamMessageMap,
    loading,
    loadChatList,
    loadTeamChatList,
    loadChatMessages,
    loadTeamChatMessages,
    sendChat,
    sendTeamChat,
    readChat,
    readTeamChat,
  }
})
