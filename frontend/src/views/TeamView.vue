<template>
  <div class="page team-page">
    <header class="team-header">
      <h1>我的队伍</h1>
    </header>
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <div v-if="teams.length === 0 && !loading" class="empty-state">
        <div class="empty-icon">🚩</div>
        <p class="empty-text">还没有队伍</p>
        <p class="empty-hint">去广场发现或发布组队吧</p>
      </div>
      <div v-else class="team-list">
        <div v-for="team in teams" :key="team.id" class="team-card" @click="goDetail(team.id)">
          <div class="card-body">
            <h3 class="card-title">{{ team.name }}</h3>
            <div class="card-info">
              <span class="member-info">{{ team.currentMembers }}/{{ team.maxMembers }}人</span>
              <span v-if="team.tag" class="tag-info">{{ team.tag }}</span>
              <van-tag :type="team.status === 'ACTIVE' ? 'success' : 'default'" size="small">
                {{ team.status === 'ACTIVE' ? '招募中' : '已关闭' }}
              </van-tag>
            </div>
          </div>
          <van-icon name="arrow" color="#bbb" />
        </div>
      </div>
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getMyTeams, type TeamVO } from '../api/team'
import { showToast } from 'vant'

const router = useRouter()
const teams = ref<TeamVO[]>([])
const loading = ref(false)
const refreshing = ref(false)

const loadTeams = async () => {
  loading.value = true
  try {
    teams.value = await getMyTeams()
  } catch (e: any) {
    showToast(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const onRefresh = async () => {
  refreshing.value = true
  try {
    teams.value = await getMyTeams()
  } finally {
    refreshing.value = false
  }
}

const goDetail = (id: number) => {
  router.push({ name: 'teamDetail', params: { id } })
}

loadTeams()
</script>

<style scoped>
.team-page {
  padding: 16px 12px;
  min-height: 100vh;
}

.team-header {
  margin-bottom: 16px;
}

.team-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
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

.team-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.team-card {
  background: var(--color-white);
  border-radius: var(--radius-md);
  padding: 16px;
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.team-card:active {
  box-shadow: var(--shadow-md);
}

.card-body {
  flex: 1;
}

.card-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
}

.card-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.member-info {
  color: var(--color-primary);
  font-weight: 500;
}

.tag-info {
  color: var(--color-text-muted);
  background: var(--color-bg);
  padding: 2px 8px;
  border-radius: 4px;
}
</style>
