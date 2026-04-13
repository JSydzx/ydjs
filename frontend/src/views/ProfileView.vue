<template>
  <div class="page profile-page">
    <div class="profile-card">
      <div class="avatar" @click="goToLogin"></div>
      <div class="info">
        <h2>{{ user?.name || '未登录' }}</h2>
        <small>{{ user?.school || '' }}</small>
      </div>
    </div>

    <div class="stats">
      <span>发布 {{ user?.postCount ?? 0 }}</span>
      <span>收藏 {{ user?.favoriteCount ?? 0 }}</span>
    </div>

    <ul class="menu-list">
      <li @click="go('service')">服务</li>
      <li @click="go('team')">我的队伍</li>
      <li @click="go('email')">我的邮箱</li>
      <li @click="go('settings')">设置</li>
      <li @click="logout" class="logout">退出登录</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getUserInfo } from '../api/profile.ts'

const user = ref<{ name: string; school: string; postCount: number; favoriteCount: number } | null>(null)

const router = useRouter()

const loadUser = async () => {
  const resp = await getUserInfo()
  user.value = resp
}

const go = (page: string) => {
  if (page === 'email') {
    router.push({ name: 'email' })
  } else if (page === 'team') {
    router.push({ name: 'team' })
  } else {
    window.alert(`跳转: ${page}`)
  }
}

const goToLogin = () => {
  router.push({ name: 'login' })
}

const logout = () => {
  if (window.confirm('确定要退出登录吗？')) {
    window.alert('已退出登录')
    // TODO: 清除登录状态
  }
}

onMounted(loadUser)
</script>

<style scoped>
.profile-page {
  padding: 15px;
  color: #000000;
}

.profile-card {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 10px;
  padding: 14px;
}

.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: #ccc;
  margin-right: 12px;
}

.info h2 {
  margin: 0;
  font-size: 1.2rem;
}

.stats {
  margin: 15px 0;
  display: flex;
  gap: 16px;
  background: #fff;
  border-radius: 10px;
  padding: 10px;
  justify-content: space-around;
  color: #000000;
}

.menu-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.menu-list li {
  background: #fff;
  margin-bottom: 8px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  color: #000000;
}

.menu-list li.logout {
  background: #ff4444;
  color: white;
  text-align: center;
  font-weight: bold;
}
</style>