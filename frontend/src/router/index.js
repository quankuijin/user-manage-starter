import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import UserManage from '../views/UserManage.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/user-manage',
    name: 'UserManage',
    component: UserManage,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/user-manage')
  } else {
    next()
  }
})

export default router
