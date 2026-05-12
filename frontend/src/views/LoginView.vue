<template>
  <div class="page login-page">
    <div class="login-card">
      <h1 class="login-title">登录</h1>
      <p class="login-subtitle">校园组队平台</p>
      <van-form @submit="handleLogin">
        <van-cell-group inset>
          <van-field
            v-model="username"
            name="username"
            label="用户名"
            placeholder="请输入用户名"
            :rules="[{ required: true, message: '请输入用户名' }]"
            left-icon="user-o"
          />
          <van-field
            v-model="password"
            name="password"
            label="密码"
            type="password"
            placeholder="请输入密码"
            :rules="[{ required: true, message: '请输入密码' }]"
            left-icon="lock"
          />
        </van-cell-group>
        <div class="login-actions">
          <van-button round block type="primary" native-type="submit" :loading="loading" class="login-btn">
            {{ loading ? '登录中...' : '登录' }}
          </van-button>
        </div>
      </van-form>
      <div class="register-link">
        <span>没有账号？</span>
        <router-link to="/register">去注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { showToast } from 'vant'

const username = ref('')
const password = ref('')
const loading = ref(false)
const router = useRouter()
const userStore = useUserStore()

const handleLogin = async () => {
  loading.value = true
  try {
    await userStore.login({ username: username.value, password: password.value })
    showToast('登录成功')
    router.push({ name: 'home' })
  } catch (e: any) {
    showToast(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 24px 16px;
}

.login-card {
  width: 100%;
  max-width: 360px;
}

.login-title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text);
  text-align: center;
}

.login-subtitle {
  margin: 6px 0 32px;
  font-size: 14px;
  color: var(--color-text-muted);
  text-align: center;
}

.login-actions {
  margin: 20px 12px;
}

.login-btn {
  height: 46px;
  font-size: 16px;
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.register-link a {
  color: var(--color-primary);
  text-decoration: none;
  margin-left: 4px;
}
</style>
