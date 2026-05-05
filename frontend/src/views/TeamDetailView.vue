<template>
  <div class="page team-detail-page">
    <header class="team-detail-header">
      <button
        class="back-button"
        @click="goBack"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="currentColor"
        >
          <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" />
        </svg>
      </button>
      <h1 class="team-detail-title">
        团队详情
      </h1>
    </header>

    <section class="team-info">
      <h2 class="team-name">
        {{ team.name }}
      </h2>
      <p
        v-if="team.description"
        class="team-description"
      >
        {{ team.description }}
      </p>
      <p
        v-if="team.tags && team.tags.length > 0"
        class="team-tags"
      >
        标签: <span
          v-for="(tag, index) in team.tags"
          :key="index"
          class="tag"
        >{{ tag }}</span>
      </p>
      <p class="team-creator">
        创建者: UID {{ team.creatorId }}
      </p>
      <p class="team-time">
        创建时间: {{ new Date(team.createdAt).toLocaleString() }}
      </p>
    </section>

    <section class="team-actions">
      <button
        v-if="isCreator"
        class="action-button edit"
        @click="openEditModal"
      >
        编辑团队
      </button>
      <button
        v-if="isCreator"
        class="action-button delete"
        @click="handleDelete"
      >
        删除团队
      </button>
      <button
        class="action-button members"
        @click="openMembersModal"
      >
        成员管理
      </button>
    </section>

    <!-- 编辑团队弹窗 -->
    <div
      v-if="showEditModal"
      class="modal-overlay"
      @click.self="closeEditModal"
    >
      <div class="modal">
        <h3>编辑团队</h3>
        <div class="form-item">
          <label>团队名称</label>
          <input
            v-model="editForm.name"
            type="text"
            placeholder="请输入团队名称"
          >
        </div>
        <div class="form-item">
          <label>团队描述</label>
          <textarea
            v-model="editForm.description"
            placeholder="请输入团队描述"
            rows="3"
          />
        </div>
        <div class="form-item">
          <label>所属分区</label>
          <div class="checkbox-group">
            <label
              v-for="tag in availableTags"
              :key="tag"
              class="checkbox-label"
            >
              <input
                v-model="editForm.tags"
                type="checkbox"
                :value="tag"
              >
              <span class="checkbox-text">{{ tag }}</span>
            </label>
          </div>
        </div>

        <div class="modal-actions">
          <button @click="closeEditModal">
            取消
          </button>
          <button
            class="primary-btn"
            @click="submitEdit"
          >
            确认更新
          </button>
        </div>
      </div>
    </div>

    <!-- 成员管理弹窗 -->
    <div
      v-if="showMembersModal"
      class="modal-overlay"
      @click.self="closeMembersModal"
    >
      <div class="modal">
        <h3>团队成员</h3>

        <!-- 添加成员表单 -->
        <div class="form-item">
          <label>添加成员 (通过UID)</label>
          <div class="add-member-form">
            <input
              v-model="addMemberForm.userId"
              type="number"
              placeholder="请输入用户UID"
            >
            <button
              class="primary-btn"
              @click="submitAddMember"
            >
              邀请
            </button>
          </div>
        </div>

        <!-- 成员列表 -->
        <div class="members-list">
          <div
            v-if="teamMembers.length === 0"
            class="empty"
          >
            暂无成员
          </div>
          <ul v-else>
            <li
              v-for="member in teamMembers"
              :key="member.id"
              class="member-item"
            >
              <div class="member-info">
                <img
                  :src="member.avatar || 'https://via.placeholder.com/40'"
                  alt="头像"
                  class="member-avatar"
                >
                <div class="member-details">
                  <span class="member-username">{{ member.username }}</span>
                  <span
                    v-if="member.nickname"
                    class="member-nickname"
                  >{{ member.nickname }}</span>
                  <span class="member-uid">UID: {{ member.id }}</span>
                </div>
              </div>
              <button
                v-if="isCreator"
                class="remove-button"
                @click="handleRemoveMember(member.id)"
              >
                移除
              </button>
            </li>
          </ul>
        </div>

        <div class="modal-actions">
          <button
            class="primary-btn"
            @click="closeMembersModal"
          >
            关闭
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTeamDetail, updateTeam, deleteTeam, getTeamMembers, addTeamMember, removeTeamMember, TeamVO, UserVO } from '../api/team.ts'

const route = useRoute()
const router = useRouter()
const teamId = Number(route.params.id)

const team = ref<TeamVO>({
  id: teamId,
  name: '',
  description: '',
  tags: [],
  creatorId: 0,
  createdAt: '',
  updatedAt: ''
})

const teamMembers = ref<UserVO[]>([])
const showEditModal = ref(false)
const showMembersModal = ref(false)

// 定义所有可选的分区
const availableTags = ['篮球', '足球', '排球', '羽毛球', '乒乓球']

// 编辑数据
const editForm = reactive({
  name: '',
  description: '',
  tags: [] as string[]
})

