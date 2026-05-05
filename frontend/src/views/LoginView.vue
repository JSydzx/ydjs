<template>
  <div class="page login-page">
    <header class="login-header">
      <button
        class="back-button"
        @click="goBack"
      >
        ← 返回
      </button>
      <h1 class="login-title">
        登录
      </h1>
    </header>
    <form @submit.prevent="login">
      <div class="form-row">
        <label>用户名</label>
        <input
          v-model="username"
          placeholder="请输入用户名"
          required
        >
      </div>
      <div class="form-row">
        <label>密码</label>
        <input
          v-model="password"
          type="password"
          placeholder="请输入密码"
          required
        >
      </div>
      <button
        class="login-button"
        type="submit"
        :disabled="loading"
      >
        登录
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const username = ref('')
const password = ref('')
const loading = ref(false)
const router = useRouter()

const login = async () => {
  loading.value = true
  try {
    // TODO: 调用登录API
    console.log('登录信息:', { username: username.value, password: password.value })
    window.alert('登录成功')
    router.push({ name: 'home' })
  } catch (error) {
    console.error('登录失败:', error)
    window.alert('登录失败，请重试')
  } finally {
    loading.value = false
  }
}


const goBack = () => {
  router.back()
}
</script>

<style scoped>
.login-page {
  padding: 12px;
  min-height: 100vh;
}

.login-header {
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
  font-size: 16px;
  cursor: pointer;
  padding: 8px 0;
}

.login-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #4a90e2;
}

.form-row {
  margin-bottom: 15px;
  color: #4a90e2;
}

.login-button {
  width: 100%;
  padding: 12px;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>