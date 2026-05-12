<template>
  <div class="page team-detail-page">
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
      <div class="actions">
        <van-button v-if="!isMyTeam" type="primary" block round @click="goApply">申请加入</van-button>
        <van-button v-else type="default" block round @click="openMembers">成员管理 ({{ team.currentMembers }})</van-button>
      </div>
    </div>
    <van-dialog v-model:show="showMembers" title="团队成员" :show-confirm-button="false">
      <div class="members-list">
        <div v-for="m in members" :key="m.id" class="member-item">
          <span>{{ m.nickname || m.username }}</span>
          <van-button v-if="isMyTeam && m.id !== team?.creatorId" size="small" type="danger" @click="handleRemove(m.id)">移除</van-button>
        </div>
        <div v-if="members.length === 0" class="empty">暂无成员</div>
      </div>
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTeamDetail, getTeamMembers, removeTeamMember, type TeamVO, type UserVO } from '../api/team'
import { showToast, showConfirmDialog } from 'vant'
const route = useRoute(); const router = useRouter(); const teamId = Number(route.params.id)
const team = ref<TeamVO | null>(null); const members = ref<UserVO[]>([]); const showMembers = ref(false)
const currentUserId = Number(localStorage.getItem('userId'))
const isMyTeam = computed(() => team.value?.creatorId === currentUserId)
const loadDetail = async () => { try { team.value = await getTeamDetail(teamId) } catch (e: any) { showToast(e.message || '加载失败') } }
const openMembers = async () => { try { members.value = await getTeamMembers(teamId) } catch { members.value = [] }; showMembers.value = true }
const handleRemove = async (userId: number) => {
  try { await showConfirmDialog({ title: '确认', message: '确定要移除该成员吗？' }); await removeTeamMember(teamId, userId); showToast('移除成功'); members.value = members.value.filter(m => m.id !== userId); if (team.value) team.value.currentMembers-- } catch { /* 取消 */ }
}
const goApply = () => { router.push({ name: 'apply', params: { postId: teamId } }) }
const goBack = () => { router.back() }
const formatTime = (t: string) => t ? new Date(t).toLocaleString() : ''
loadDetail()
</script>

<style scoped>
.team-detail-page { min-height: 100vh; }
.team-info { padding: 12px; }
.actions { margin-top: 20px; padding: 0 16px; }
.members-list { padding: 16px; max-height: 300px; overflow-y: auto; }
.member-item { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; border-bottom: 1px solid #f5f5f5; }
.empty { text-align: center; color: #999; padding: 20px; }
</style>
