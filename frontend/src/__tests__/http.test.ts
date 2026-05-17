import { describe, it, expect } from 'vitest'
import { http } from '../api/http'

describe('HTTP 配置测试', () => {
  it('应该配置正确的 baseURL', () => {
    expect(http.defaults.baseURL).toBe('/api')
  })

  it('应该有超时配置', () => {
    expect(http.defaults.timeout).toBeDefined()
  })

  it('应该能创建实例', () => {
    expect(http).toBeDefined()
    expect(typeof http.get).toBe('function')
    expect(typeof http.post).toBe('function')
    expect(typeof http.put).toBe('function')
    expect(typeof http.delete).toBe('function')
  })
})
