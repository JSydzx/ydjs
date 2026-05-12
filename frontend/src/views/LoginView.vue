<template>
  <div class="page login-page">
    <header class="login-header"><h1 class="login-title">登录</h1></header>
    <form @submit.prevent="handleLogin">
      <div class="form-row"><label>用户名</label><input v-model="username" placeholder="请输入用户名" required /></div>
      <div class="form-row"><label>密码</label><input v-model="password" type="password" placeholder="请输入密码" required /></div>
      <button class="login-button" type="submit" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</button>
      <div class="register-link"><span>没有账号？</span><router-link to="/register">去注册</router-link></div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
const username = ref(''); const password = ref(''); const loading = ref(false)
const router = useRouter(); const userStore = useUserStore()
const handleLogin = async () => {
  loading.value = true
  try { await userStore.login({ username: username.value, password: password.value }); router.push({ name: 'home' }) }
  catch (e: any) { alert(e.message || '登录失败') }
  finally { loading.value = false }
}
</script>

<style scoped>
.login-page { padding: 24px 16px; min-height: 100vh; display: flex; flex-direction: column; justify-content: center; }
.login-header { margin-bottom: 32px; text-align: center; }
.login-title { margin: 0; font-size: 24px; font-weight: 600; color: #333; }
.form-row { margin-bottom: 16px; }
label { display: block; margin-bottom: 6px; font-weight: 500; color: #333; }
input { width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 8px; box-sizing: border-box; font-size: 15px; }
.login-button { width: 100%; padding: 14px; background: #4a90e2; color: white; border: none; border-radius: 8px; cursor: pointer; font-size: 16px; font-weight: 500; margin-bottom: 16px; }
.login-button:disabled { opacity: 0.6; cursor: not-allowed; }
.register-link { text-align: center; font-size: 14px; color: #666; }
.register-link a { color: #4a90e2; text-decoration: none; margin-left: 4px; }
</style>
