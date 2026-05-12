<template>
  <div class="page email-page">
    <van-nav-bar title="通知中心" />
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <div v-if="notifications.length === 0 && !loading" class="empty-state">
        <div class="empty-icon">🔔</div>
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
            <div v-if="item.type === 'JOIN_REQUEST'" class="notify-hint">
              点击查看队伍详情处理申请
            </div>
          </div>
          <template #right>
            <van-button square type="danger" text="删除" @click="handleDelete(item.id)" />
          </template>
        </van-swipe-cell>
      </div>
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getNotifications, deleteNotification, type NotificationVO } from '../api/notification'
import { showToast } from 'vant'

const router = useRouter()
const notifications = ref<NotificationVO[]>([])
const loading = ref(false)
const refreshing = ref(false)

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
    notifications.value = await getNotifications()
  } finally {
    refreshing.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await deleteNotification(id)
    notifications.value = notifications.value.filter(n => n.id !== id)
    showToast('已删除')
  } catch (e: any) {
    showToast(e.message || '删除失败')
  }
}

const handleClick = (item: NotificationVO) => {
  if (item.type === 'JOIN_REQUEST' || item.type === 'TEAM_UPDATE') {
    router.push({ name: 'team' })
  }
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
    JOIN_REQUEST: '📩',
    SYSTEM: '📢',
    TEAM_UPDATE: '👥',
  }
  return map[type] || '📌'
}

const formatTime = (t: string) => {
  if (!t) return ''
  return new Date(t).toLocaleString()
}

loadData()
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
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-text {
  margin: 0;
  font-size: 15px;
  color: var(--color-text-muted);
}

.notify-list {
  padding: 12px;
}

.notify-card {
  background: var(--color-white);
  border-radius: var(--radius-md);
  padding: 14px 16px;
  margin-bottom: 10px;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
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
  font-size: 16px;
}

.notify-time {
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
</style>
