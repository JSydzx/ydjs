<template>
  <div class="page register-page">
    <div class="register-card">
      <h1 class="register-title">用户注册</h1>
      <p class="register-subtitle">加入校园组队平台</p>
      <van-form @submit="handleRegister">
        <van-cell-group inset>
          <van-field
            v-model="form.username"
            name="username"
            label="用户名"
            placeholder="请输入用户名"
            :rules="[{ required: true, message: '请输入用户名' }]"
          />
          <van-field
            v-model="form.password"
            name="password"
            label="密码"
            type="password"
            placeholder="请输入密码（至少6位）"
            :rules="[{ required: true, message: '请输入密码' }, { min: 6, message: '密码至少6位' }]"
          />
          <van-field
            v-model="form.nickname"
            name="nickname"
            label="昵称"
            placeholder="请输入昵称"
          />
          <van-field
            v-model="form.email"
            name="email"
            label="邮箱"
            type="email"
            placeholder="请输入邮箱"
          />
        </van-cell-group>
        <div class="register-actions">
          <van-button round block type="primary" native-type="submit" :loading="loading" class="register-btn">
            注册
          </van-button>
        </div>
      </van-form>
      <div class="login-link">
        <span>已有账号？</span>
        <router-link to="/login">去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { register as registerApi } from '../api/user'
import { showToast } from 'vant'

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  email: '',
})
const loading = ref(false)
const router = useRouter()

const handleRegister = async () => {
  loading.value = true
  try {
    await registerApi(form)
    showToast('注册成功！请登录')
    router.push({ name: 'login' })
  } catch (e: any) {
    showToast(e.message || '注册失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 24px 16px;
}

.register-card {
  width: 100%;
  max-width: 360px;
}

.register-title {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text);
  text-align: center;
}

.register-subtitle {
  margin: 6px 0 32px;
  font-size: 14px;
  color: var(--color-text-muted);
  text-align: center;
}

.register-actions {
  margin: 20px 12px;
}

.register-btn {
  height: 46px;
  font-size: 16px;
}

.login-link {
  text-align: center;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.login-link a {
  color: var(--color-primary);
  text-decoration: none;
  margin-left: 4px;
}
</style>
