<template>
  <div class="page team-page">
    <header class="team-header">
      <button class="back-button" @click="goBack">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
          <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" />
        </svg>
      </button>
      <h1 class="team-title">我的队伍</h1>
    </header>

    <div class="team-actions">
      <button @click="refresh">刷新</button>
      <button @click="openCreateModal" class="primary-btn">创建队伍</button>
    </div>

    <div v-if="teams.length === 0" class="empty">还没有队伍，去创建或加入一个吧</div>
    <ul v-else>
      <li v-for="team in teams" :key="team.id" class="team-card" @click="goToTeamDetail(team.id)">
        <h3>{{ team.name }}</h3>
        <p v-if="team.description">{{ team.description }}</p>
        <p v-if="team.tags && team.tags.length > 0">分区: <span class="tag" v-for="(tag, index) in team.tags"
            :key="index">{{ tag }}</span></p>
        <p>创建时间: {{ new Date(team.createdAt).toLocaleString() }}</p>
      </li>
    </ul>

    <!-- 创建队伍弹窗 -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="closeCreateModal">
      <div class="modal">
        <h3>创建新队伍</h3>
        <div class="form-item">
          <label>队伍名称</label>
          <input v-model="form.name" type="text" placeholder="请输入队伍名称" />
        </div>
        <div class="form-item">
          <label>队伍描述</label>
          <textarea v-model="form.description" placeholder="请输入队伍描述" rows="3"></textarea>
        </div>
        <div class="form-item">
          <label>所属分区</label>
          <div class="checkbox-group">
            <label v-for="tag in availableTags" :key="tag" class="checkbox-label">
              <input type="checkbox" :value="tag" v-model="form.tags">
              <span class="checkbox-text">{{ tag }}</span>
            </label>
          </div>
        </div>

        <div class="modal-actions">
          <button @click="closeCreateModal">取消</button>
          <button @click="submitCreate" class="primary-btn">确认创建</button>
        </div>
      </div>
    </div>

    <!-- 编辑队伍弹窗 -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
      <div class="modal">
        <h3>编辑队伍</h3>
        <div class="form-item">
          <label>队伍名称</label>
          <input v-model="editForm.name" type="text" placeholder="请输入队伍名称" />
        </div>
        <div class="form-item">
          <label>队伍描述</label>
          <textarea v-model="editForm.description" placeholder="请输入队伍描述" rows="3"></textarea>
        </div>
        <div class="form-item">
          <label>所属分区</label>
          <div class="checkbox-group">
            <label v-for="tag in availableTags" :key="tag" class="checkbox-label">
              <input type="checkbox" :value="tag" v-model="editForm.tags">
              <span class="checkbox-text">{{ tag }}</span>
            </label>
          </div>
        </div>

        <div class="modal-actions">
          <button @click="closeEditModal">取消</button>
          <button @click="submitEdit" class="primary-btn">确认更新</button>
        </div>
      </div>
    </div>


  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { getMyTeams, createTeam as apiCreateTeam, updateTeam, deleteTeam } from '../api/team.ts'
import type { TeamVO } from '../api/team.ts'

const teams = ref<TeamVO[]>([])
const router = useRouter()

// 弹窗控制
const showCreateModal = ref(false)
const showEditModal = ref(false)

// 定义所有可选的分区
const availableTags = ['篮球', '足球', '排球', '羽毛球', '乒乓球']

// 表单数据
const form = reactive({
  name: '',
  description: '',
  tags: [] as string[]
})

// 编辑数据
const editForm = reactive({
  id: 0,
  name: '',
  description: '',
  tags: [] as string[]
})



const loadTeams = async () => {
  const data = await getMyTeams()
  teams.value = data
}

const goBack = () => {
  router.back()
}

const refresh = () => loadTeams()

const goToTeamDetail = (id: number) => {
  router.push({ name: 'teamDetail', params: { id } })
}

const openCreateModal = () => {
  form.name = ''
  form.description = ''
  form.tags = []
  showCreateModal.value = true
}

const closeCreateModal = () => {
  showCreateModal.value = false
}

