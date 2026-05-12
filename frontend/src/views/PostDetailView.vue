<template>
  <div class="page post-detail-page">
    <van-nav-bar title="队伍详情" left-text="返回" left-arrow @click-left="goBack" />
    <div v-if="team" class="team-info">
      <div class="info-card">
        <div class="card-row">
          <h2 class="team-name">{{ team.name }}</h2>
          <van-tag :type="team.status === 'ACTIVE' ? 'success' : 'default'" size="medium">
            {{ team.status === 'ACTIVE' ? '招募中' : '已关闭' }}
          </van-tag>
        </div>
        <div class="card-row meta-row">
          <span class="member-count">{{ team.currentMembers }}/{{ team.maxMembers }}人</span>
          <span class="creator">队长：{{ team.creatorName || 'UID:' + team.creatorId }}</span>
        </div>
        <div v-if="team.tag" class="tag-row">
          <van-tag type="primary" size="medium" plain>{{ team.tag }}</van-tag>
        </div>
        <p v-if="team.description" class="team-desc">{{ team.description }}</p>
        <p class="team-time">{{ formatTime(team.createdAt) }}</p>
      </div>
      <div class="actions">
        <van-button type="primary" block round size="large" @click="goApply">申请加入</van-button>
      </div>
    </div>
    <div v-else class="loading-state">
      <van-loading type="spinner" size="24" />
      <p>加载中...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTeamDetail, type TeamVO } from '../api/team'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const teamId = Number(route.params.id)
const team = ref<TeamVO | null>(null)

const loadDetail = async () => {
  try {
    team.value = await getTeamDetail(teamId)
  } catch (e: any) {
    showToast(e.message || '加载失败')
  }
}

const goApply = () => {
  router.push({ name: 'apply', params: { postId: teamId } })
}

const goBack = () => {
  router.back()
}

const formatTime = (t: string) => {
  if (!t) return ''
  return new Date(t).toLocaleString()
}

loadDetail()
</script>

<style scoped>
.post-detail-page {
  min-height: 100vh;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  color: var(--color-text-muted);
  gap: 12px;
}

.team-info {
  padding: 12px;
}

.info-card {
  background: var(--color-white);
  border-radius: var(--radius-md);
  padding: 16px;
  box-shadow: var(--shadow-sm);
}

.card-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.team-name {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text);
}

.meta-row {
  font-size: 13px;
  margin-bottom: 10px;
}

.member-count {
  color: var(--color-primary);
  font-weight: 500;
}

.creator {
  color: var(--color-text-muted);
}

.tag-row {
  margin-bottom: 10px;
}

.team-desc {
  margin: 0 0 10px;
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.team-time {
  margin: 0;
  font-size: 12px;
  color: var(--color-text-muted);
}

.actions {
  margin-top: 20px;
}
</style>
