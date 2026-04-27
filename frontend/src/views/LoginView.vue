<template>
  <div class="page login-page">
    <header class="login-header">
      <!-- 已删除返回按钮 -->
      <h1 class="login-title">用户登录</h1>
    </header>

    <form @submit.prevent="login">
      <div class="form-row">
        <label>用户名</label>
        <input v-model="username" placeholder="请输入用户名" required />
      </div>
      <div class="form-row">
        <label>密码</label>
        <input v-model="password" type="password" placeholder="请输入密码" required />
      </div>

      <!-- 新增：调试用的账号提示 -->
      <div class="debug-info">
        <p>测试账号：abc</p>
        <p>测试密码：123456</p>
      </div>

      <button class="login-button" type="submit" :disabled="loading">登录</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login as loginApi } from '../api/user'

const username = ref('')
const password = ref('')
const loading = ref(false)
const router = useRouter()

const login = async () => {
  loading.value = true
  try {
    console.log('开始登录，用户名:', username.value, '密码:', password.value)
    try {
      // 尝试调用后端API
      console.log('尝试调用后端登录API')
      const user = await loginApi({ username: username.value, password: password.value })
      console.log('后端API返回用户数据:', user)
      localStorage.setItem('userId', user.id.toString())
      console.log('存储userId到localStorage:', user.id)
      console.log('准备跳转到首页')
      const navigationResult = router.push({ name: 'home' })
      console.log('路由跳转结果:', navigationResult)
      window.alert('登录成功')
      console.log('登录成功，跳转到首页')
    } catch (apiError) {
      // API调用失败，使用模拟数据
      console.log('API调用失败，使用模拟数据:', apiError)
      if (username.value === 'abc' && password.value === '123456') {
        // 模拟登录成功
        const mockUser = { id: 1, username: 'abc', nickname: '测试用户' }
        localStorage.setItem('userId', mockUser.id.toString())
        console.log('存储mock userId到localStorage:', mockUser.id)
        console.log('准备跳转到首页')
        const navigationResult = router.push({ name: 'home' })
        console.log('路由跳转结果:', navigationResult)
        window.alert('登录成功（使用模拟数据）')
        console.log('模拟登录成功，跳转到首页')
      } else {
        window.alert('账号或密码错误')
      }
    }
  } catch (error) {
    console.error('登录失败:', error)
    window.alert('登录失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  padding: 12px;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  /* 让内容垂直居中 */
}

.login-header {
  margin-bottom: 40px;
  text-align: center;
}

.login-title {
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

/* 新增：调试信息样式 */
.debug-info {
  background: #f0f7ff;
  border: 1px solid #b3d7ff;
  border-radius: 6px;
  padding: 10px;
  margin-bottom: 20px;
}

.debug-info p {
  margin: 4px 0;
  font-size: 13px;
  color: #0056b3;
  text-align: center;
}

.login-button {
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
}

.login-button:hover {
  background: #357abd;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>