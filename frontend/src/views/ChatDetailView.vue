<template>
  <div class="page chat-detail-page">
    <van-nav-bar :title="title" left-text="返回" left-arrow @click-left="handleBack" />
    <div ref="scrollRef" class="chat-body">
      <div v-if="messages.length === 0 && !loading" class="empty-state">
        <div class="empty-icon">💬</div>
        <p class="empty-text">暂无消息，发送第一条消息吧</p>
      </div>
      <div v-else class="message-list">
        <div
          v-for="item in messages"
          :key="item.id"
          class="message-row"
          :class="{ mine: isMine(item.senderId) }"
        >
          <div class="message-bubble">
            <div v-if="type === 'team' && !isMine(item.senderId)" class="message-sender">{{ item.senderName || '成员' }}</div>
            <div class="message-content">{{ item.content }}</div>
            <div class="message-footer">
              <span v-if="isMine(item.senderId) && type === 'user' && item.isRead" class="read-status">已读</span>
              <span v-else-if="isMine(item.senderId) && type === 'user' && !item.isRead" class="read-status unread">未读</span>
              <span class="message-time">{{ formatTime(item.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="chat-input">
      <input v-model="input" class="chat-text" placeholder="输入消息" @keyup.enter="handleSend" />
      <van-button size="small" type="primary" :loading="sending" @click="handleSend">发送</van-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { useChatStore } from '../stores/chat'

const route = useRoute()
const router = useRouter()
const chatStore = useChatStore()
const loading = ref(false)
const sending = ref(false)
const input = ref('')
const scrollRef = ref<HTMLDivElement | null>(null)
const messages = ref<any[]>([])
let pollTimer: ReturnType<typeof setInterval> | null = null

const type = computed(() => route.params.type as string)
const targetId = computed(() => Number(route.params.id))
const userId = computed(() => Number(localStorage.getItem('userId') || '0'))

const title = computed(() => {
  const queryTitle = route.query.title as string | undefined
  if (queryTitle) return queryTitle
  if (type.value === 'team') {
    const team = chatStore.teamChatList.find(item => item.teamId === targetId.value)
    return team?.teamName || '团队聊天'
  }
  const chat = chatStore.chatList.find(item => item.otherUserId === targetId.value)
  return chat?.otherUserName || '私聊'
})

const handleBack = () => {
  router.back()
}

const isMine = (senderId: number) => senderId === userId.value

const formatTime = (value: string) => {
  if (!value) return ''
  const d = new Date(value)
  const now = new Date()
  const isToday = d.toDateString() === now.toDateString()
  if (isToday) {
    return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return d.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

/** 判断用户是否滚动在底部。 */
const isAtBottom = () => {
  if (!scrollRef.value) return true
  const el = scrollRef.value
  return el.scrollHeight - el.scrollTop - el.clientHeight < 60
}

/** 滚动到底部。 */
const scrollToBottom = () => {
  if (!scrollRef.value) return
  scrollRef.value.scrollTop = scrollRef.value.scrollHeight
}

/** 加载聊天消息。 */
const loadMessages = async () => {
  loading.value = true
  try {
    if (type.value === 'team') {
      messages.value = await chatStore.loadTeamChatMessages(targetId.value, 1, 50)
    } else {
      messages.value = await chatStore.loadChatMessages(targetId.value, 1, 50)
    }
  } finally {
    loading.value = false
  }
}

/** 静默拉取新消息（轮询用），不显示 loading。 */
const pollMessages = async () => {
  try {
    const atBottom = isAtBottom()
    if (type.value === 'team') {
      messages.value = await chatStore.loadTeamChatMessages(targetId.value, 1, 50)
      await chatStore.readTeamChat(targetId.value)
    } else {
      messages.value = await chatStore.loadChatMessages(targetId.value, 1, 50)
      await chatStore.readChat(targetId.value)
    }
    await nextTick()
    if (atBottom) scrollToBottom()
  } catch {
    /* 轮询失败静默处理 */
  }
}

/** 发送消息。 */
const handleSend = async () => {
  if (!input.value.trim()) {
    showToast('请输入消息')
    return
  }
  sending.value = true
  try {
    if (type.value === 'team') {
      await chatStore.sendTeamChat(targetId.value, input.value.trim())
    } else {
      await chatStore.sendChat(targetId.value, input.value.trim())
    }
    input.value = ''
    await loadMessages()
    await nextTick()
    scrollToBottom()
  } catch (e: any) {
    showToast(e.message || '发送失败')
  } finally {
    sending.value = false
  }
}

onMounted(async () => {
  await loadMessages()
  await nextTick()
  scrollToBottom()
  // 首次标记已读
  if (type.value === 'team') {
    await chatStore.readTeamChat(targetId.value)
  } else {
    await chatStore.readChat(targetId.value)
  }
  // 每 5 秒轮询新消息
  pollTimer = setInterval(pollMessages, 5000)
})

onUnmounted(() => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
})
</script>

<style scoped>
.chat-detail-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px 12px 12px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.message-row {
  display: flex;
  justify-content: flex-start;
}

.message-row.mine {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 72%;
  background: var(--color-white);
  border-radius: 12px;
  padding: 10px 12px;
  box-shadow: var(--shadow-sm);
}

.message-row.mine .message-bubble {
  background: rgba(98, 87, 255, 0.12);
}

.message-sender {
  font-size: 11px;
  color: var(--color-primary);
  font-weight: 600;
  margin-bottom: 4px;
}

.message-content {
  font-size: 14px;
  color: var(--color-text);
  line-height: 1.5;
  word-break: break-all;
}

.message-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 6px;
  margin-top: 6px;
}

.message-time {
  font-size: 11px;
  color: var(--color-text-muted);
}

.read-status {
  font-size: 10px;
  color: #999;
}

.read-status.unread {
  color: var(--color-primary);
}

.chat-input {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px 16px;
  background: var(--color-white);
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

.chat-text {
  flex: 1;
  border: none;
  outline: none;
  background: #f5f6fb;
  border-radius: 10px;
  padding: 8px 12px;
  font-size: 14px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-text {
  margin: 0;
  font-size: 15px;
  color: var(--color-text-muted);
}
</style>
