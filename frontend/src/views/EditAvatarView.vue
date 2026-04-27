<template>
  <div class="page edit-avatar-page">
    <header class="edit-avatar-header">
      <button class="back-button" @click="goBack">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
          <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" />
        </svg>
      </button>
      <h1 class="edit-avatar-title">修改头像</h1>
    </header>

    <div class="content">
      <!-- 头像预览区 -->
      <div class="avatar-preview">
        <img :src="previewUrl || defaultAvatar" alt="预览头像" />
      </div>

      <!-- 文件选择器 (隐藏) -->
      <input type="file" ref="fileInput" accept="image/*" @change="onFileChange" style="display: none;" />

      <!-- 操作按钮 -->
      <div class="actions">
        <button @click="triggerFileInput" class="select-btn">选择图片</button>
        <button @click="uploadAvatar" :disabled="!selectedFile" class="upload-btn">
          确认上传
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const fileInput = ref<HTMLInputElement | null>(null)
const selectedFile = ref<File | null>(null)
const previewUrl = ref<string>('')
const defaultAvatar = '' // 这里可以放一个默认的头像图片URL

// 触发隐藏的文件选择器
const triggerFileInput = () => {
  fileInput.value?.click()
}

// 处理文件选择
const onFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    selectedFile.value = file
    // 创建本地预览链接
    previewUrl.value = URL.createObjectURL(file)
  }
}

// 模拟上传图片
const uploadAvatar = async () => {
  if (!selectedFile.value) return

  // 这里应该调用后端API上传图片
  // 例如: const formData = new FormData(); formData.append('avatar', selectedFile.value); await api.uploadAvatar(formData);

  console.log('准备上传图片:', selectedFile.value.name)

  // 模拟上传延迟
  setTimeout(() => {
    alert('头像上传成功！')
    goBack()
  }, 1000)
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.edit-avatar-page {
  padding: 12px;
  min-height: 100vh;
}

.header {
  position: relative;
  margin-bottom: 30px;
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

.title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 20px;
}

.avatar-preview {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background: #e0e0e0;
  overflow: hidden;
  margin-bottom: 40px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.actions {
  display: flex;
  gap: 15px;
  width: 100%;
}

.select-btn,
.upload-btn {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  border: none;
}

.select-btn {
  background: #f0f2f5;
  color: #333;
}

.upload-btn {
  background: #4a90e2;
  color: white;
}

.upload-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>