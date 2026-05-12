<template>
  <div class="app-wrapper">
    <main class="page-container"><router-view /></main>
    <footer v-if="showTabbar" class="tabbar">
      <router-link to="/" class="tab" :class="{ active: route.path === '/' }">
        <span class="tab-icon">🏠</span>
        <span class="tab-label">广场</span>
      </router-link>
      <router-link to="/publish" class="tab" :class="{ active: route.path === '/publish' }">
        <span class="tab-icon">✏️</span>
        <span class="tab-label">发布</span>
      </router-link>
      <router-link to="/email" class="tab" :class="{ active: route.path === '/email' }">
        <span class="tab-icon">🔔</span>
        <span class="tab-label">通知</span>
      </router-link>
      <router-link to="/profile" class="tab" :class="{ active: route.path === '/profile' }">
        <span class="tab-icon">👤</span>
        <span class="tab-label">我的</span>
      </router-link>
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
.app-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-bg);
}

.page-container {
  flex: 1;
  overflow: auto;
  padding-bottom: 70px;
}

.tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 56px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  background: var(--color-white);
  box-shadow: 0 -1px 8px rgba(0, 0, 0, 0.06);
  z-index: 100;
  padding-bottom: env(safe-area-inset-bottom, 0);
}

.tab {
  text-decoration: none;
  color: var(--color-text-muted);
  font-size: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 4px 12px;
  transition: color 0.2s;
  -webkit-tap-highlight-color: transparent;
}

.tab-icon {
  font-size: 20px;
  line-height: 1;
}

.tab-label {
  font-size: 11px;
}

.tab.active {
  color: var(--color-primary);
  font-weight: 500;
}
</style>
