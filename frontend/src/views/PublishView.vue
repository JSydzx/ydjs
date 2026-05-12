<template>
  <div class="page publish-page">
    <header class="publish-header"><h1>发布组队</h1></header>
    <van-form @submit="handleSubmit">
      <van-cell-group inset>
        <van-field v-model="form.name" label="队伍名称" placeholder="请输入队伍名称" required :rules="[{ required: true, message: '请输入队伍名称' }]" />
        <van-field v-model="form.description" label="队伍描述" placeholder="请输入队伍描述" type="textarea" rows="3" />
        <van-field v-model="form.tag" label="标签" placeholder="如：篮球、足球、排球" />
        <van-field v-model.number="form.maxMembers" label="最大人数" placeholder="默认10人" type="digit" />
      </van-cell-group>
      <div style="margin: 16px;"><van-button round block type="primary" native-type="submit" :loading="submitting">发 布</van-button></div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { createTeam } from '../api/team'
import { showToast } from 'vant'
const router = useRouter(); const submitting = ref(false)
const form = reactive({ name: '', description: '', tag: '', maxMembers: 10 })
const handleSubmit = async () => {
  if (!form.name.trim()) { showToast('请输入队伍名称'); return }
  submitting.value = true
  try {
    await createTeam({ name: form.name, description: form.description || undefined, tag: form.tag || undefined, maxMembers: form.maxMembers || 10 })
    showToast('发布成功')
    form.name = ''; form.description = ''; form.tag = ''; form.maxMembers = 10
    router.push({ name: 'home' })
  } catch (e: any) { showToast(e.message || '发布失败') }
  finally { submitting.value = false }
}
</script>

<style scoped>
.publish-page { padding: 12px; }
.publish-header { text-align: center; margin-bottom: 20px; }
.publish-header h1 { margin: 0; font-size: 22px; font-weight: 700; color: #333; }
</style>
