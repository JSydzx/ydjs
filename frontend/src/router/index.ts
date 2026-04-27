import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PublishView from '../views/PublishView.vue'
import TeamView from '../views/TeamView.vue'
import TeamDetailView from '../views/TeamDetailView.vue'
import ProfileView from '../views/ProfileView.vue'
import UserDetailView from '../views/UserDetailView.vue'
import PostDetailView from '../views/PostDetailView.vue'
import ApplyView from '../views/ApplyView.vue'
import EmailView from '../views/EmailView.vue'
import ScheduleView from '../views/ScheduleView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import EditAvatarView from '../views/EditAvatarView.vue'

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
      path: '/team/:id',
      name: 'teamDetail',
      component: TeamDetailView,
      props: true,
    },
    {
      path: '/user/:id',
      name: 'userDetail',
      component: UserDetailView,
      props: true,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
    },
    // --- 新增：添加修改头像的路由 ---
    {
      path: '/edit-avatar',
      name: 'edit-avatar',
      component: EditAvatarView,
    },
    {
      path: '/:catchAll(.*)',
      redirect: '/',
    },
  ],
})

export default router
