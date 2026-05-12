<template>
  <div class="app-wrapper">
    <main class="page-container"><router-view /></main>
    <footer v-if="showTabbar" class="tabbar">
      <router-link to="/" class="tab" :class="{ active: route.path === '/' }"><span>广场</span></router-link>
      <router-link to="/publish" class="tab" :class="{ active: route.path === '/publish' }"><span>发布</span></router-link>
      <router-link to="/email" class="tab" :class="{ active: route.path === '/email' }"><span>通知</span></router-link>
      <router-link to="/profile" class="tab" :class="{ active: route.path === '/profile' }"><span>我的</span></router-link>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const hideTabbarRoutes = ['login', 'register']
const showTabbar = computed(() => !hideTabbarRoutes.includes(route.name as string))
</script>

<style>
.app-wrapper { min-height: 100vh; display: flex; flex-direction: column; }
.page-container { flex: 1; overflow: auto; padding-bottom: 70px; background-color: #f7f8fa; }
.tabbar { position: fixed; left: 0; right: 0; bottom: 0; height: 56px; display: flex; border-top: 1px solid #eee; background: #ffffff; z-index: 10; justify-content: space-around; align-items: center; }
.tab { text-decoration: none; color: #999; font-size: 13px; display: flex; flex-direction: column; align-items: center; gap: 2px; padding: 4px; }
.tab.active { color: #4a90e2; }
</style>
