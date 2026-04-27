<template>
  <div class="page profile-page">
    <div class="profile-card">
      <!-- 1. 头像点击事件 -->
      <div class="avatar" @click="goToEditAvatar">
        <!-- 2. 如果有头像就显示图片，没有就显示默认占位符 -->
        <img v-if="user?.avatar" :src="user.avatar" alt="avatar" />
        <span v-else>头像</span>
      </div>
      <div class="info">
        <!-- 3. 显示用户ID，如果没有则显示默认值 -->
        <h2>{{ user?.name || '用户00001' }}</h2>
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

const user = ref<{ name: string; school: string; postCount: number; favoriteCount: number; avatar?: string } | null>(null)
const router = useRouter()

const loadUser = async () => {
  try {
    const resp = await getUserInfo()
    user.value = resp
  } catch (e) {
    console.error('获取用户信息失败', e)
  }
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

// --- 新增：跳转到编辑头像页面 ---
const goToEditAvatar = () => {
  router.push({ name: 'edit-avatar' })
}

const logout = () => {
  if (window.confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token')
    router.push({ name: 'login' })
    window.alert('已退出登录')
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
  background: #e0e0e0;
  margin-right: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  cursor: pointer;
  /* 增加点击手型 */
  flex-shrink: 0;
  /* 防止头像被压缩 */
}

/* 新增：头像图片样式 */
.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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