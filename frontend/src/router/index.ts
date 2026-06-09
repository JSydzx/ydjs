import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PublishView from '../views/PublishView.vue'
import TeamView from '../views/TeamView.vue'
import ProfileView from '../views/ProfileView.vue'
import ApplyView from '../views/ApplyView.vue'
import EmailView from '../views/EmailView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import TeamDetailView from '../views/TeamDetailView.vue'
import ChatDetailView from '../views/ChatDetailView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: HomeView },
    { path: '/publish', name: 'publish', component: PublishView, meta: { requiresAuth: true } },
    { path: '/post/:id', name: 'postDetail', component: TeamDetailView, props: true },
    { path: '/apply/:postId', name: 'apply', component: ApplyView, props: true, meta: { requiresAuth: true } },
    { path: '/email', name: 'email', component: EmailView, meta: { requiresAuth: true } },
    { path: '/email/chat/:type/:id', name: 'chatDetail', component: ChatDetailView, meta: { requiresAuth: true } },
    { path: '/team', name: 'team', component: TeamView, meta: { requiresAuth: true } },
    { path: '/team/:id', name: 'teamDetail', component: TeamDetailView, props: true },
    { path: '/login', name: 'login', component: LoginView },
    { path: '/register', name: 'register', component: RegisterView },
    { path: '/profile', name: 'profile', component: ProfileView, meta: { requiresAuth: true } },
    { path: '/:catchAll(.*)', redirect: '/' },
  ],
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  return true
})

window.addEventListener('auth-expired', () => {
  router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
})

export default router
