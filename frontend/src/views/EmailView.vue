<template>
  <div class="page email-page">
    <van-nav-bar title="通知中心" />

    <div class="tab-switch">
      <button class="tab-btn" :class="{ active: activeTab === 'notification' }" @click="switchTab('notification')">
        通知
      </button>
      <button class="tab-btn" :class="{ active: activeTab === 'chat' }" @click="switchTab('chat')">
        聊天
        <span v-if="totalUnread > 0" class="tab-badge">{{ totalUnread }}</span>
      </button>
    </div>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <template v-if="activeTab === 'notification'">
        <div v-if="notifications.length === 0 && !loading" class="empty-state">
          <div class="empty-icon">暂无</div>
          <p class="empty-text">暂无通知</p>
        </div>

        <div v-else class="notify-list">
          <van-swipe-cell v-for="item in notifications" :key="item.id">
            <div class="notify-card" :class="{ unread: !item.isRead }" @click="handleClick(item)">
              <div class="notify-header">
                <span class="notify-type">
                  <span class="type-icon">{{ getTypeIcon(item.type) }}</span>
                  {{ getTypeLabel(item.type) }}
                </span>
                <span class="notify-time">{{ formatTime(item.createdAt) }}</span>
              </div>
              <p class="notify-msg">{{ item.message }}</p>
              <div v-if="item.relatedId" class="notify-hint">点击查看相关队伍</div>
            </div>
            <template #right>
              <van-button square type="danger" text="删除" @click="handleDelete(item.id)" />
            </template>
          </van-swipe-cell>
        </div>
      </template>

      <template v-else>
        <div v-if="chatList.length === 0 && teamChatList.length === 0 && !chatStore.loading" class="empty-state">
          <div class="empty-icon">暂无</div>
          <p class="empty-text">暂无聊天记录</p>
        </div>

        <div v-else class="chat-section">
          <div class="section-title">私聊</div>
          <div v-if="chatList.length === 0" class="section-empty">暂无私聊</div>
          <div v-else class="chat-list">
            <div v-for="item in chatList" :key="item.conversationId" class="chat-card" @click="openChat(item)">
              <div class="chat-avatar">{{ item.otherUserName?.slice(0, 1) || '私' }}</div>
              <div class="chat-info">
                <div class="chat-title">
                  {{ item.otherUserName || '匿名用户' }}
                  <span v-if="item.unreadCount > 0" class="chat-badge">{{ item.unreadCount }}</span>
                </div>
                <div class="chat-preview">{{ item.lastMessage || '暂无消息' }}</div>
              </div>
              <div class="chat-time">{{ formatTime(item.lastMessageAt || '') }}</div>
            </div>
          </div>

          <div class="section-title">团队</div>
          <div v-if="teamChatList.length === 0" class="section-empty">暂无团队聊天</div>
          <div v-else class="chat-list">
            <div v-for="item in teamChatList" :key="item.teamId" class="chat-card" @click="openTeamChat(item)">
              <div class="chat-avatar">队</div>
              <div class="chat-info">
                <div class="chat-title">
                  {{ item.teamName }}
                  <span v-if="item.unreadCount > 0" class="chat-badge">{{ item.unreadCount }}</span>
                </div>
                <div class="chat-preview">{{ item.lastMessage || '暂无消息' }}</div>
              </div>
              <div class="chat-time">{{ formatTime(item.lastMessageAt || '') }}</div>
            </div>
          </div>
        </div>
      </template>
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { deleteNotification, getNotifications, markRead, type NotificationVO } from '../api/notification'
import { showToast } from 'vant'
import { useChatStore } from '../stores/chat'

const router = useRouter()
const notifications = ref<NotificationVO[]>([])
const loading = ref(false)
const refreshing = ref(false)
const activeTab = ref<'notification' | 'chat'>('notification')
const chatStore = useChatStore()

const chatList = computed(() => chatStore.chatList)
const teamChatList = computed(() => chatStore.teamChatList)
const totalUnread = computed(() => {
  const chatUnread = chatList.value.reduce((sum, item) => sum + (item.unreadCount || 0), 0)
  const teamUnread = teamChatList.value.reduce((sum, item) => sum + (item.unreadCount || 0), 0)
  return chatUnread + teamUnread
})

