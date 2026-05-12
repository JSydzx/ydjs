<template>
  <div class="page profile-page">
    <div class="profile-card">
      <div class="avatar" @click="goToLogin" />
      <div class="info">
        <h2>{{ userStore.user?.nickname || userStore.user?.username || '未登录' }}</h2>
        <small>{{ userStore.user?.email || '' }}</small>
      </div>
    </div>
    <van-cell-group inset style="margin-top: 12px;">
      <van-cell title="我的队伍" is-link @click="go('team')" />
      <van-cell title="我的通知" is-link @click="go('email')" />
      <van-cell title="加入申请记录" is-link @click="go('apply')" />
    </van-cell-group>
    <div style="margin: 24px 16px;" v-if="userStore.isLoggedIn"><van-button round block type="danger" @click="handleLogout">退出登录</van-button></div>
    <div style="margin: 24px 16px;" v-else><van-button round block type="primary" @click="goToLogin">去登录</van-button></div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { showConfirmDialog, showToast } from 'vant'
const router = useRouter(); const userStore = useUserStore()
const go = (page: string) => {
  if (!userStore.isLoggedIn) { router.push({ name: 'login' }); return }
  if (page === 'team') router.push({ name: 'team' })
  else if (page === 'email') router.push({ name: 'email' })
  else if (page === 'apply') router.push({ name: 'apply', params: { postId: 0 } })
}
const goToLogin = () => { if (!userStore.isLoggedIn) router.push({ name: 'login' }) }
const handleLogout = async () => {
  try { await showConfirmDialog({ title: '提示', message: '确定退出登录吗？' }); userStore.logout(); showToast('已退出') } catch { /* 取消 */ }
}
onMounted(() => { if (userStore.isLoggedIn) userStore.loadProfile() })
</script>

<style scoped>
.profile-page { padding: 16px; min-height: 100vh; }
.profile-card { display: flex; align-items: center; background: #fff; border-radius: 12px; padding: 20px; }
.avatar { width: 64px; height: 64px; border-radius: 50%; background: #e0e0e0; margin-right: 16px; }
.info h2 { margin: 0; font-size: 18px; color: #333; }
.info small { color: #999; font-size: 13px; }
</style>
