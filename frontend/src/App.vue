  <template>
    <div class="app-wrapper">
      <main class="page-container">
        <router-view />
      </main>

      <footer
        v-if="route.name !== 'login' && route.name !== 'team' && route.name !== 'email' && route.name !== 'apply' && route.name !== 'teamDetail' && route.name !== 'userDetail' && route.name !== 'postDetail' && route.name !== 'edit-avatar'"
        class="tabbar">
        <router-link to="/" class="tab" :class="{ active: activeTab === 'home' }" @click="activeTab = 'home'">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="currentColor"
            class="tab-icon">
            <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z" />
          </svg>
          <span>首页</span>
        </router-link>

        <router-link to="/publish" class="tab publish" :class="{ active: activeTab === 'publish' }"
          @click="activeTab = 'publish'">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="currentColor"
            class="tab-icon">
            <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" />
          </svg>
          <span>发布</span>
        </router-link>

        <router-link to="/schedule" class="tab" :class="{ active: activeTab === 'schedule' }"
          @click="activeTab = 'schedule'">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="currentColor"
            class="tab-icon">
            <path
              d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11z" />
          </svg>
          <span>日程</span>
        </router-link>

        <router-link to="/profile" class="tab" :class="{ active: activeTab === 'profile' }"
          @click="activeTab = 'profile'">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="currentColor"
            class="tab-icon">
            <path
              d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z" />
          </svg>
          <span>我</span>
        </router-link>
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
  align-items: stretch;
}

.tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  color: #999;
  font-size: 12px;
  flex: 1;
}

.tab.active {
  color: #4a90e2;
}

.tab-icon {
  margin-bottom: 4px;
  width: 24px;
  height: 24px;
}
</style>