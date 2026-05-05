import axios from 'axios'

axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  const userId = localStorage.getItem('userId') // 前端需要存储 userId

  if (config.headers) {
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    // 关键：后端要求必须传这个
    if (userId) {
      config.headers['X-User-Id'] = userId
    }
  }
  return config
})
