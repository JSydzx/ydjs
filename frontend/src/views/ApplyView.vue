<template>
  <div class="page apply-page">
    <header class="apply-header">
      <button class="back-button" @click="goBack">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
          <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" />
        </svg>
      </button>
      <h1 class="apply-title">申请加入</h1>
    </header>

    <section class="apply-form">
      <textarea v-model="reason" placeholder="请填写您的申请理由..." class="reason-input" rows="8"></textarea>
    </section>

    <section class="apply-actions">
      <button class="submit-button" @click="submitApplication">提交申请</button>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { sendMessage } from '../api/message.ts'

const route = useRoute()
const router = useRouter()

const reason = ref('')

const goBack = () => {
  router.back()
}

const submitApplication = async () => {
  if (!reason.value.trim()) {
    alert('请填写申请理由')
    return
  }

  try {
    await sendMessage({
      postId: Number(route.params.postId),
      content: reason.value,
      type: 'application'
    })

    alert('申请已提交！申请理由已发送给帖主的邮箱。')
    router.back()
  } catch (error) {
    console.error('发送申请失败:', error)
    alert('发送失败，请重试')
  }
}
</script>

<style scoped>
.apply-page {
  padding: 12px;
  min-height: 100vh;
}

.apply-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  position: relative;
  width: 100%;
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

.apply-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #4a90e2;
  text-align: center;
}

.apply-form {
  margin-bottom: 20px;
}

.reason-input {
  width: 100%;
  padding: 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  line-height: 1.5;
  resize: vertical;
  min-height: 120px;
  box-sizing: border-box;
}

.reason-input::placeholder {
  color: #333333;
}

.apply-actions {
  text-align: center;
  margin-top: 20px;
}

.submit-button {
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  width: 100%;
  max-width: 300px;
  padding: 12px 24px;
  transition: background-color 0.3s;
}
</style>