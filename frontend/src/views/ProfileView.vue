<template>
  <div class="page profile-page">
    <div class="profile-card">
      <div class="avatar">{{ userInitial }}</div>
      <div class="info">
        <h2>{{ userStore.user?.nickname || userStore.user?.username || '未登录' }}</h2>
        <small>{{ userStore.user?.email || '完善资料后更容易被队长了解' }}</small>
        <div v-if="userStore.user?.major || userStore.user?.grade" class="profile-tags">
          <span v-if="userStore.user?.major">{{ userStore.user.major }}</span>
          <span v-if="userStore.user?.grade">{{ userStore.user.grade }}</span>
        </div>
      </div>
      <van-icon v-if="!userStore.isLoggedIn" name="arrow" color="#bbb" @click="goToLogin" />
    </div>

    <div v-if="userStore.user?.skills || userStore.user?.bio" class="summary-card">
      <p v-if="userStore.user?.skills">技能：{{ userStore.user.skills }}</p>
      <p v-if="userStore.user?.bio">{{ userStore.user.bio }}</p>
    </div>

    <van-cell-group inset>
      <van-cell title="编辑个人资料" is-link @click="openProfileEditor">
        <template #icon><span class="cell-icon">资料</span></template>
      </van-cell>
      <van-cell title="我的队伍" is-link @click="go('team')">
        <template #icon><span class="cell-icon">队伍</span></template>
      </van-cell>
      <van-cell title="通知中心" is-link @click="go('email')">
        <template #icon><span class="cell-icon">通知</span></template>
      </van-cell>
      <van-cell title="加入申请记录" is-link @click="openApplyHistory">
        <template #icon><span class="cell-icon">申请</span></template>
      </van-cell>
    </van-cell-group>

    <div class="logout-wrap" v-if="userStore.isLoggedIn">
      <van-button round block type="danger" @click="handleLogout">退出登录</van-button>
    </div>
    <div class="logout-wrap" v-else>
      <van-button round block type="primary" @click="goToLogin">去登录</van-button>
    </div>

    <van-dialog v-model:show="showProfileEditor" title="编辑个人资料" show-cancel-button confirm-button-text="保存" @confirm="saveProfile">
      <van-form class="edit-form">
        <van-field v-model="profileForm.nickname" label="昵称" maxlength="50" />
        <van-field v-model="profileForm.email" label="邮箱" type="email" />
        <van-field v-model="profileForm.major" label="专业" maxlength="100" />
        <van-field v-model="profileForm.grade" label="年级" maxlength="50" />
        <van-field v-model="profileForm.skills" label="技能" placeholder="例如：Vue, Java, UI设计" maxlength="255" />
        <van-field v-model="profileForm.bio" label="简介" type="textarea" rows="3" autosize maxlength="500" show-word-limit />
      </van-form>
    </van-dialog>

    <van-dialog v-model:show="showApplyHistory" title="加入申请记录" :show-confirm-button="false" :close-on-click-overlay="true">
      <div class="dialog-list">
        <div v-for="request in applyList" :key="request.id" class="dialog-item">
          <div class="apply-info">
            <span class="apply-team">{{ request.teamName || `队伍 #${request.teamId}` }}</span>
            <span v-if="request.reason" class="apply-reason">理由：{{ request.reason }}</span>
            <span class="apply-time">提交：{{ formatTime(request.requestedAt) }}</span>
            <span v-if="request.processedAt" class="apply-time">处理：{{ formatTime(request.processedAt) }}</span>
          </div>
          <van-tag :type="request.status === 'APPROVED' ? 'success' : request.status === 'REJECTED' ? 'danger' : 'warning'" size="small">
            {{ request.status === 'APPROVED' ? '已通过' : request.status === 'REJECTED' ? '已拒绝' : '待审核' }}
          </van-tag>
        </div>
        <div v-if="applyList.length === 0" class="dialog-empty">暂无申请记录</div>
      </div>
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getMyJoinRequests, type JoinRequestVO } from '../api/join'
import { showConfirmDialog, showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()

const applyList = ref<JoinRequestVO[]>([])
const showApplyHistory = ref(false)
const showProfileEditor = ref(false)
const profileForm = reactive({
  nickname: '',
  email: '',
  major: '',
  grade: '',
  skills: '',
  bio: '',
})

const userInitial = computed(() => {
  const name = userStore.user?.nickname || userStore.user?.username
  return name ? name.charAt(0).toUpperCase() : '?'
})

const requireLogin = () => {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'login' })
    return false
  }
  return true
}

const go = (page: string) => {
  if (!requireLogin()) return
  if (page === 'team') router.push({ name: 'team' })
  else if (page === 'email') router.push({ name: 'email' })
}

const openProfileEditor = () => {
  if (!requireLogin()) return
  const user = userStore.user
  profileForm.nickname = user?.nickname || ''
  profileForm.email = user?.email || ''
  profileForm.major = user?.major || ''
  profileForm.grade = user?.grade || ''
  profileForm.skills = user?.skills || ''
  profileForm.bio = user?.bio || ''
  showProfileEditor.value = true
}

const saveProfile = async () => {
  try {
    await userStore.saveProfile({ ...profileForm })
    showToast('资料已保存')
  } catch (e: any) {
    showToast(e.message || '保存失败')
  }
}

const openApplyHistory = async () => {
  if (!requireLogin()) return
  try {
    applyList.value = await getMyJoinRequests()
  } catch {
    applyList.value = []
  }
  showApplyHistory.value = true
}

const goToLogin = () => {
  router.push({ name: 'login' })
}

const handleLogout = async () => {
  try {
    await showConfirmDialog({ title: '提示', message: '确定退出登录吗？', closeOnClickOverlay: true })
    userStore.logout()
    showToast('已退出')
  } catch {
    /* 用户取消 */
  }
}

const formatTime = (time: string) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
}

onMounted(() => {
  if (userStore.isLoggedIn) userStore.loadProfile()
})
</script>

<style scoped>
.profile-page {
  padding: 16px 12px;
  min-height: 100vh;
}

.profile-card,
.summary-card {
  background: var(--color-white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  margin-bottom: 16px;
}

.profile-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.summary-card {
  padding: 14px 16px;
  font-size: 13px;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.summary-card p {
  margin: 0 0 6px;
}

.summary-card p:last-child {
  margin-bottom: 0;
}

.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), #6db3f2);
  color: var(--color-white);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
  margin-right: 14px;
  flex-shrink: 0;
}

.info {
  flex: 1;
  min-width: 0;
}

.info h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
}

.info small,
.apply-time {
  color: var(--color-text-muted);
  font-size: 12px;
}

.profile-tags {
  display: flex;
  gap: 6px;
  margin-top: 6px;
}

.profile-tags span,
.cell-icon {
  font-size: 12px;
  color: var(--color-primary);
  background: rgba(98, 87, 255, 0.1);
  border-radius: 4px;
  padding: 2px 6px;
}

.cell-icon {
  margin-right: 8px;
}

.logout-wrap {
  margin: 24px 12px;
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
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid var(--color-divider);
}

.dialog-item:last-child {
  border-bottom: none;
}

.apply-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.apply-team {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text);
}

.apply-reason {
  font-size: 12px;
  color: var(--color-text-secondary);
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
