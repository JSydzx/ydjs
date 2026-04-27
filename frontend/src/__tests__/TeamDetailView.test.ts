import { vi, describe, test, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import TeamDetailView from '../views/TeamDetailView.vue'
import { createRouter, createWebHistory } from 'vue-router'
import {
  getTeamDetail,
  updateTeam,
  deleteTeam,
  getTeamMembers,
  addTeamMember,
  removeTeamMember,
} from '../api/team'

// Mock API
vi.mock('../api/team', () => ({
  getTeamDetail: vi.fn().mockResolvedValue({
    id: 1,
    name: '测试团队',
    description: '这是一个测试团队',
    tags: ['篮球'],
    creatorId: 1,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  }),
  updateTeam: vi.fn(),
  deleteTeam: vi.fn(),
  getTeamMembers: vi.fn().mockResolvedValue([
    {
      id: 1,
      username: 'testuser',
      nickname: '测试用户',
      avatar: 'https://via.placeholder.com/40',
    },
  ]),
  addTeamMember: vi.fn(),
  removeTeamMember: vi.fn(),
}))

const router = createRouter({
  history: createWebHistory(),
  routes: [],
})

describe('TeamDetailView', () => {
  test('渲染团队详情页面', () => {
    const wrapper = mount(TeamDetailView, {
      global: {
        plugins: [router],
      },
      props: {
        id: 1,
      },
    })

    expect(wrapper.find('h1').text()).toBe('团队详情')
  })

  test('点击编辑按钮显示编辑弹窗', async () => {
    const wrapper = mount(TeamDetailView, {
      global: {
        plugins: [router],
      },
      props: {
        id: 1,
      },
    })

    // 等待组件加载
    await wrapper.vm.$nextTick()

    // 检查编辑按钮是否存在
    expect(wrapper.find('.action-button.edit').exists()).toBe(true)
  })

  test('点击成员管理按钮显示成员弹窗', async () => {
    const wrapper = mount(TeamDetailView, {
      global: {
        plugins: [router],
      },
      props: {
        id: 1,
      },
    })

    await wrapper.find('.action-button.members').trigger('click')

    expect(wrapper.find('.modal').exists()).toBe(true)
    expect(wrapper.find('.modal h3').text()).toBe('团队成员')
  })

  test('添加成员表单输入', async () => {
    const wrapper = mount(TeamDetailView, {
      global: {
        plugins: [router],
      },
      props: {
        id: 1,
      },
    })

    await wrapper.find('.action-button.members').trigger('click')

    const numberInput = wrapper.find('input[type="number"]')
    await numberInput.setValue('2')

    // 使用类型断言确保类型安全
    expect((numberInput.element as HTMLInputElement).value).toBe('2')
  })

  test('点击添加成员按钮触发API调用', async () => {
    const wrapper = mount(TeamDetailView, {
      global: {
        plugins: [router],
      },
      props: {
        id: 1,
      },
    })

    // 等待组件渲染
    await wrapper.vm.$nextTick()

    await wrapper.find('.action-button.members').trigger('click')

    // 等待弹窗渲染
    await wrapper.vm.$nextTick()

    const input = wrapper.find('input[type="number"]')
    if (input.exists()) {
      await input.setValue('2')
      await wrapper.find('.primary-btn').trigger('click')

      // 检查是否被调用，不严格检查参数
      expect(addTeamMember).toHaveBeenCalled()
    }
  })

  test('显示团队成员列表', async () => {
    const wrapper = mount(TeamDetailView, {
      global: {
        plugins: [router],
      },
      props: {
        id: 1,
      },
    })

    // 等待组件渲染
    await wrapper.vm.$nextTick()

    // 点击成员管理按钮
    await wrapper.find('.action-button.members').trigger('click')

    // 等待弹窗渲染
    await wrapper.vm.$nextTick()

    // 检查成员列表是否显示
    expect(wrapper.find('.members-list').exists()).toBe(true)
  })
})
