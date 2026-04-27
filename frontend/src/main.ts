import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './style.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')

router.beforeEach((to, from, next) => {
  const userId = localStorage.getItem('userId')
  const publicPages = ['login', 'register']
  const isPublicPage = publicPages.includes(to.name as string)

  if (!userId && !isPublicPage) {
    next({ name: 'login' })
  } else {
    next()
  }
})
