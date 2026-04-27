import axios from 'axios'

export const http = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

http.interceptors.request.use((config) => {
  console.log('API 请求发起:', config.method?.toUpperCase(), config.url)
  const userId = localStorage.getItem('userId')
  if (userId && config.headers) {
    config.headers['X-User-Id'] = userId
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    console.log('API 响应成功:', response.config.url, response.status)
    const data = response.data
    if (data && data.code && data.code !== 0) {
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data.data ?? data
  },
  (error) => {
    console.log('API 响应失败:', error.config?.url, error.message)
    // Mock 成功响应（即使后端不存在）
    if (error.code === 'ERR_NETWORK') {
      console.log('🔄 Mock 模拟响应（后端不存在）')
      const mockData = getMockData(error.config.url)
      return Promise.resolve(mockData)
    }
    return Promise.reject(error)
  }
)
// Mock 数据生成函数
function getMockData(url: string | undefined) {
  if (!url) return {}

  if (url.includes('/posts')) {
    return {
      total: 20,
      items: Array(5)
        .fill(0)
        .map((_, i) => ({
          id: i + 1,
          title: `Mock 帖子 ${i + 1}`,
          preview: '这是Mock的帖子预览内容...',
          meta: '2026-03-30',
          avatar: 'https://via.placeholder.com/40',
          nickname: `用户${i + 1}`,
          imgUrl: 'https://via.placeholder.com/320x180',
          views: Math.floor(Math.random() * 1000),
          likes: Math.floor(Math.random() * 200),
          shares: Math.floor(Math.random() * 10),
          category: '全部',
          userId: i + 100,
        })),
    }
  }

  if (url.includes('/announcements')) {
    return {
      total: 5,
      items: Array(3)
        .fill(0)
        .map((_, i) => ({
          id: i + 1,
          title: `Mock 公告 ${i + 1}`,
          summary: '这是Mock的公告摘要...',
        })),
    }
  }

  if (url.includes('/schedule')) {
    return {
      days: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      slots: [
        {
          time: '上午',
          events: [
            { id: 1, title: 'Mock 活动1' },
            { id: 2, title: 'Mock 活动2' },
          ],
        },
        { time: '下午', events: [{ id: 3, title: 'Mock 活动3' }] },
        { time: '晚上', events: [] },
      ],
    }
  }

  if (url.includes('/teams')) {
    return {
      items: Array(3)
        .fill(0)
        .map((_, i) => ({
          id: i + 1,
          name: `Mock 队伍 ${i + 1}`,
          memberCount: Math.floor(Math.random() * 10) + 1,
          eventCount: Math.floor(Math.random() * 5),
        })),
    }
  }

  if (url.includes('/user/profile')) {
    return {
      name: 'Mock 用户',
      school: 'Mock 大学',
      postCount: 10,
      followCount: 5,
      favoriteCount: 3,
    }
  }

  if (url.includes('/user/login')) {
    return {
      id: 1,
      username: 'abc',
      nickname: '测试用户',
      email: 'test@example.com',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    }
  }

  if (url.includes('/team/my')) {
    return [
      {
        id: 1,
        name: '篮球队',
        description: '我们是一支热爱篮球的队伍，欢迎加入！',
        tags: ['篮球'],
        creatorId: 1,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      },
      {
        id: 2,
        name: '足球队',
        description: '足球爱好者的聚集地',
        tags: ['足球'],
        creatorId: 1,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      },
    ]
  }

  return {}
}
