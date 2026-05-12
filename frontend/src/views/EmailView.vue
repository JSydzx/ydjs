<template>
  <div class="page email-page">
    <van-nav-bar title="通知中心" />
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <div v-if="notifications.length === 0 && !loading" class="empty">暂无通知</div>
      <van-cell-group inset v-else>
        <van-swipe-cell v-for="item in notifications" :key="item.id">
          <van-cell :title="getTypeLabel(item.type)" :label="item.message" :value="formatTime(item.createdAt)" />
          <template #right><van-button square type="danger" text="删除" @click="handleDelete(item.id)" /></template>
        </van-swipe-cell>
      </van-cell-group>
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getNotifications, deleteNotification, type NotificationVO } from '../api/notification'
import { showToast } from 'vant'
const notifications = ref<NotificationVO[]>([]); const loading = ref(false); const refreshing = ref(false)
const loadData = async () => { loading.value = true; try { notifications.value = await getNotifications() } catch (e: any) { showToast(e.message || '加载失败') } finally { loading.value = false } }
const onRefresh = async () => { refreshing.value = true; try { notifications.value = await getNotifications() } finally { refreshing.value = false } }
const handleDelete = async (id: number) => {
  try { await deleteNotification(id); notifications.value = notifications.value.filter(n => n.id !== id); showToast('已删除') } catch (e: any) { showToast(e.message || '删除失败') }
}
const getTypeLabel = (type: string) => ({ JOIN_REQUEST: '加入申请', SYSTEM: '系统通知', TEAM_UPDATE: '队伍更新' } as Record<string, string>)[type] || type
const formatTime = (t: string) => t ? new Date(t).toLocaleString() : ''
loadData()
</script>

<style scoped>
.email-page { min-height: 100vh; }
.empty { text-align: center; padding: 60px 20px; color: #999; }
</style>
