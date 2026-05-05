<template>
  <div class="page register-page">
    <header class="register-header">
      <h1 class="register-title">
        用户注册
      </h1>
    </header>

    <form @submit.prevent="register">
      <div class="form-row">
        <label>用户名</label>
        <input
          v-model="form.username"
          placeholder="请输入用户名"
          required
        >
      </div>
      <div class="form-row">
        <label>密码</label>
        <input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          required
        >
      </div>
      <div class="form-row">
        <label>昵称</label>
        <input
          v-model="form.nickname"
          placeholder="请输入昵称"
        >
      </div>
      <div class="form-row">
        <label>邮箱</label>
        <input
          v-model="form.email"
          type="email"
          placeholder="请输入邮箱"
        >
      </div>

      <button
        class="register-button"
        type="submit"
        :disabled="loading"
      >
        注册
      </button>

      <div class="login-link">
        <span>已有账号？</span>
        <router-link to="/login">
          去登录
        </router-link>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register as registerApi } from '../api/user'

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  email: ''
})
const loading = ref(false)
const router = useRouter()

const register = async () => {
  loading.value = true
  try {
    await registerApi(form)
    window.alert('注册成功！请登录')
    router.push({ name: 'login' })
  } catch (error) {
    console.error('注册失败:', error)
    window.alert('注册失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  padding: 12px;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.register-header {
  margin-bottom: 40px;
  text-align: center;
}

.register-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.form-row {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #333;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  box-sizing: border-box;
  font-size: 14px;
}

.register-button {
  width: 100%;
  padding: 14px;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: background 0.2s;
  margin-bottom: 15px;
}

.register-button:hover {
  background: #357abd;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #4a90e2;
  text-decoration: none;
  margin-left: 5px;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>