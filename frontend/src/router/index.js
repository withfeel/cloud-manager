import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import ResourceApplication from '../views/ResourceApplication.vue'
import ApprovalManagement from '../views/ApprovalManagement.vue'
import ResourceAllocation from '../views/ResourceAllocation.vue'
import CostCalculation from '../views/CostCalculation.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/application',
    name: 'ResourceApplication',
    component: ResourceApplication,
    meta: { requiresAuth: true }
  },
  {
    path: '/approval',
    name: 'ApprovalManagement',
    component: ApprovalManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/allocation',
    name: 'ResourceAllocation',
    component: ResourceAllocation,
    meta: { requiresAuth: true }
  },
  {
    path: '/cost',
    name: 'CostCalculation',
    component: CostCalculation,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      next({ name: 'Login' })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router