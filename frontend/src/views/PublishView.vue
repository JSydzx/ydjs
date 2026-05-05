<template>
  <div class="page publish-page">
    <h1>发布文章</h1>
    <div class="form-row">
      <input
        v-model="title"
        placeholder="填写标题"
      >
    </div>
    <div class="form-row">
      <textarea
        v-model="content"
        rows="6"
        placeholder="填写正文"
      />
    </div>
    <div class="form-row">
      <select
        v-model="category"
        class="category-select"
      >
        <option value="">
          请选择分区
        </option>
        <option
          v-for="cat in categories"
          :key="cat"
          :value="cat"
        >
          {{ cat }}
        </option>
      </select>
    </div>
    <button
      :disabled="submitting"
      @click="submit"
    >
      发布
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { createPost } from '../api/publish.ts'

const title = ref('')
const content = ref('')
const category = ref('')
const submitting = ref(false)

const categories = ['篮球', '足球', '排球', '羽毛球', '乒乓球']

const submit = async () => {
  if (!title.value.trim() || !content.value.trim() || !category.value) {
    window.alert('请填写标题、正文和选择分区')
    return
  }
  submitting.value = true
  try {
    await createPost({ title: title.value, content: content.value, topic: category.value })
    window.alert('发布成功')
    title.value = ''
    content.value = ''
    category.value = ''
  } catch (error) {
    console.error(error)
    window.alert('发布失败，请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.publish-page {
  padding: 15px;
}

:root {
  --primary: #4a90e2;
}

h1 {
  color: #4a90e2;
  font-size: 28px;
  margin-bottom: 20px;
  text-align: center;
}

.form-row {
  margin-bottom: 12px;
}

label {
  display: block;
  margin-bottom: 6px;
}

input,
textarea,
select {
  width: 100%;
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 8px;
  box-sizing: border-box;
}

button {
  width: 100%;
  background: #0066ff;
  color: #333333;
  border: 0;
  padding: 10px;
  border-radius: 8px;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>