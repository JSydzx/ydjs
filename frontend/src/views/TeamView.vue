<template>
  <div class="page team-page">
    <header class="team-header">
      <button class="back-button" @click="goBack">← 返回</button>
      <h1 class="team-title">我的队伍</h1>
    </header>
    <div class="team-actions">
      <button @click="refresh">刷新</button>
      <button @click="createTeam">创建队伍</button>
    </div>
    <div v-if="teams.length === 0" class="empty">还没有队伍，去创建或加入一个吧</div>
    <ul v-else>
      <li v-for="team in teams" :key="team.id" class="team-card">
        <h3>{{ team.name }}</h3>
        <p>成员: {{ team.memberCount }} | 赛事: {{ team.eventCount }}</p>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getTeamList, createTeam as apiCreateTeam } from '../api/team.ts'

const teams = ref<Array<{ id: number; name: string; memberCount: number; eventCount: number }>>([])
const router = useRouter()

const loadTeams = async () => {
  const resp = await getTeamList()
  teams.value = resp.items
}

const goBack = () => {
  router.back()
}

const refresh = () => loadTeams()

const createTeam = async () => {
  const teamName = window.prompt('请输入队伍名')
  if (teamName) {
    await apiCreateTeam({ name: teamName })
    await loadTeams()
  }
}

onMounted(loadTeams)
</script>

<style scoped>
.team-page {
  padding: 12px;
  min-height: 100vh;
}

.team-header {
  position: relative;
  margin-bottom: 20px;
  text-align: center;
}

.back-button {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  border: none;
  background: transparent;
  color: #4a90e2;
  font-size: 16px;
  cursor: pointer;
  padding: 8px 0;
}

.team-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #4a90e2;
}

.team-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.empty {
  color: #333333;
}

.team-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 10px;
}
</style>