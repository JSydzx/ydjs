<template>
  <div class="app-wrapper">
    <main class="page-container">
      <router-view />
    </main>

    <footer v-if="route.name !== 'login'" class="tabbar">
      <router-link to="/" class="tab" :class="{ active: activeTab === 'home' }"
        @click="activeTab = 'home'">首页</router-link>
      <router-link to="/publish" class="tab publish" :class="{ active: activeTab === 'publish' }"
        @click="activeTab = 'publish'">发布</router-link>
      <router-link to="/schedule" class="tab" :class="{ active: activeTab === 'schedule' }"
        @click="activeTab = 'schedule'">日程</router-link>
      <router-link to="/profile" class="tab" :class="{ active: activeTab === 'profile' }"
        @click="activeTab = 'profile'">我</router-link>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeTab = ref('home')

watch(route, (to) => {
  const mapping: Record<string, string> = {
    '/': 'home',
    '/publish': 'publish',
    '/schedule': 'schedule',
    '/profile': 'profile',
  }
  activeTab.value = mapping[to.path] ?? 'home'
})
</script>

<style>
.app-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.page-container {
  flex: 1;
  overflow: auto;
  padding-bottom: 65px;
  background-color: #f7f8fa;
}

.tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 60px;
  display: flex;
  border-top: 1px solid #ddd;
  background: #ffffff;
  z-index: 10;
  justify-content: space-around;
  align-items: center;
  color: #333333;
}

.tab.active {
  color: #4a90e2;
}
</style>