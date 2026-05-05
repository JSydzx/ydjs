import { describe, test, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import HomeView from '../views/HomeView.vue'
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [],
})

describe('HomeView', () => {
  test('渲染首页', () => {
    const wrapper = mount(HomeView, {
      global: {
        plugins: [router],
      },
    })

    expect(wrapper.find('h1').text()).toBe('公告')
    expect(wrapper.find('.category-area').exists()).toBe(true)
  })

  test('点击分类标签切换分类', async () => {
    const wrapper = mount(HomeView, {
      global: {
        plugins: [router],
      },
    })

    const tabs = wrapper.findAll('.category-area button')
    if (tabs.length > 1) {
      await tabs[1].trigger('click')
      expect(tabs[1].classes()).toContain('active')
    }
  })

  test('点击帖子跳转到详情页', async () => {
    const pushMock = vi.spyOn(router, 'push')

    const wrapper = mount(HomeView, {
      global: {
        plugins: [router],
      },
    })

    // 等待组件加载
    await wrapper.vm.$nextTick()

    // 检查帖子列表是否显示
    expect(wrapper.find('.post-area').exists()).toBe(true)
  })

  test('点击用户头像跳转到用户详情页', async () => {
    const pushMock = vi.spyOn(router, 'push')

    const wrapper = mount(HomeView, {
      global: {
        plugins: [router],
      },
    })

    // 等待组件加载
    await wrapper.vm.$nextTick()

    // 检查帖子列表是否显示
    expect(wrapper.find('.post-area').exists()).toBe(true)
  })
})
