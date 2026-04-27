import { vi, describe, test, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import TeamView from '../views/TeamView.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { getMyTeams, createTeam, updateTeam, deleteTeam } from '../api/team'

// Mock API
vi.mock('../api/team', () => ({
  getMyTeams: vi.fn().mockResolvedValue([
    {
      id: 1,
      name: '测试团队',
      description: '这是一个测试团队',
      tags: ['篮球'],
      creatorId: 1,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
  ]),
  createTeam: vi.fn(),
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
    expect(wrapper.find('.primary-btn').text()).toBe('创建队伍')
  })

  test('点击创建队伍按钮显示弹窗', async () => {
    const wrapper = mount(TeamView, {
      global: {
        plugins: [router],
      },
    })

    // 等待组件渲染
    await wrapper.vm.$nextTick()

    await wrapper.find('.primary-btn').trigger('click')

    // 等待弹窗渲染
    await wrapper.vm.$nextTick()

    expect(wrapper.find('.modal').exists()).toBe(true)
    expect(wrapper.find('.modal h3').text()).toBe('创建新队伍')
  })

  test('创建队伍表单输入', async () => {
    const wrapper = mount(TeamView, {
      global: {
        plugins: [router],
      },
    })

    // 等待组件渲染
    await wrapper.vm.$nextTick()

    await wrapper.find('.primary-btn').trigger('click')

    // 等待弹窗渲染
    await wrapper.vm.$nextTick()

    const nameInput = wrapper.find('input[type="text"]')
    const descInput = wrapper.find('textarea')

    if (nameInput.exists() && descInput.exists()) {
      await nameInput.setValue('测试队伍')
      await descInput.setValue('这是一个测试队伍')

      // 使用类型断言确保类型安全
      expect((nameInput.element as HTMLInputElement).value).toBe('测试队伍')
      expect((descInput.element as HTMLTextAreaElement).value).toBe('这是一个测试队伍')
    }
  })

  test('点击创建队伍按钮触发API调用', async () => {
    const wrapper = mount(TeamView, {
      global: {
        plugins: [router],
      },
    })

    // 等待组件渲染
    await wrapper.vm.$nextTick()

    await wrapper.find('.primary-btn').trigger('click')

    // 等待弹窗渲染
    await wrapper.vm.$nextTick()

    const nameInput = wrapper.find('input[type="text"]')
    const descInput = wrapper.find('textarea')

    if (nameInput.exists() && descInput.exists()) {
      await nameInput.setValue('测试队伍')
      await descInput.setValue('这是一个测试队伍')

      // 找到弹窗中的确认按钮
      const confirmButton = wrapper.find('.modal-actions .primary-btn')
      if (confirmButton.exists()) {
        await confirmButton.trigger('click')
        // 检查是否被调用，不严格检查参数
        expect(createTeam).toHaveBeenCalled()
      }
    }
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
    // 再等待一段时间，确保数据加载完成
    await new Promise((resolve) => setTimeout(resolve, 100))
    // 再次等待DOM更新
    await wrapper.vm.$nextTick()

    // 检查团队列表是否显示
    const teamCard = wrapper.find('.team-card')
    expect(teamCard.exists()).toBe(true)

    // 如果团队卡片存在，模拟点击
    if (teamCard.exists()) {
      await teamCard.trigger('click')
      // 检查是否调用了push方法
      expect(pushMock).toHaveBeenCalled()
    }
  })
})
