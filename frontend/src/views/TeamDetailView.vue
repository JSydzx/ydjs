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
          <span class="creator">队长：{{ team.creatorName || `UID:${team.creatorId}` }}</span>
        </div>
        <div v-if="team.tag" class="tag-row">
          <van-tag type="primary" size="medium" plain>{{ team.tag }}</van-tag>
        </div>
        <p v-if="team.description" class="team-desc">{{ team.description }}</p>
        <p class="team-time">{{ formatTime(team.createdAt) }}</p>
      </div>

      <div class="actions">
        <van-button v-if="canApply" type="primary" block round @click="goApply">申请加入</van-button>
        <van-button type="default" block round @click="openMembers">成员列表 ({{ team.currentMembers }})</van-button>

        <template v-if="isTeamAdmin">
          <van-button type="primary" block round @click="openJoinRequests">申请列表</van-button>
          <van-button type="default" block round @click="openEdit">编辑队伍</van-button>
          <van-button :type="team.status === 'ACTIVE' ? 'warning' : 'success'" block round @click="toggleStatus">
            {{ team.status === 'ACTIVE' ? '关闭招募' : '重新开放招募' }}
          </van-button>
          <van-button type="danger" block round plain @click="handleDelete">删除队伍</van-button>
        </template>
      </div>
    </div>

    <div v-else class="loading-state">
      <van-loading type="spinner" size="24" />
      <p>加载中...</p>
    </div>

    <van-dialog v-model:show="showMembers" title="队伍成员" :show-confirm-button="false" :close-on-click-overlay="true">
      <div class="dialog-list">
        <div v-for="member in members" :key="member.id" class="dialog-item">
          <div class="member-info">
            <span class="member-name">{{ member.nickname || member.username }}</span>
            <span class="member-meta">{{ [member.email, member.major, member.grade].filter(Boolean).join(' · ') || '资料待完善' }}</span>
          </div>
          <div class="member-actions">
            <van-button v-if="member.id !== currentUserId && currentUserId" size="small" type="primary" plain @click="handleChat(member.id, member.nickname || member.username)">
              私聊
            </van-button>
            <van-button v-if="isTeamAdmin && member.id !== team?.creatorId" size="small" type="danger" plain @click="handleRemove(member.id)">
              移除
            </van-button>
          </div>
        </div>
        <div v-if="members.length === 0" class="dialog-empty">暂无成员</div>
      </div>
    </van-dialog>

    <van-dialog v-model:show="showRequests" title="入队申请" :show-confirm-button="false" :close-on-click-overlay="true">
      <div class="dialog-list">
        <div v-for="request in joinRequests" :key="request.id" class="dialog-item request-item">
          <div class="request-info">
            <span class="request-user">{{ request.applicantName || `用户 #${request.userId}` }}</span>
            <span v-if="request.reason" class="request-reason">{{ request.reason }}</span>
            <span class="request-time">{{ formatTime(request.requestedAt) }}</span>
          </div>
          <div v-if="request.status === 'PENDING'" class="request-actions">
            <van-button size="small" type="success" @click="handleApprove(request.id)">同意</van-button>
            <van-button size="small" type="danger" @click="handleReject(request.id)">拒绝</van-button>
          </div>
          <van-tag v-else :type="request.status === 'APPROVED' ? 'success' : 'danger'" size="small">
            {{ request.status === 'APPROVED' ? '已同意' : '已拒绝' }}
          </van-tag>
        </div>
        <div v-if="joinRequests.length === 0" class="dialog-empty">暂无入队申请</div>
      </div>
    </van-dialog>

    <van-dialog v-model:show="showEdit" title="编辑队伍" show-cancel-button confirm-button-text="保存" @confirm="saveEdit">
      <van-form class="edit-form">
        <van-field v-model="editForm.name" label="名称" placeholder="请输入队伍名称" maxlength="100" />
        <van-field v-model="editForm.tag" label="标签" placeholder="例如：前端、算法" maxlength="255" />
        <van-field v-model.number="editForm.maxMembers" label="人数上限" type="digit" placeholder="2-200" />
        <van-field v-model="editForm.description" label="描述" type="textarea" rows="3" autosize maxlength="1000" show-word-limit />
      </van-form>
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  deleteTeam,
  getTeamDetail,
  getTeamMembers,
  removeTeamMember,
  updateTeam,
  type TeamVO,
  type UserVO,
} from '../api/team'
import { approveJoinRequest, getTeamJoinRequests, rejectJoinRequest, type JoinRequestVO } from '../api/join'
import { showConfirmDialog, showToast } from 'vant'

