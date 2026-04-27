<template>
  <div class="page email-page">
    <header class="email-header">
      <button class="back-button" @click="goBack">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
          <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" />
        </svg>
      </button>
      <div class="email-title-wrapper">
        <h1 class="email-title">我的邮箱</h1>
      </div>
    </header>
    <section class="messages-section">
      <div v-if="messages.length === 0" class="empty-messages">
        暂无消息
      </div>
      <div v-else class="messages-list">
        <div v-for="message in messages" :key="message.id" class="message-item" @click="openMessage(message)">
          <div class="message-header">
            <span class="sender">{{ message.sender }}</span>
            <span class="time">{{ message.time }}</span>
          </div>
          <div class="message-preview">{{ message.preview }}</div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getMessages, type Message } from '../api/message.ts'

const router = useRouter()

const messages = ref<Message[]>([])

const loadMessages = async () => {
  try {
    const data = await getMessages()
    messages.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('加载消息失败:', error)
    messages.value = [
      {
        id: 1,
        sender: '系统通知',
        preview: '您的队伍申请已通过审核',
        time: '2026-04-12 10:30',
        content: '恭喜！您的队伍"篮球精英队"申请已通过审核，现在可以开始招募队员了。',
        type: 'system'
      },
      {
        id: 2,
        sender: '张三',
        preview: '申请加入您的足球队',
        time: '2026-04-11 15:20',
        content: '您好，我想加入您的足球队。我有3年足球经验，希望能为队伍贡献力量。',
        type: 'application',
        postId: 123
      },
      {
        id: 3,
        sender: '李四',
        preview: '关于羽毛球比赛的咨询',
        time: '2026-04-10 09:15',
        content: '您好，我想了解一下您组织的羽毛球比赛的报名要求和时间安排。',
        type: 'inquiry',
        postId: 456
      }
    ]
  }
}

const goBack = () => {
  router.back()
}

const openMessage = (message: any) => {
  window.alert(`消息详情：\n发件人：${message.sender}\n时间：${message.time}\n内容：${message.content || message.preview}`)
}

onMounted(() => {
  loadMessages()
})
</script>

<style scoped>
.email-page {
  padding: 12px;
  min-height: 100vh;
}

.email-header {
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

.email-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #4a90e2;
  padding-top: 5px;
}

.messages-section {
  margin-top: 20px;
}

.empty-messages {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-item {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: background-color 0.2s;
}

.message-item:hover {
  background: #f8f9fa;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.sender {
  font-weight: 600;
  color: #333333;
  font-size: 16px;
}

.time {
  color: #999;
  font-size: 12px;
}

.message-preview {
  color: #666;
  font-size: 14px;
  line-height: 1.4;
}
</style>