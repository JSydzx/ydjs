<template>
  <div class="page home-page">
    <header class="top-bar"><h1>组队广场</h1></header>
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="loadTeams">
        <div v-if="teams.length === 0 && !loading" class="empty-state">
          <p>暂无队伍</p><p class="sub">去发布第一个组队吧</p>
        </div>
        <van-card v-for="team in teams" :key="team.id" :title="team.name" :desc="team.description || '暂无描述'" @click="goDetail(team.id)">
          <template #tags><van-tag v-if="team.tag" type="primary" size="medium">{{ team.tag }}</van-tag></template>
          <template #footer>
            <span class="member-info">{{ team.currentMembers }}/{{ team.maxMembers }}人</span>
            <span class="creator-info">{{ team.creatorName || '未知' }}</span>
          </template>
        </van-card>
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
const loading = ref(false); const finished = ref(false); const refreshing = ref(false)
const loadTeams = async () => {
  loading.value = true
  try { teams.value = await getTeamList(); finished.value = true }
  catch (e: any) { showToast(e.message || '加载失败'); finished.value = true }
  finally { loading.value = false }
}
const onRefresh = async () => { refreshing.value = true; try { teams.value = await getTeamList(); finished.value = false } finally { refreshing.value = false } }
const goDetail = (id: number) => { router.push({ name: 'postDetail', params: { id } }) }
</script>

<style scoped>
.home-page { padding: 12px; }
.top-bar { margin-bottom: 12px; }
.top-bar h1 { margin: 0; font-size: 22px; font-weight: 700; color: #333; }
.empty-state { text-align: center; padding: 60px 20px; color: #999; }
.empty-state .sub { margin-top: 8px; font-size: 14px; }
.member-info { margin-right: 12px; color: #4a90e2; font-size: 13px; }
.creator-info { color: #999; font-size: 13px; }
</style>
