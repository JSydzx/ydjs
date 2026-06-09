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
          maxlength="100"
          :rules="[{ required: true, message: '请输入队伍名称' }, { validator: validateName, message: '队伍名称至少2个字' }]"
        />
        <van-field
          v-model="form.tag"
          name="tag"
          label="标签"
          placeholder="例如：算法、前端、竞赛"
          maxlength="255"
          :rules="[{ required: true, message: '请输入标签，便于别人搜索' }]"
        />
        <van-field
          v-model.number="form.maxMembers"
          name="maxMembers"
          label="最大人数"
          placeholder="建议 2-20 人"
          type="digit"
          :rules="[{ validator: validateMaxMembers, message: '人数范围为2-200' }]"
        />
        <van-field
          v-model="form.description"
          name="description"
          label="队伍描述"
          placeholder="介绍目标、分工、要求和联系方式"
          type="textarea"
          rows="4"
          autosize
          maxlength="1000"
          show-word-limit
        />
      </van-cell-group>

      <div class="draft-hint" v-if="hasDraft">草稿已自动保存</div>

      <div class="submit-wrap">
        <van-button round block type="primary" native-type="submit" :loading="submitting" size="large">
          发布
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { createTeam } from '../api/team'
import { showToast } from 'vant'

const DRAFT_KEY = 'publishTeamDraft'

const router = useRouter()
const submitting = ref(false)
const form = reactive(loadDraft())

const hasDraft = computed(() => !!form.name || !!form.description || !!form.tag)

function loadDraft() {
  const rawDraft = localStorage.getItem(DRAFT_KEY)
  if (!rawDraft) {
    return { name: '', description: '', tag: '', maxMembers: 10 }
  }
  try {
    return { name: '', description: '', tag: '', maxMembers: 10, ...JSON.parse(rawDraft) }
  } catch {
    return { name: '', description: '', tag: '', maxMembers: 10 }
  }
}

const validateName = (value: string) => value.trim().length >= 2
const validateMaxMembers = (value: number) => Number(value) >= 2 && Number(value) <= 200

const handleSubmit = async () => {
  submitting.value = true
  try {
    await createTeam({
      name: form.name.trim(),
      description: form.description.trim() || undefined,
      tag: form.tag.trim(),
      maxMembers: Number(form.maxMembers) || 10,
    })
    localStorage.removeItem(DRAFT_KEY)
    form.name = ''
    form.description = ''
    form.tag = ''
    form.maxMembers = 10
    showToast('发布成功')
    router.push({ name: 'home' })
  } catch (e: any) {
    showToast(e.message || '发布失败')
  } finally {
    submitting.value = false
  }
}

watch(
  form,
  () => {
    localStorage.setItem(DRAFT_KEY, JSON.stringify(form))
  },
  { deep: true }
)
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

.draft-hint {
  margin: 12px;
  font-size: 12px;
  color: var(--color-text-muted);
}

.submit-wrap {
  margin: 24px 12px;
}
</style>
