import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PublishView from '../views/PublishView.vue'
import TeamView from '../views/TeamView.vue'
import ProfileView from '../views/ProfileView.vue'
import PostDetailView from '../views/PostDetailView.vue'
import ApplyView from '../views/ApplyView.vue'
import EmailView from '../views/EmailView.vue'
import ScheduleView from '../views/ScheduleView.vue'
import LoginView from '../views/LoginView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/publish',
      name: 'publish',
      component: PublishView,
    },
    {
      path: '/post/:id',
      name: 'postDetail',
      component: PostDetailView,
      props: true,
    },
    {
      path: '/apply/:postId',
      name: 'apply',
      component: ApplyView,
      props: true,
    },
    {
      path: '/email',
      name: 'email',
      component: EmailView,
    },
    {
      path: '/schedule',
      name: 'schedule',
      component: ScheduleView,
    },
    {
      path: '/team',
      name: 'team',
      component: TeamView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
    },
    {
      path: '/:catchAll(.*)',
      redirect: '/',
    },
  ],
})

export default router
