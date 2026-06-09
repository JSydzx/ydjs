import axios from 'axios'

export const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data && typeof data.code === 'number' && data.code !== 200) {
      if (data.code === 401) {
        clearLoginState()
      }
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data?.data !== undefined ? data.data : data
  },
  (error) => {
    if (error.response?.status === 401) {
      clearLoginState()
      return Promise.reject(new Error(error.response?.data?.message || '登录已过期，请重新登录'))
    }
    const msg = error.response?.data?.message || error.message || '网络错误'
    return Promise.reject(new Error(msg))
  }
)

function clearLoginState() {
  localStorage.removeItem('userId')
  localStorage.removeItem('token')
  if (window.location.pathname !== '/login') {
    window.dispatchEvent(new CustomEvent('auth-expired'))
  }
}
