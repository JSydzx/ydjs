<template>
  <div class="page team-detail-page">
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
        <van-button v-if="!isMyTeam" type="primary" block round @click="goApply">申请加入</van-button>
        <template v-else>
          <van-button type="primary" block round @click="openJoinRequests" class="action-btn">
            申请列表
          </van-button>
          <van-button type="default" block round @click="openMembers" class="action-btn">
            成员管理 ({{ team.currentMembers }})
          </van-button>
        </template>
      </div>
    </div>

    <div v-else class="loading-state">
      <van-loading type="spinner" size="24" />
      <p>加载中...</p>
    </div>

    <!-- 成员管理弹窗 -->
    <van-dialog v-model:show="showMembers" title="团队成员" :show-confirm-button="false">
      <div class="dialog-list">
        <div v-for="m in members" :key="m.id" class="dialog-item">
          <span class="member-name">{{ m.nickname || m.username }}</span>
          <van-button
            v-if="isMyTeam && m.id !== team?.creatorId"
            size="small"
            type="danger"
            plain
            @click="handleRemove(m.id)"
          >
            移除
          </van-button>
        </div>
        <div v-if="members.length === 0" class="dialog-empty">暂无成员</div>
      </div>
    </van-dialog>

    <!-- 申请列表弹窗 -->
    <van-dialog v-model:show="showRequests" title="入队申请" :show-confirm-button="false">
      <div class="dialog-list">
        <div v-for="r in joinRequests" :key="r.id" class="dialog-item">
          <div class="request-info">
            <span class="request-user">用户 #{{ r.userId }}</span>
            <span v-if="r.reason" class="request-reason">{{ r.reason }}</span>
            <span class="request-time">{{ formatTime(r.requestedAt) }}</span>
          </div>
          <div v-if="r.status === 'PENDING'" class="request-actions">
            <van-button size="small" type="success" @click="handleApprove(r.id)">同意</van-button>
            <van-button size="small" type="danger" @click="handleReject(r.id)">拒绝</van-button>
          </div>
          <van-tag v-else :type="r.status === 'APPROVED' ? 'success' : 'danger'" size="small">
            {{ r.status === 'APPROVED' ? '已同意' : '已拒绝' }}
          </van-tag>
        </div>
        <div v-if="joinRequests.length === 0" class="dialog-empty">暂无入队申请</div>
      </div>
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTeamDetail, getTeamMembers, removeTeamMember, type TeamVO, type UserVO } from '../api/team'
import { getTeamJoinRequests, approveJoinRequest, rejectJoinRequest, type JoinRequestVO } from '../api/join'
import { showToast, showConfirmDialog } from 'vant'

const route = useRoute()
const router = useRouter()
const teamId = Number(route.params.id)

const team = ref<TeamVO | null>(null)
const members = ref<UserVO[]>([])
const joinRequests = ref<JoinRequestVO[]>([])
const showMembers = ref(false)
const showRequests = ref(false)

const currentUserId = Number(localStorage.getItem('userId'))
const isMyTeam = computed(() => team.value?.creatorId === currentUserId)

const loadDetail = async () => {
  try {
    team.value = await getTeamDetail(teamId)
  } catch (e: any) {
    showToast(e.message || '加载失败')
  }
}

const openMembers = async () => {
  try {
    members.value = await getTeamMembers(teamId)
  } catch {
    members.value = []
  }
  showMembers.value = true
}

const openJoinRequests = async () => {
  try {
    joinRequests.value = await getTeamJoinRequests(teamId)
  } catch {
    joinRequests.value = []
  }
  showRequests.value = true
}

const handleRemove = async (userId: number) => {
  try {
    await showConfirmDialog({ title: '确认', message: '确定要移除该成员吗？' })
    await removeTeamMember(teamId, userId)
    showToast('已移除')
    await loadDetail()
    members.value = members.value.filter(m => m.id !== userId)
  } catch {
    /* 取消 */
  }
}

const handleApprove = async (requestId: number) => {
  try {
    await approveJoinRequest(requestId)
    showToast('已同意')
    await loadDetail()
    // 刷新申请列表
    joinRequests.value = joinRequests.value.map(r =>
      r.id === requestId ? { ...r, status: 'APPROVED' as const } : r
    )
  } catch (e: any) {
    showToast(e.message || '操作失败')
  }
}

const handleReject = async (requestId: number) => {
  try {
    await rejectJoinRequest(requestId)
    showToast('已拒绝')
    joinRequests.value = joinRequests.value.map(r =>
      r.id === requestId ? { ...r, status: 'REJECTED' as const } : r
    )
  } catch (e: any) {
    showToast(e.message || '操作失败')
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
.team-detail-page {
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
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-btn {
  margin: 0;
}

.dialog-list {
  padding: 12px 16px;
  max-height: 320px;
  overflow-y: auto;
}

.dialog-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--color-divider);
}

.dialog-item:last-child {
  border-bottom: none;
}

.member-name {
  font-size: 15px;
  color: var(--color-text);
}

.request-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.request-user {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
}

.request-reason {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.request-time {
  font-size: 11px;
  color: var(--color-text-muted);
}

.request-actions {
  display: flex;
  gap: 6px;
}

.dialog-empty {
  text-align: center;
  padding: 24px;
  color: var(--color-text-muted);
  font-size: 14px;
}
</style>
