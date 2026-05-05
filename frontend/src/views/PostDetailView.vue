<template>
  <div class="page post-detail-page">
    <header class="detail-header">
      <button
        class="back-button"
        @click="goBack"
      >
        ← 返回
      </button>
    </header>

    <div class="detail-image">
      <img
        :src="detail.imgUrl"
        alt="帖子配图"
      >
    </div>

    <section class="detail-meta">
      <img
        class="avatar"
        :src="detail.avatar"
        alt="头像"
      >
      <div class="meta-text">
        <div class="nickname">
          {{ detail.nickname }}
        </div>
        <div class="time">
          {{ detail.time }}
        </div>
      </div>
    </section>

    <section class="detail-content">
      <h2>{{ detail.title }}</h2>
      <p>{{ detail.content }}</p>
    </section>

    <section class="detail-actions">
      <button
        class="apply-button"
        @click="applyToTeam"
      >
        申请加入队伍
      </button>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const detail = reactive({
  id: Number(route.params.id ?? 0),
  title: '帖子标题示例：XXXX 队伍招人',
  content:
    '这里是帖子正文，展示详细内容。你可以在此处显示完整文本描述、活动规则、联系方式等信息。',
  avatar: 'https://via.placeholder.com/60',
  nickname: 'XX用户',
  time: '2026-04-12 14:30',
  imgUrl: 'https://via.placeholder.com/640x320',
})

const goBack = () => {
  router.back()
}

const applyToTeam = () => {
  router.push({ name: 'apply', params: { postId: detail.id } })
}
</script>

<style scoped>
.post-detail-page {
  padding: 12px;
}

.detail-header {
  display: flex;
  align-items: center;
  margin-bottom: 14px;
}

.back-button {
  border: none;
  background: transparent;
  color: #333333;
}

.detail-image img {
  width: 100%;
  border-radius: 12px;
  display: block;
  margin-bottom: 16px;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.detail-meta .avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.meta-text .nickname {
  font-weight: 700;
  font-size: 16px;
  color: #333333;
}

.meta-text .time {
  color: #333333;
  font-size: 14px;
  margin-top: 4px;
}

.detail-content h2 {
  margin: 0 0 12px;
  font-size: 20px;
  line-height: 1.4;
  color: #333333;
}

.detail-content p {
  margin: 0;
  color: #333333;
  line-height: 1.8;
  white-space: pre-wrap;
}

.detail-actions {
  margin-top: 20px;
  text-align: center;
}

.apply-button {
  background: #4CAF50;
  color: #333333;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  width: 100%;
  max-width: 300px;
}
</style>
