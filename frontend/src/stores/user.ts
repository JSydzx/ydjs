import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  login as loginApi,
  register as registerApi,
  getProfile,
  updateProfile,
  type UserVO,
  type LoginRequest,
  type RegisterRequest,
  type UserProfileUpdateRequest,
} from '../api/user'

export const useUserStore = defineStore('user', () => {
  const userId = ref<number | null>(Number(localStorage.getItem('userId')) || null)
  const token = ref<string | null>(localStorage.getItem('token') || null)
  const user = ref<UserVO | null>(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => !!userId.value && !!token.value)

  async function login(params: LoginRequest) {
    loading.value = true
    try {
      const response = await loginApi(params)
      userId.value = response.user.id
      token.value = response.token
      user.value = response.user
      localStorage.setItem('userId', String(response.user.id))
      localStorage.setItem('token', response.token)
      return response.user
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
    if (!token.value) return
    loading.value = true
    try {
      user.value = await getProfile()
      userId.value = user.value.id
      localStorage.setItem('userId', String(user.value.id))
    } catch {
      logout()
    } finally {
      loading.value = false
    }
  }

  async function saveProfile(params: UserProfileUpdateRequest) {
    if (!token.value) return
    loading.value = true
    try {
      user.value = await updateProfile(params)
      return user.value
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

  function syncFromStorage() {
    userId.value = Number(localStorage.getItem('userId')) || null
    token.value = localStorage.getItem('token') || null
    if (!token.value) user.value = null
  }

  return { userId, token, user, loading, isLoggedIn, login, register, loadProfile, saveProfile, logout, syncFromStorage }
})