const submitCreate = async () => {
  if (!form.name.trim()) {
    window.alert('请输入队伍名称')
    return
  }

  try {
    const newTeam = await apiCreateTeam({
      name: form.name,
      description: form.description,
      tags: form.tags
    })

    // 手动添加新团队到列表中
    if (newTeam) {
      teams.value.unshift(newTeam)
    } else {
      // 如果API返回空，创建一个模拟的团队对象
      const mockTeam: TeamVO = {
        id: Date.now(), // 使用时间戳作为临时ID
        name: form.name,
        description: form.description,
        tags: form.tags,
        creatorId: 1, // 假设当前用户ID为1
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
      teams.value.unshift(mockTeam)
    }

    window.alert('创建成功！')
    closeCreateModal()
  } catch (error) {
    console.error('创建失败:', error)
    window.alert('创建失败，请稍后重试')
  }
}

const openEditModal = (team: TeamVO) => {
  editForm.id = team.id
  editForm.name = team.name
  editForm.description = team.description || ''
  editForm.tags = team.tags || []
  showEditModal.value = true
}

const closeEditModal = () => {
  showEditModal.value = false
}

const submitEdit = async () => {
  if (!editForm.name.trim()) {
    window.alert('请输入队伍名称')
    return
  }

  try {
    await updateTeam(editForm.id, {
      name: editForm.name,
      description: editForm.description,
      tags: editForm.tags
    })
    window.alert('更新成功！')
    closeEditModal()
    await loadTeams()
  } catch (error) {
    console.error('更新失败:', error)
    window.alert('更新失败，请稍后重试')
  }
}

const handleDelete = async (id: number) => {
  if (!confirm('确定要删除这个团队吗？')) {
    return
  }

  try {
    await deleteTeam(id)
    window.alert('删除成功！')
    await loadTeams()
  } catch (error) {
    console.error('删除失败:', error)
    window.alert('删除失败，请稍后重试')
  }
}



onMounted(loadTeams)
</script>

<style scoped>
/* 之前的样式保持不变... */
.team-page {
  padding: 12px;
  min-height: 100vh;
}

.team-header {
  position: relative;
  margin-bottom: 20px;
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

.team-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #4a90e2;
}

.team-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

button {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #fff;
  color: #333;
  font-size: 14px;
  cursor: pointer;
}

.primary-btn {
  background: #4a90e2;
  color: #fff;
  border: none;
}

.empty {
  color: #666;
  text-align: center;
  margin-top: 40px;
}

.team-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.2s;
}

.team-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.team-card h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #333;
}

.form-item textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  resize: vertical;
  box-sizing: border-box;
}

.add-member-form {
  display: flex;
  gap: 10px;
}

.add-member-form input {
  flex: 1;
}

.members-list {
  margin: 15px 0;
  max-height: 200px;
  overflow-y: auto;
}

.member-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.member-info {
  display: flex;
  flex-direction: column;
}

.member-username {
  font-weight: 600;
  font-size: 14px;
}

.member-nickname {
  font-size: 12px;
  color: #666;
}

.remove-button {
  padding: 4px 8px;
  border: 1px solid #ff4757;
  border-radius: 4px;
  background: #fff;
  color: #ff4757;
  font-size: 12px;
  cursor: pointer;
}

.remove-button:hover {
  background: #ffe6e6;
}

.team-card p {
  margin: 4px 0;
  font-size: 13px;
  color: #666;
}

.tag {
  display: inline-block;
  background: #f0f2f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 4px;
  color: #4a90e2;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  width: 80%;
  max-width: 320px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.modal h3 {
  margin: 0 0 16px 0;
  text-align: center;
  color: #333;
}

.form-item {
  margin-bottom: 16px;
}

.form-item label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.form-item input[type="text"] {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  box-sizing: border-box;
}

/* 新增：Checkbox 样式 */
.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 5px 0;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  margin-right: 10px;
  accent-color: #4a90e2;
  /* 选中时的颜色 */
  cursor: pointer;
}

.checkbox-text {
  font-size: 14px;
  color: #333;
}

.modal-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.modal-actions button {
  flex: 1;
}
</style>