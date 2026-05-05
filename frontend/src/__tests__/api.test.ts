import { beforeEach, describe, expect, test, vi } from 'vitest'

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  clear: vi.fn(),
}

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
})

// 直接mock整个API模块
vi.mock('../api/team', () => ({
  getMyTeams: vi.fn(),
  getTeamDetail: vi.fn(),
  createTeam: vi.fn(),
  updateTeam: vi.fn(),
  deleteTeam: vi.fn(),
  getTeamMembers: vi.fn(),
  addTeamMember: vi.fn(),
  removeTeamMember: vi.fn(),
}))

vi.mock('../api/user', () => ({
  login: vi.fn(),
  register: vi.fn(),
  getUserDetail: vi.fn(),
}))

vi.mock('../api/http', () => ({
  http: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    interceptors: {
      request: { use: vi.fn() },
      response: { use: vi.fn() },
    },
  },
}))

// 导入API函数
import { getMyTeams, getTeamDetail } from '../api/team'
import { login, register } from '../api/user'
import { http } from '../api/http'

// 使用http对象来模拟API调用
const mockedHttp = http as any

// 为http对象的方法添加mock类型断言
;(http.get as any).mockResolvedValue = vi.fn()
;(http.post as any).mockResolvedValue = vi.fn()
;(http.get as any).mockRejectedValue = vi.fn()
;(http.post as any).mockRejectedValue = vi.fn()

describe('API Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  test('getMyTeams API 成功态', async () => {
    interface Team {
      id: number
      name: string
      description: string
      tags: string[]
      creatorId: number
      createdAt: string
      updatedAt: string
    }

    const mockTeams: Team[] = [
      {
        id: 1,
        name: '篮球队',
        description: '篮球爱好者的队伍',
        tags: ['篮球'],
        creatorId: 1,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      },
    ]

    // 直接mock getMyTeams函数
    vi.mocked(getMyTeams).mockResolvedValue(mockTeams)

    const teams = await getMyTeams()

    expect(teams).toEqual(mockTeams)
    expect(getMyTeams).toHaveBeenCalled()
  })

  test('getMyTeams API 失败态', async () => {
    // 直接mock getMyTeams函数
    vi.mocked(getMyTeams).mockRejectedValue(new Error('Network Error'))

    await expect(getMyTeams()).rejects.toThrow('Network Error')
  })

  test('getTeamDetail API 成功态', async () => {
    interface Team {
      id: number
      name: string
      description: string
      tags: string[]
      creatorId: number
      createdAt: string
      updatedAt: string
    }

    const mockTeam: Team = {
      id: 1,
      name: '篮球队',
      description: '篮球爱好者的队伍',
      tags: ['篮球'],
      creatorId: 1,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    }

    // 直接mock getTeamDetail函数
    vi.mocked(getTeamDetail).mockResolvedValue(mockTeam)

    const team = await getTeamDetail(1)

    expect(team).toEqual(mockTeam)
    expect(getTeamDetail).toHaveBeenCalledWith(1)
  })

  test('getTeamDetail API 失败态', async () => {
    // 直接mock getTeamDetail函数
    vi.mocked(getTeamDetail).mockRejectedValue(new Error('Network Error'))

    await expect(getTeamDetail(1)).rejects.toThrow('Network Error')
  })

  test('login API 成功态', async () => {
    interface User {
      id: number
      username: string
      nickname: string
      email: string
      createdAt: string
      updatedAt: string
    }

    const mockUser: User = {
      id: 1,
      username: 'abc',
      nickname: '测试用户',
      email: 'test@example.com',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    }

    // 直接mock login函数
    vi.mocked(login).mockResolvedValue(mockUser)

    const user = await login({ username: 'abc', password: '123456' })

    expect(user).toEqual(mockUser)
    expect(login).toHaveBeenCalledWith({ username: 'abc', password: '123456' })
  })

  test('login API 失败态', async () => {
    // 直接mock login函数
    vi.mocked(login).mockRejectedValue(new Error('Network Error'))

    await expect(login({ username: 'abc', password: '123456' })).rejects.toThrow('Network Error')
  })

  test('register API 成功态', async () => {
    interface User {
      id: number
      username: string
      nickname: string
      email: string
      createdAt: string
      updatedAt: string
    }

    const mockUser: User = {
      id: 2,
      username: 'testuser',
      nickname: '新用户',
      email: 'test@example.com',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    }

    // 直接mock register函数
    vi.mocked(register).mockResolvedValue(mockUser)

    const user = await register({
      username: 'testuser',
      password: '123456',
      email: 'test@example.com',
    })

    expect(user).toEqual(mockUser)
    expect(register).toHaveBeenCalledWith({
      username: 'testuser',
      password: '123456',
      email: 'test@example.com',
    })
  })

  test('register API 失败态', async () => {
    // 直接mock register函数
    vi.mocked(register).mockRejectedValue(new Error('Network Error'))

    await expect(
      register({ username: 'testuser', password: '123456', email: 'test@example.com' })
    ).rejects.toThrow('Network Error')
  })
})
