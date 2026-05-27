<template>
  <div class="page chat-detail-page">
    <van-nav-bar :title="title" left-text="返回" left-arrow @click-left="handleBack" />
    <div ref="scrollRef" class="chat-body">
      <div v-if="messages.length === 0 && !loading" class="empty-state">
        <div class="empty-icon">💬</div>
        <p class="empty-text">暂无消息</p>
      </div>
      <div v-else class="message-list">
        <div
          v-for="item in messages"
          :key="item.id"
          class="message-row"
          :class="{ mine: isMine(item.senderId) }"
        >
          <div class="message-bubble">
            <div class="message-content">{{ item.content }}</div>
            <div class="message-time">{{ formatTime(item.createdAt) }}</div>
          </div>
        </div>
      </div>
    </div>
    <div class="chat-input">
      <input v-model="input" class="chat-text" placeholder="输入消息" />
      <van-button size="small" type="primary" :loading="sending" @click="handleSend">发送</van-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
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

/** 返回上一页。 */
const handleBack = () => {
  router.back()
}

/** 判断是否为自己的消息。 */
const isMine = (senderId: number) => senderId === userId.value

/** 格式化时间显示。 */
const formatTime = (value: string) => {
  if (!value) return ''
  return new Date(value).toLocaleString()
}

/** 加载聊天消息并滚动到底部。 */
const loadMessages = async () => {
  loading.value = true
  try {
    if (type.value === 'team') {
      messages.value = await chatStore.loadTeamChatMessages(targetId.value, 1, 50)
      await chatStore.readTeamChat(targetId.value)
    } else {
      messages.value = await chatStore.loadChatMessages(targetId.value, 1, 50)
      await chatStore.readChat(targetId.value)
    }
    await nextTick()
    scrollToBottom()
  } finally {
    loading.value = false
  }
}

/** 发送消息并刷新列表。 */
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
  } catch (e: any) {
    showToast(e.message || '发送失败')
  } finally {
    sending.value = false
  }
}

/** 滚动到消息底部。 */
const scrollToBottom = () => {
  if (!scrollRef.value) return
  scrollRef.value.scrollTop = scrollRef.value.scrollHeight
}

onMounted(() => {
  loadMessages()
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

.message-content {
  font-size: 14px;
  color: var(--color-text);
  line-height: 1.5;
}

.message-time {
  margin-top: 6px;
  font-size: 11px;
  color: var(--color-text-muted);
  text-align: right;
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
