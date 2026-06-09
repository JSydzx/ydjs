import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as userApi from '../api/user'
import * as teamApi from '../api/team'
import * as joinApi from '../api/join'
import * as notificationApi from '../api/notification'

vi.mock('axios', () => {
  const instance = {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    interceptors: {
      request: { use: vi.fn() },
      response: { use: vi.fn() },
    },
  }
  return {
    default: {
      create: vi.fn(() => instance),
    },
  }
})

describe('API 模块测试', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('user API 应该导出正确的函数', () => {
    expect(typeof userApi.login).toBe('function')
    expect(typeof userApi.register).toBe('function')
    expect(typeof userApi.getProfile).toBe('function')
  })

  it('team API 应该导出正确的函数', () => {
    expect(typeof teamApi.createTeam).toBe('function')
    expect(typeof teamApi.getTeamList).toBe('function')
    expect(typeof teamApi.updateTeam).toBe('function')
  })

  it('join API 应该导出正确的函数', () => {
    expect(typeof joinApi.createJoinRequest).toBe('function')
    expect(typeof joinApi.getMyJoinRequests).toBe('function')
  })

  it('notification API 应该导出正确的函数', () => {
    expect(typeof notificationApi.getNotifications).toBe('function')
    expect(typeof notificationApi.markRead).toBe('function')
  })
})
