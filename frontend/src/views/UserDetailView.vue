<template>
  <div class="page user-detail-page">
    <header class="user-detail-header">
      <button
        class="back-button"
        @click="goBack"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="currentColor"
        >
          <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" />
        </svg>
      </button>
      <h1 class="user-detail-title">
        用户详情
      </h1>
    </header>

    <section class="user-info">
      <div class="avatar-container">
        <img
          :src="user.avatar || 'https://via.placeholder.com/100'"
          alt="用户头像"
          class="avatar"
        >
      </div>
      <div class="user-meta">
        <h2 class="nickname">
          {{ user.nickname || user.username }}
        </h2>
        <p class="uid">
          UID: {{ user.id }}
        </p>
        <p class="username">
          用户名: {{ user.username }}
        </p>
        <p
          v-if="user.email"
          class="email"
        >
          邮箱: {{ user.email }}
        </p>
      </div>
    </section>

    <section class="user-posts">
      <h3>发布的帖子</h3>
      <div
        v-if="posts.length === 0"
        class="empty"
      >
        暂无发布的帖子
      </div>
      <ul v-else>
        <li
          v-for="post in posts"
          :key="post.id"
          class="post-item"
        >
          <h4>{{ post.title }}</h4>
          <p>{{ post.preview }}</p>
          <span class="post-time">{{ post.time }}</span>
        </li>
      </ul>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { UserVO } from '../api/user'
import type { PostItem } from '../api/home'

const route = useRoute()
const router = useRouter()
const userId = Number(route.params.id)

const user = ref<UserVO>({
  id: userId,
  username: '',
  nickname: '',
  email: '',
  avatar: '',
  createdAt: '',
  updatedAt: ''
})

const posts = ref<PostItem[]>([])

const loadUserInfo = async () => {
  try {
    // 这里应该调用获取用户详情的API
    // 由于后端可能没有实现，这里使用模拟数据
    user.value = {
      id: userId,
      username: `user${userId}`,
      nickname: `用户${userId}`,
      email: `user${userId}@example.com`,
      avatar: `https://via.placeholder.com/100?text=User${userId}`,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const loadUserPosts = async () => {
  try {
    // 这里应该调用获取用户帖子的API
    // 由于后端可能没有实现，这里使用模拟数据
    if (userId === 1) {
      posts.value = [
        {
          id: 1,
          title: '篮球队招人',
          preview: '我们篮球队正在招人，欢迎喜欢篮球的朋友加入！',
          time: '2026-04-25 10:00',
          meta: '',
          avatar: '',
          userId: 1,
          nickname: '用户1'
        },
        {
          id: 2,
          title: '足球比赛通知',
          preview: '本周日有一场足球比赛，感兴趣的朋友可以参加',
          time: '2026-04-24 15:30',
          meta: '',
          avatar: '',
          userId: 1,
          nickname: '用户1'
        }
      ]
    }
  } catch (error) {
    console.error('获取用户帖子失败:', error)
  }
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadUserInfo()
  loadUserPosts()
})
</script>

<style scoped>
.user-detail-page {
  padding: 12px;
  min-height: 100vh;
}

.user-detail-header {
  position: relative;
  margin-bottom: 20px;
  text-align: center;
}

.back-button {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  border: none;
  background: transparent;
  color: #4a90e2;
  font-size: 14px;
  cursor: pointer;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-detail-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #4a90e2;
  padding-top: 5px;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.avatar-container {
  margin-bottom: 15px;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
}

.user-meta {
  text-align: center;
}

.nickname {
  margin: 0 0 5px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.uid {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #666;
}

.username,
.email {
  margin: 5px 0;
  font-size: 14px;
  color: #666;
}

.user-posts {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.user-posts h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.empty {
  text-align: center;
  color: #999;
  padding: 20px 0;
}

.post-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.post-item:last-child {
  border-bottom: none;
}

.post-item h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.post-item p {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.post-time {
  font-size: 12px;
  color: #999;
}
</style>