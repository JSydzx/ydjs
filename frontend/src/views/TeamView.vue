<template>
  <div class="page team-page">
    <header class="team-header"><h1>我的队伍</h1></header>
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <div v-if="teams.length === 0 && !loading" class="empty">还没有队伍</div>
      <van-cell-group inset v-else>
        <van-cell v-for="team in teams" :key="team.id" :title="team.name" :label="`${team.currentMembers}/${team.maxMembers}人  ${team.tag || ''}`" is-link @click="goDetail(team.id)" />
      </van-cell-group>
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getMyTeams, type TeamVO } from '../api/team'
import { showToast } from 'vant'
const router = useRouter()
const teams = ref<TeamVO[]>([]); const loading = ref(false); const refreshing = ref(false)
const loadTeams = async () => { loading.value = true; try { teams.value = await getMyTeams() } catch (e: any) { showToast(e.message || '加载失败') } finally { loading.value = false } }
const onRefresh = async () => { refreshing.value = true; try { teams.value = await getMyTeams() } finally { refreshing.value = false } }
const goDetail = (id: number) => { router.push({ name: 'teamDetail', params: { id } }) }
loadTeams()
</script>

<style scoped>
.team-page { padding: 12px; min-height: 100vh; }
.team-header { text-align: center; margin-bottom: 16px; }
.team-header h1 { margin: 0; font-size: 20px; font-weight: 600; color: #333; }
.empty { text-align: center; padding: 60px 20px; color: #999; }
</style>