const route = useRoute()
const router = useRouter()
const teamId = Number(route.params.id)

const team = ref<TeamVO | null>(null)
const members = ref<UserVO[]>([])
const joinRequests = ref<JoinRequestVO[]>([])
const showMembers = ref(false)
const showRequests = ref(false)
const showEdit = ref(false)
const currentUserId = Number(localStorage.getItem('userId') || '0')

const editForm = reactive({
  name: '',
  description: '',
  tag: '',
  maxMembers: 10,
})

const isTeamAdmin = computed(() => !!team.value && team.value.creatorId === currentUserId)
const canApply = computed(() => !!team.value && team.value.status === 'ACTIVE' && team.value.creatorId !== currentUserId)

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

const openEdit = () => {
  if (!team.value) return
  editForm.name = team.value.name
  editForm.description = team.value.description || ''
  editForm.tag = team.value.tag || ''
  editForm.maxMembers = team.value.maxMembers
  showEdit.value = true
}

const saveEdit = async () => {
  if (!team.value) return
  if (!editForm.name.trim()) {
    showToast('请输入队伍名称')
    return
  }
  if (editForm.maxMembers < team.value.currentMembers || editForm.maxMembers > 200) {
    showToast('人数上限不能小于当前人数，且不能超过200')
    return
  }
  try {
    team.value = await updateTeam(teamId, {
      name: editForm.name.trim(),
      description: editForm.description.trim() || undefined,
      tag: editForm.tag.trim() || undefined,
      maxMembers: editForm.maxMembers,
    })
    showToast('已保存')
  } catch (e: any) {
    showToast(e.message || '保存失败')
  }
}

const toggleStatus = async () => {
  if (!team.value) return
  const nextStatus = team.value.status === 'ACTIVE' ? 'CLOSED' : 'ACTIVE'
  try {
    team.value = await updateTeam(teamId, { status: nextStatus })
    showToast(nextStatus === 'ACTIVE' ? '已开放招募' : '已关闭招募')
  } catch (e: any) {
    showToast(e.message || '操作失败')
  }
}

const handleDelete = async () => {
  try {
    await showConfirmDialog({ title: '确认删除', message: '删除后队伍和成员关系将不可恢复，确定继续吗？', closeOnClickOverlay: true })
    await deleteTeam(teamId)
    showToast('已删除')
    router.replace({ name: 'team' })
  } catch {
    /* 用户取消 */
  }
}

const handleRemove = async (userId: number) => {
  try {
    await showConfirmDialog({ title: '确认移除', message: '确定要移除该成员吗？', closeOnClickOverlay: true })
    await removeTeamMember(teamId, userId)
    members.value = members.value.filter((member) => member.id !== userId)
    await loadDetail()
    showToast('已移除')
  } catch {
    /* 用户取消 */
  }
}

const handleApprove = async (requestId: number) => {
  try {
    await approveJoinRequest(requestId)
    showToast('已同意')
    await Promise.all([loadDetail(), openJoinRequests()])
  } catch (e: any) {
    showToast(e.message || '操作失败')
  }
}

const handleReject = async (requestId: number) => {
  try {
    await rejectJoinRequest(requestId)
    showToast('已拒绝')
    await openJoinRequests()
  } catch (e: any) {
    showToast(e.message || '操作失败')
  }
}

const goApply = () => {
  router.push({ name: 'apply', params: { postId: teamId } })
}

const handleChat = (memberId: number, memberName: string) => {
  showMembers.value = false
  router.push({ name: 'chatDetail', params: { type: 'user', id: memberId }, query: { title: memberName } })
}

const goBack = () => {
  router.back()
}

const formatTime = (time: string) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
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
  gap: 10px;
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

.creator,
.team-time,
.member-meta,
.request-time {
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
}

.actions {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.dialog-list {
  padding: 12px 16px;
  max-height: 340px;
  overflow-y: auto;
}

.dialog-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid var(--color-divider);
}

.dialog-item:last-child {
  border-bottom: none;
}

.member-info,
.request-info {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 4px;
}

.member-name,
.request-user {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.member-meta,
.request-reason,
.request-time {
  font-size: 12px;
}

.request-reason {
  color: var(--color-text-secondary);
  line-height: 1.4;
}

.member-actions,
.request-actions {
  display: flex;
  gap: 6px;
}

.request-item {
  align-items: flex-start;
}

.dialog-empty {
  text-align: center;
  padding: 24px;
  color: var(--color-text-muted);
  font-size: 14px;
}

.edit-form {
  padding: 10px 0;
}
</style>
