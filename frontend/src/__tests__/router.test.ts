import { describe, it, expect } from 'vitest'
import router from '../router'

describe('Router 测试', () => {
  it('应该有正确的路由配置', () => {
    const routes = router.getRoutes()
    expect(routes.length).toBeGreaterThan(0)
  })

  it('应该包含必要的路由', () => {
    const routes = router.getRoutes()
    const routeNames = routes.map((route) => route.name)

    expect(routeNames).toContain('home')
    expect(routeNames).toContain('login')
    expect(routeNames).toContain('register')
    expect(routeNames).toContain('profile')
    expect(routeNames).toContain('teamDetail')
  })

  it('根路径应该直接展示首页', () => {
    const routes = router.getRoutes()
    const homeRoute = routes.find((route) => route.path === '/')
    expect(homeRoute?.name).toBe('home')
  })
})
