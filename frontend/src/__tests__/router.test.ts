import { describe, it, expect } from 'vitest'
import router from '../router'

describe('Router 测试', () => {
  it('应该有正确的路由配置', () => {
    const routes = router.getRoutes()
    expect(routes.length).toBeGreaterThan(0)
  })

  it('应该包含必要的路由', () => {
    const routes = router.getRoutes()
    const routeNames = routes.map((r) => r.name)

    expect(routeNames).toContain('home')
    expect(routeNames).toContain('login')
    expect(routeNames).toContain('register')
    expect(routeNames).toContain('profile')
  })

  it('应该有正确的重定向路由', () => {
    const routes = router.getRoutes()
    const redirectRoute = routes.find((r) => r.path === '/')
    expect(redirectRoute?.redirect).toBe('/home')
  })
})
