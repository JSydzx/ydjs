import axios from 'axios'

export const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  const userId = localStorage.getItem('userId')
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`
  }
  if (userId && config.headers) {
    config.headers['X-User-Id'] = userId
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const data = response.data
    // 后端 ErrorCode.SUCCESS = 200
    if (data && typeof data.code === 'number' && data.code !== 200) {
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data.data !== undefined ? data.data : data
  },
  (error) => {
    const msg = error.response?.data?.message || error.message || '网络错误'
    return Promise.reject(new Error(msg))
  }
)
