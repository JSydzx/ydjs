<template>
  <div class="page publish-page">
    <header class="publish-header">
      <h1>发布组队</h1>
      <p class="publish-sub">创建一个新队伍，召集志同道合的队友</p>
    </header>
    <van-form @submit="handleSubmit">
      <van-cell-group inset>
        <van-field
          v-model="form.name"
          name="name"
          label="队伍名称"
          placeholder="给你的队伍起个名字"
          :rules="[{ required: true, message: '请输入队伍名称' }]"
        />
        <van-field
          v-model="form.description"
          name="description"
          label="队伍描述"
          placeholder="介绍一下队伍的目标和要求"
          type="textarea"
          rows="3"
          autosize
        />
        <van-field
          v-model="form.tag"
          name="tag"
          label="标签"
          placeholder="如：篮球、算法、数模"
        />
        <van-field
          v-model.number="form.maxMembers"
          name="maxMembers"
          label="最大人数"
          placeholder="默认10人"
          type="digit"
        />
      </van-cell-group>
      <div class="submit-wrap">
        <van-button round block type="primary" native-type="submit" :loading="submitting" size="large">
          发 布
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createTeam } from '../api/team'
import { showToast } from 'vant'

const router = useRouter()
const submitting = ref(false)
const form = reactive({
  name: '',
  description: '',
  tag: '',
  maxMembers: 10,
})

const handleSubmit = async () => {
  if (!form.name.trim()) {
    showToast('请输入队伍名称')
    return
  }
  submitting.value = true
  try {
    await createTeam({
      name: form.name.trim(),
      description: form.description || undefined,
      tag: form.tag || undefined,
      maxMembers: form.maxMembers || 10,
    })
    showToast('发布成功')
    form.name = ''
    form.description = ''
    form.tag = ''
    form.maxMembers = 10
    router.push({ name: 'home' })
  } catch (e: any) {
    showToast(e.message || '发布失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.publish-page {
  padding: 16px 12px;
  min-height: 100vh;
}

.publish-header {
  text-align: center;
  margin-bottom: 24px;
}

.publish-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
}

.publish-sub {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--color-text-muted);
}

.submit-wrap {
  margin: 24px 12px;
}
</style>
