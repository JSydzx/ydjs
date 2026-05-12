<template>
  <div class="page profile-page">
    <div class="profile-card">
      <div class="avatar">{{ userInitial }}</div>
      <div class="info">
        <h2>{{ userStore.user?.nickname || userStore.user?.username || '未登录' }}</h2>
        <small>{{ userStore.user?.email || '' }}</small>
      </div>
      <van-icon v-if="!userStore.isLoggedIn" name="arrow" color="#bbb" @click="goToLogin" />
    </div>

    <van-cell-group inset>
      <van-cell title="我的队伍" is-link @click="go('team')">
        <template #icon><span class="cell-icon">🚩</span></template>
      </van-cell>
      <van-cell title="通知中心" is-link @click="go('email')">
        <template #icon><span class="cell-icon">🔔</span></template>
      </van-cell>
      <van-cell title="加入申请记录" is-link @click="openApplyHistory">
        <template #icon><span class="cell-icon">📋</span></template>
      </van-cell>
    </van-cell-group>

    <div class="logout-wrap" v-if="userStore.isLoggedIn">
      <van-button round block type="danger" @click="handleLogout">退出登录</van-button>
    </div>
    <div class="logout-wrap" v-else>
      <van-button round block type="primary" @click="goToLogin">去登录</van-button>
    </div>

    <!-- 加入申请记录弹窗 -->
    <van-dialog v-model:show="showApplyHistory" title="加入申请记录" :show-confirm-button="false">
      <div class="dialog-list">
        <div v-for="r in applyList" :key="r.id" class="dialog-item">
          <div class="apply-info">
            <span class="apply-team">队伍 #{{ r.teamId }}</span>
            <span v-if="r.reason" class="apply-reason">理由：{{ r.reason }}</span>
            <span class="apply-time">{{ formatTime(r.requestedAt) }}</span>
          </div>
          <van-tag
            :type="r.status === 'APPROVED' ? 'success' : r.status === 'REJECTED' ? 'danger' : 'warning'"
            size="small"
          >
            {{ r.status === 'APPROVED' ? '已通过' : r.status === 'REJECTED' ? '已拒绝' : '待审核' }}
          </van-tag>
        </div>
        <div v-if="applyList.length === 0" class="dialog-empty">暂无申请记录</div>
      </div>
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getMyJoinRequests, type JoinRequestVO } from '../api/join'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()
const userStore = useUserStore()

const applyList = ref<JoinRequestVO[]>([])
const showApplyHistory = ref(false)

const userInitial = computed(() => {
  const name = userStore.user?.nickname || userStore.user?.username
  return name ? name.charAt(0).toUpperCase() : '?'
})

const go = (page: string) => {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'login' })
    return
  }
  if (page === 'team') router.push({ name: 'team' })
  else if (page === 'email') router.push({ name: 'email' })
}

const openApplyHistory = async () => {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'login' })
    return
  }
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
    await showConfirmDialog({ title: '提示', message: '确定退出登录吗？' })
    userStore.logout()
    showToast('已退出')
  } catch {
    /* 取消 */
  }
}

const formatTime = (t: string) => {
  if (!t) return ''
  return new Date(t).toLocaleString()
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

.profile-card {
  display: flex;
  align-items: center;
  background: var(--color-white);
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-sm);
  margin-bottom: 16px;
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
}

.info h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
}

.info small {
  color: var(--color-text-muted);
  font-size: 13px;
  margin-top: 2px;
  display: block;
}

.cell-icon {
  font-size: 18px;
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
  padding: 12px 0;
  border-bottom: 1px solid var(--color-divider);
}

.dialog-item:last-child {
  border-bottom: none;
}

.apply-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
  flex: 1;
}

.apply-team {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
}

.apply-reason {
  font-size: 12px;
  color: var(--color-text-secondary);
}

.apply-time {
  font-size: 11px;
  color: var(--color-text-muted);
}

.dialog-empty {
  text-align: center;
  padding: 24px;
  color: var(--color-text-muted);
  font-size: 14px;
}
</style>
