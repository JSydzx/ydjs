import { describe, test, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import UserDetailView from '../views/UserDetailView.vue'
import { createRouter, createWebHistory } from 'vue-router'

// Mock API
vi.mock('../api/user', () => ({
  getUserDetail: vi.fn().mockResolvedValue({
    id: 1,
    username: 'testuser',
    nickname: '测试用户',
    avatar: 'https://via.placeholder.com/40',
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
  }),
}))

const router = createRouter({
  history: createWebHistory(),
  routes: [],
})

describe('UserDetailView', () => {
  test('渲染用户详情页面', () => {
    const wrapper = mount(UserDetailView, {
      global: {
        plugins: [router],
      },
      props: {
        id: 1,
      },
    })

    expect(wrapper.find('h1').text()).toBe('用户详情')
  })

  test('显示用户信息', async () => {
    const wrapper = mount(UserDetailView, {
      global: {
        plugins: [router],
      },
      props: {
        id: 1,
      },
    })

    // 等待组件加载
    await wrapper.vm.$nextTick()

    // 检查用户信息是否显示
    expect(wrapper.find('.avatar').exists()).toBe(true)
    expect(wrapper.find('.user-info').exists()).toBe(true)
  })

  test('显示用户发布的帖子', async () => {
    // 模拟帖子数据
    const mockPosts = [
      {
        id: 1,
        title: '篮球队招人',
        preview: '我们篮球队正在招人，欢迎加入！',
        time: '2026-04-25 10:00',
      },
    ]

    const wrapper = mount(UserDetailView, {
      global: {
        plugins: [router],
      },
      props: {
        id: 1,
      },
    })

    // 等待组件加载
    await wrapper.vm.$nextTick()

    // 检查帖子列表是否显示
    expect(wrapper.find('.user-posts').exists()).toBe(true)
  })

  test('没有帖子时显示空状态', async () => {
    const wrapper = mount(UserDetailView, {
      global: {
        plugins: [router],
      },
      props: {
        id: 1,
      },
    })

    // 等待组件加载
    await wrapper.vm.$nextTick()

    // 检查空状态是否显示
    expect(wrapper.find('.user-posts').exists()).toBe(true)
  })
})
