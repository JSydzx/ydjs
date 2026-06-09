import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/register', name: 'register', component: RegisterView },
  ],
})

describe('视图组件测试', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('HomeView 应该能正常挂载', () => {
    const wrapper = mount(HomeView, {
      global: {
        plugins: [router],
      },
    })
    expect(wrapper.exists()).toBe(true)
  })

  it('LoginView 应该能正常挂载', () => {
    const wrapper = mount(LoginView, {
      global: {
        plugins: [router],
      },
    })
    expect(wrapper.exists()).toBe(true)
  })

  it('RegisterView 应该能正常挂载', () => {
    const wrapper = mount(RegisterView, {
      global: {
        plugins: [router],
      },
    })
    expect(wrapper.exists()).toBe(true)
  })

  it('LoginView 应该包含 Vant 表单组件', () => {
    const wrapper = mount(LoginView, {
      global: {
        plugins: [router],
      },
    })
    expect(wrapper.find('van-form').exists()).toBe(true)
  })

  it('RegisterView 应该包含 Vant 表单组件', () => {
    const wrapper = mount(RegisterView, {
      global: {
        plugins: [router],
      },
    })
    expect(wrapper.find('van-form').exists()).toBe(true)
  })
})
