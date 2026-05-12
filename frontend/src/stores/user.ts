import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getProfile, updateProfile, type UserVO, type LoginRequest, type RegisterRequest, type UserProfileUpdateRequest } from '../api/user'

export const useUserStore = defineStore('user', () => {
  const userId = ref<number | null>(Number(localStorage.getItem('userId')) || null)
  const token = ref<string | null>(localStorage.getItem('token') || null)
  const user = ref<UserVO | null>(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => !!userId.value && !!token.value)

  async function login(params: LoginRequest) {
    loading.value = true
    try {
      const res = await loginApi(params)
      userId.value = res.id
      token.value = 'jwt-token-' + res.id
      user.value = res
      localStorage.setItem('userId', String(res.id))
      localStorage.setItem('token', token.value)
      return res
    } finally {
      loading.value = false
    }
  }

  async function register(params: RegisterRequest) {
    loading.value = true
    try {
      return await registerApi(params)
    } finally {
      loading.value = false
    }
  }

  async function loadProfile() {
    if (!userId.value) return
    loading.value = true
    try {
      user.value = await getProfile()
    } catch { /* ignore */ } finally {
      loading.value = false
    }
  }

  async function saveProfile(params: UserProfileUpdateRequest) {
    if (!userId.value) return
    loading.value = true
    try {
      user.value = await updateProfile(params)
    } finally {
      loading.value = false
    }
  }

  function logout() {
    userId.value = null
    token.value = null
    user.value = null
    localStorage.removeItem('userId')
    localStorage.removeItem('token')
  }

  return { userId, token, user, loading, isLoggedIn, login, register, loadProfile, saveProfile, logout }
})
