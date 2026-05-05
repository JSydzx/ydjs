<template>
  <div class="page apply-page">
    <header class="apply-header">
      <button
        class="back-button"
        @click="goBack"
      >
        ← 返回
      </button>
      <h1 class="apply-title">
        申请理由
      </h1>
    </header>

    <section class="apply-form">
      <textarea
        v-model="reason"
        placeholder="请填写您的申请理由..."
        class="reason-input"
        rows="8"
      />
    </section>

    <section class="apply-actions">
      <button
        class="submit-button"
        @click="submitApplication"
      >
        提交申请
      </button>
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

    alert('申请已提交！消息已发送给帖主。')
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
  display: flex;
  flex-direction: column;
}

.apply-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  position: relative;
}

.back-button {
  border: none;
  background: transparent;
  color: #333333;
  font-size: 16px;
  cursor: pointer;
  padding: 8px 0;
  position: absolute;
  left: 0;
}

.apply-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333333;
}

.apply-form {
  flex: 1;
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
}

.submit-button {
  background: #4CAF50;
  color: #333333;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  width: 100%;
  max-width: 300px;
}

.submit-button:hover {
  background: #45a049;
}
</style>