// 添加成员表单
const addMemberForm = reactive({
  userId: 0
})

// 判断当前用户是否是团队创建者
const isCreator = computed(() => {
  const currentUserId = Number(localStorage.getItem('userId'))
  return currentUserId === team.value.creatorId
})

const loadTeamDetail = async () => {
  try {
    const data = await getTeamDetail(teamId)
    team.value = data
  } catch (error) {
    console.error('获取团队详情失败:', error)
    // 使用模拟数据
    team.value = {
      id: teamId,
      name: `团队${teamId}`,
      description: `这是团队${teamId}的描述`,
      tags: ['篮球'],
      creatorId: 1,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
  }
}

const loadTeamMembers = async () => {
  try {
    const members = await getTeamMembers(teamId)
    teamMembers.value = members
  } catch (error) {
    console.error('获取团队成员失败:', error)
    // 使用模拟数据
    teamMembers.value = [
      {
        id: 1,
        username: 'abc',
        nickname: '测试用户',
        avatar: 'https://via.placeholder.com/40'
      },
      {
        id: 2,
        username: 'user2',
        nickname: '用户2',
        avatar: 'https://via.placeholder.com/40'
      }
    ]
  }
}

const goBack = () => {
  router.back()
}

const openEditModal = () => {
  editForm.name = team.value.name
  editForm.description = team.value.description || ''
  editForm.tags = team.value.tags || []
  showEditModal.value = true
}

const closeEditModal = () => {
  showEditModal.value = false
}

const submitEdit = async () => {
  if (!editForm.name.trim()) {
    window.alert('请输入团队名称')
    return
  }

  try {
    const updatedTeam = await updateTeam(teamId, {
      name: editForm.name,
      description: editForm.description,
      tags: editForm.tags
    })
    team.value = updatedTeam
    window.alert('更新成功！')
    closeEditModal()
  } catch (error) {
    console.error('更新失败:', error)
    window.alert('更新失败，请稍后重试')
  }
}

const handleDelete = async () => {
  if (!confirm('确定要删除这个团队吗？')) {
    return
  }

  try {
    await deleteTeam(teamId)
    window.alert('删除成功！')
    router.push({ name: 'team' })
  } catch (error) {
    console.error('删除失败:', error)
    window.alert('删除失败，请稍后重试')
  }
}

const openMembersModal = () => {
  loadTeamMembers()
  showMembersModal.value = true
}

const closeMembersModal = () => {
  showMembersModal.value = false
}

const submitAddMember = async () => {
  if (!addMemberForm.userId) {
    window.alert('请输入用户UID')
    return
  }

  try {
    await addTeamMember(teamId, {
      userId: addMemberForm.userId
    })
    window.alert('邀请发送成功！')
    addMemberForm.userId = 0
    // 重新加载成员列表
    await loadTeamMembers()
  } catch (error) {
    console.error('添加成员失败:', error)
    window.alert('添加成员失败，请稍后重试')
  }
}

const handleRemoveMember = async (userId: number) => {
  if (!confirm('确定要移除这个成员吗？')) {
    return
  }

  try {
    await removeTeamMember(teamId, userId)
    window.alert('移除成员成功！')
    // 重新加载成员列表
    await loadTeamMembers()
  } catch (error) {
    console.error('移除成员失败:', error)
    window.alert('移除成员失败，请稍后重试')
  }
}

onMounted(() => {
  loadTeamDetail()
})
</script>

<style scoped>
.team-detail-page {
  padding: 12px;
  min-height: 100vh;
}

.team-detail-header {
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

.team-detail-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #4a90e2;
  padding-top: 5px;
}

.team-info {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.team-name {
  margin: 0 0 10px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.team-description {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.team-tags {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #666;
}

.tag {
  display: inline-block;
  background: #f0f2f5;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 8px;
  color: #4a90e2;
}

.team-creator,
.team-time {
  margin: 5px 0;
  font-size: 14px;
  color: #666;
}

.team-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}

.action-button {
  flex: 1;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #fff;
  color: #333;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-button.edit {
  background: #4a90e2;
  color: white;
  border-color: #4a90e2;
}

.action-button.delete {
  background: #ff4757;
  color: white;
  border-color: #ff4757;
}

.action-button.members {
  background: #2ecc71;
  color: white;
  border-color: #2ecc71;
}

.action-button:hover {
  opacity: 0.9;
  transform: translateY(-2px);
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
  max-height: 250px;
  overflow-y: auto;
}

.member-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.member-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.member-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.member-details {
  display: flex;
  flex-direction: column;
}

.member-username {
  font-weight: 600;
  font-size: 14px;
  color: #333;
}

.member-nickname {
  font-size: 12px;
  color: #666;
}

.member-uid {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.remove-button {
  padding: 6px 12px;
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

.empty {
  text-align: center;
  color: #999;
  padding: 20px 0;
}
</style>