const switchTab = (tab: 'notification' | 'chat') => {
  activeTab.value = tab
  if (tab === 'chat') {
    Promise.all([chatStore.loadChatList(), chatStore.loadTeamChatList()])
  }
}

const loadData = async () => {
  loading.value = true
  try {
    notifications.value = await getNotifications()
  } catch (e: any) {
    showToast(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const onRefresh = async () => {
  refreshing.value = true
  try {
    await Promise.all([loadData(), chatStore.loadChatList(), chatStore.loadTeamChatList()])
  } finally {
    refreshing.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await deleteNotification(id)
    notifications.value = notifications.value.filter((notification) => notification.id !== id)
    showToast('已删除')
  } catch (e: any) {
    showToast(e.message || '删除失败')
  }
}

const handleClick = async (item: NotificationVO) => {
  if (!item.isRead) {
    try {
      await markRead(item.id)
      item.isRead = true
    } catch {
      /* 标记失败不阻断跳转 */
    }
  }
  if (item.relatedId) {
    router.push({ name: 'teamDetail', params: { id: item.relatedId } })
  }
}

const openChat = (item: { otherUserId: number; otherUserName: string | null }) => {
  router.push({ name: 'chatDetail', params: { type: 'user', id: item.otherUserId }, query: { title: item.otherUserName || '私聊' } })
}

const openTeamChat = (item: { teamId: number; teamName: string }) => {
  router.push({ name: 'chatDetail', params: { type: 'team', id: item.teamId }, query: { title: item.teamName } })
}

const getTypeLabel = (type: string) => {
  const map: Record<string, string> = {
    JOIN_REQUEST: '入队申请',
    SYSTEM: '系统通知',
    TEAM_UPDATE: '队伍更新',
  }
  return map[type] || type
}

const getTypeIcon = (type: string) => {
  const map: Record<string, string> = {
    JOIN_REQUEST: '申请',
    SYSTEM: '系统',
    TEAM_UPDATE: '队伍',
  }
  return map[type] || '消息'
}

const formatTime = (time: string) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
}

onMounted(() => {
  Promise.all([loadData(), chatStore.loadChatList(), chatStore.loadTeamChatList()])
})
</script>

<style scoped>
.email-page {
  min-height: 100vh;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: var(--color-white);
  color: var(--color-text-muted);
  margin-bottom: 12px;
}

.empty-text {
  margin: 0;
  font-size: 15px;
  color: var(--color-text-muted);
}

.tab-switch {
  display: flex;
  gap: 8px;
  padding: 12px;
}

.tab-btn {
  flex: 1;
  border: none;
  background: #f5f6fb;
  color: var(--color-text-muted);
  padding: 8px 12px;
  border-radius: 999px;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.tab-btn.active {
  background: rgba(98, 87, 255, 0.16);
  color: var(--color-primary);
  font-weight: 600;
}

.tab-badge,
.chat-badge {
  background: var(--color-primary);
  color: #fff;
  padding: 0 6px;
  border-radius: 999px;
  font-size: 11px;
  line-height: 18px;
}

.notify-list,
.chat-section {
  padding: 0 12px 12px;
}

.notify-card,
.chat-card {
  background: var(--color-white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  cursor: pointer;
}

.notify-card {
  padding: 14px 16px;
  margin-bottom: 10px;
}

.notify-card.unread {
  border-left: 3px solid var(--color-primary);
}

.notify-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notify-type {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 4px;
}

.type-icon {
  font-size: 12px;
  color: var(--color-primary);
}

.notify-time,
.chat-time {
  font-size: 11px;
  color: var(--color-text-muted);
}

.notify-msg {
  margin: 0;
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.5;
}

.notify-hint {
  margin-top: 8px;
  font-size: 12px;
  color: var(--color-primary);
}

.section-title {
  margin: 12px 0 8px;
  font-size: 13px;
  color: var(--color-text-muted);
}

.section-empty {
  font-size: 12px;
  color: var(--color-text-muted);
  padding: 6px 0 12px;
}

.chat-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.chat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
}

.chat-avatar {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(98, 87, 255, 0.12);
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
}

.chat-info {
  flex: 1;
  min-width: 0;
}

.chat-title {
  font-size: 14px;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 6px;
}

.chat-preview {
  font-size: 12px;
  color: var(--color-text-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
