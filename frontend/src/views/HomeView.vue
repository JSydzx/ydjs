<template>
  <div class="page home-page">
    <header class="top-bar">
      <h1>组队广场</h1>
      <p class="top-sub">发现感兴趣的队伍，快速申请加入</p>
    </header>

    <section class="filter-panel">
      <van-search
        v-model="keyword"
        placeholder="搜索队伍名、描述或标签"
        shape="round"
        clearable
        @search="reloadTeams"
        @clear="reloadTeams"
      />
      <div class="tag-row">
        <button
          v-for="option in tagOptions"
          :key="option"
          class="tag-chip"
          :class="{ active: activeTag === option }"
          @click="toggleTag(option)"
        >
          {{ option }}
        </button>
      </div>
      <van-checkbox v-model="availableOnly" shape="square" @change="reloadTeams">
        只看招募中且未满员
      </van-checkbox>
    </section>

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="loadTeams">
        <div v-if="teams.length === 0 && !loading" class="empty-state">
          <div class="empty-icon">暂无</div>
          <p class="empty-text">没有匹配的队伍</p>
          <p class="empty-hint">换个关键词，或者发布一个新队伍</p>
        </div>

        <div v-for="team in teams" :key="team.id" class="team-card" @click="goDetail(team.id)">
          <div class="card-header">
            <h3 class="card-title">{{ team.name }}</h3>
            <van-tag :type="team.status === 'ACTIVE' ? 'success' : 'default'" size="medium">
              {{ team.status === 'ACTIVE' ? '招募中' : '已关闭' }}
            </van-tag>
          </div>
          <p class="card-desc">{{ team.description || '暂无描述' }}</p>
          <div class="card-footer">
            <div class="card-tags">
              <van-tag v-if="team.tag" type="primary" size="medium" plain>{{ team.tag }}</van-tag>
            </div>
            <div class="card-meta">
              <span class="member-count">{{ team.currentMembers }}/{{ team.maxMembers }}人</span>
              <span class="creator-name">{{ team.creatorName || `UID:${team.creatorId}` }}</span>
            </div>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getTeamList, type TeamVO } from '../api/team'
import { showToast } from 'vant'

const router = useRouter()
const teams = ref<TeamVO[]>([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const keyword = ref('')
const activeTag = ref('')
const availableOnly = ref(false)
const tagOptions = ['算法', '前端', '后端', '设计', '竞赛', '创业']

let searchTimer: number | undefined

const buildParams = () => ({
  keyword: keyword.value.trim() || undefined,
  tag: activeTag.value || undefined,
  availableOnly: availableOnly.value || undefined,
})

const loadTeams = async () => {
  loading.value = true
  try {
    teams.value = await getTeamList(buildParams())
    finished.value = true
  } catch (e: any) {
    showToast(e.message || '加载失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

const reloadTeams = async () => {
  finished.value = false
  await loadTeams()
}

const onRefresh = async () => {
  refreshing.value = true
  try {
    await reloadTeams()
  } finally {
    refreshing.value = false
  }
}

const toggleTag = (tag: string) => {
  activeTag.value = activeTag.value === tag ? '' : tag
  reloadTeams()
}

const goDetail = (id: number) => {
  router.push({ name: 'postDetail', params: { id } })
}

watch(keyword, () => {
  window.clearTimeout(searchTimer)
  searchTimer = window.setTimeout(() => reloadTeams(), 350)
})
</script>

<style scoped>
.home-page {
  padding: 16px 12px;
}

.top-bar {
  margin-bottom: 12px;
}

.top-bar h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text);
}

.top-sub {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--color-text-muted);
}

.filter-panel {
  background: var(--color-white);
  border-radius: var(--radius-md);
  padding: 8px 10px 12px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-sm);
}

.tag-row {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 4px 0 10px;
}

.tag-chip {
  border: 1px solid var(--color-divider);
  background: var(--color-white);
  color: var(--color-text-secondary);
  border-radius: 999px;
  padding: 5px 12px;
  font-size: 12px;
  white-space: nowrap;
}

.tag-chip.active {
  border-color: var(--color-primary);
  background: rgba(98, 87, 255, 0.12);
  color: var(--color-primary);
  font-weight: 600;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: var(--color-white);
  color: var(--color-text-muted);
  margin-bottom: 12px;
}

.empty-text {
  margin: 0;
  font-size: 16px;
  color: var(--color-text-secondary);
}

.empty-hint {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--color-text-muted);
}

.team-card {
  background: var(--color-white);
  border-radius: var(--radius-md);
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
}

.card-header,
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.card-header {
  margin-bottom: 8px;
}

.card-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-desc {
  margin: 0 0 12px;
  font-size: 13px;
  color: var(--color-text-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  gap: 8px;
  font-size: 12px;
}

.member-count {
  color: var(--color-primary);
  font-weight: 500;
}

.creator-name {
  color: var(--color-text-muted);
}
</style>
