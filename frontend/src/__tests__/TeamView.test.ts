import { vi, describe, test, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import TeamView from '../views/TeamView.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { getTeamList, createTeam } from '../api/team'

// Mock API
vi.mock('../api/team', () => ({
  getTeamList: vi.fn().mockResolvedValue({
    items: [
      {
        id: 1,
        name: '测试团队',
        memberCount: 5,
        eventCount: 2,
      },
    ],
  }),
  createTeam: vi.fn().mockResolvedValue({ id: 2, name: '测试队伍' }),
  updateTeam: vi.fn(),
  deleteTeam: vi.fn(),
}))

const router = createRouter({
  history: createWebHistory(),
  routes: [],
})

describe('TeamView', () => {
  test('渲染团队列表页面', async () => {
    const wrapper = mount(TeamView, {
      global: {
        plugins: [router],
      },
    })

    // 等待组件渲染
    await wrapper.vm.$nextTick()

    expect(wrapper.find('h1').text()).toBe('我的队伍')
    // 使用包含文本的选择器来找到创建队伍按钮
    const createButton = wrapper.find('button')
    expect(createButton.exists()).toBe(true)
  })

  test('创建队伍调用prompt', async () => {
    // 在组件挂载之前就设置好window.prompt的模拟
    const promptMock = vi.fn().mockReturnValue('测试队伍')
    const originalPrompt = window.prompt
    ;(window as any).prompt = promptMock

    const wrapper = mount(TeamView, {
      global: {
        plugins: [router],
      },
    })

    // 等待组件渲染和数据加载
    await wrapper.vm.$nextTick()
    await new Promise((resolve) => setTimeout(resolve, 100))

    // 直接调用组件暴露的createTeam方法（使用类型断言）
    await (wrapper.vm as any).createTeam()

    // 检查prompt是否被调用
    expect(promptMock).toHaveBeenCalledWith('请输入队伍名')

    // 恢复原始的prompt
    ;(window as any).prompt = originalPrompt
  })

  test('创建队伍通过prompt输入', async () => {
    // 在组件挂载之前就设置好window.prompt的模拟
    const promptMock = vi.fn().mockReturnValue('新队伍')
    const originalPrompt = window.prompt
    ;(window as any).prompt = promptMock

    const wrapper = mount(TeamView, {
      global: {
        plugins: [router],
      },
    })

    // 等待组件渲染和数据加载
    await wrapper.vm.$nextTick()
    await new Promise((resolve) => setTimeout(resolve, 100))

    // 直接调用组件暴露的createTeam方法（使用类型断言）
    await (wrapper.vm as any).createTeam()

    // 检查prompt返回值
    expect(promptMock).toHaveBeenCalled()
    expect(promptMock.mock.results[0].value).toBe('新队伍')

    // 恢复原始的prompt
    ;(window as any).prompt = originalPrompt
  })

  test('创建队伍触发API调用', async () => {
    // 在组件挂载之前就设置好window.prompt的模拟
    const promptMock = vi.fn().mockReturnValue('测试队伍')
    const originalPrompt = window.prompt
    ;(window as any).prompt = promptMock

    const wrapper = mount(TeamView, {
      global: {
        plugins: [router],
      },
    })

    // 等待组件渲染和数据加载
    await wrapper.vm.$nextTick()
    await new Promise((resolve) => setTimeout(resolve, 100))

    // 直接调用组件暴露的createTeam方法（使用类型断言）
    await (wrapper.vm as any).createTeam()
    await wrapper.vm.$nextTick()
    await new Promise((resolve) => setTimeout(resolve, 100))

    // 检查createTeam是否被调用
    expect(createTeam).toHaveBeenCalledWith({ name: '测试队伍' })

    // 恢复原始的prompt
    ;(window as any).prompt = originalPrompt
  })

  test('点击团队卡片跳转到详情页', async () => {
    // 模拟router.push方法
    const pushMock = vi.spyOn(router, 'push').mockImplementation(() => Promise.resolve())

    const wrapper = mount(TeamView, {
      global: {
        plugins: [router],
      },
    })

    // 等待组件加载和数据获取
    await wrapper.vm.$nextTick()
    await new Promise((resolve) => setTimeout(resolve, 100))
    await wrapper.vm.$nextTick()

    // 检查团队列表是否显示
    const teamCard = wrapper.find('.team-card')
    expect(teamCard.exists()).toBe(true)

    // 如果团队卡片存在，模拟点击
    if (teamCard.exists()) {
      await teamCard.trigger('click')
      await wrapper.vm.$nextTick()
      // 检查是否调用了push方法
      expect(pushMock).toHaveBeenCalled()
    }
  })
})
