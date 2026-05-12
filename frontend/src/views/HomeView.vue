<template>
  <div class="page home-page">
    <header class="top-bar">
      <h1>组队广场</h1>
      <p class="top-sub">发现感兴趣的队伍，立即加入</p>
    </header>
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="loadTeams">
        <div v-if="teams.length === 0 && !loading" class="empty-state">
          <div class="empty-icon">🏟️</div>
          <p class="empty-text">暂无队伍</p>
          <p class="empty-hint">去发布第一个组队吧</p>
        </div>
        <div v-for="team in teams" :key="team.id" class="team-card" @click="goDetail(team.id)">
          <div class="card-header">
            <h3 class="card-title">{{ team.name }}</h3>
            <van-tag :type="team.status === 'ACTIVE' ? 'success' : 'default'" size="medium">
              {{ team.status === 'ACTIVE' ? '招募中' : '已关闭' }}
            </van-tag>
          </div>
          <p class="card-desc">{{ team.description || '暂无描述' }}</p>
          <div class="card-footer">
            <div class="card-tags">
              <van-tag v-if="team.tag" type="primary" size="medium" plain>{{ team.tag }}</van-tag>
            </div>
            <div class="card-meta">
              <span class="member-count">{{ team.currentMembers }}/{{ team.maxMembers }}人</span>
              <span class="creator-name">{{ team.creatorName || 'UID:' + team.creatorId }}</span>
            </div>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getTeamList, type TeamVO } from '../api/team'
import { showToast } from 'vant'

const router = useRouter()
const teams = ref<TeamVO[]>([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

const loadTeams = async () => {
  loading.value = true
  try {
    teams.value = await getTeamList()
    finished.value = true
  } catch (e: any) {
    showToast(e.message || '加载失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

const onRefresh = async () => {
  refreshing.value = true
  try {
    teams.value = await getTeamList()
    finished.value = false
  } finally {
    refreshing.value = false
  }
}

const goDetail = (id: number) => {
  router.push({ name: 'postDetail', params: { id } })
}
</script>

<style scoped>
.home-page {
  padding: 16px 12px;
}

.top-bar {
  margin-bottom: 16px;
}

.top-bar h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
}

.top-sub {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--color-text-muted);
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
  font-size: 16px;
  color: var(--color-text-secondary);
}

.empty-hint {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--color-text-muted);
}

.team-card {
  background: var(--color-white);
  border-radius: var(--radius-md);
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.team-card:active {
  box-shadow: var(--shadow-md);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.card-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-desc {
  margin: 0 0 12px;
  font-size: 13px;
  color: var(--color-text-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-meta {
  display: flex;
  gap: 8px;
  font-size: 12px;
}

.member-count {
  color: var(--color-primary);
  font-weight: 500;
}

.creator-name {
  color: var(--color-text-muted);
}
</style>
