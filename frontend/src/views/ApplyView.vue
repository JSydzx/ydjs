<template>
  <div class="page apply-page">
    <van-nav-bar title="申请加入" left-text="返回" left-arrow @click-left="goBack" />
    <div class="form-area">
      <van-field v-model="reason" label="申请理由" type="textarea" rows="4" placeholder="请介绍自己，说明为什么想加入这个队伍..." maxlength="500" show-word-limit />
      <div class="submit-wrap"><van-button type="primary" block round :loading="submitting" @click="handleSubmit">提交申请</van-button></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createJoinRequest } from '../api/join'
import { showToast } from 'vant'
const route = useRoute(); const router = useRouter(); const teamId = Number(route.params.postId)
const reason = ref(''); const submitting = ref(false)
const goBack = () => { router.back() }
const handleSubmit = async () => {
  if (!reason.value.trim()) { showToast('请填写申请理由'); return }
  submitting.value = true
  try { await createJoinRequest(teamId, reason.value); showToast('申请已提交'); router.back() }
  catch (e: any) { showToast(e.message || '提交失败') }
  finally { submitting.value = false }
}
</script>

<style scoped>
.apply-page { min-height: 100vh; }
.form-area { padding: 16px; }
.submit-wrap { margin-top: 24px; }
</style>
