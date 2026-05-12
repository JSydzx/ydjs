<template>
  <div class="page post-detail-page">
    <van-nav-bar title="队伍详情" left-text="返回" left-arrow @click-left="goBack" />
    <div v-if="team" class="team-info">
      <van-cell-group inset>
        <van-cell :title="team.name" :label="`${team.currentMembers}/${team.maxMembers}人`">
          <template #extra><van-tag :type="team.status === 'ACTIVE' ? 'success' : 'danger'">{{ team.status === 'ACTIVE' ? '招募中' : '已关闭' }}</van-tag></template>
        </van-cell>
        <van-cell v-if="team.tag" title="标签" :value="team.tag" />
        <van-cell v-if="team.description" title="描述" :label="team.description" />
        <van-cell title="创建者" :value="team.creatorName || 'UID:' + team.creatorId" />
        <van-cell title="创建时间" :value="formatTime(team.createdAt)" />
      </van-cell-group>
      <div class="actions"><van-button type="primary" block round @click="goApply">申请加入</van-button></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTeamDetail, type TeamVO } from '../api/team'
import { showToast } from 'vant'
const route = useRoute(); const router = useRouter(); const teamId = Number(route.params.id)
const team = ref<TeamVO | null>(null)
const loadDetail = async () => { try { team.value = await getTeamDetail(teamId) } catch (e: any) { showToast(e.message || '加载失败') } }
const goApply = () => { router.push({ name: 'apply', params: { postId: teamId } }) }
const goBack = () => { router.back() }
const formatTime = (t: string) => t ? new Date(t).toLocaleString() : ''
loadDetail()
</script>

<style scoped>
.post-detail-page { min-height: 100vh; }
.team-info { padding: 12px; }
.actions { margin-top: 20px; padding: 0 16px; }
</style>
