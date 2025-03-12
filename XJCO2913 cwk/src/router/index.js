import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import ScooterMap from '../views/ScooterMap.vue'
import BookingHistory from '../views/BookingHistory.vue'
import Issues from '../views/Issues.vue'
import Profile from '../views/Profile.vue'
import AdminDashboard from '../views/admin/Dashboard.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: MainLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard
      },
      {
        path: 'map',
        name: 'Map',
        component: ScooterMap
      },
      {
        path: 'bookings',
        name: 'BookingHistory',
        component: BookingHistory
      },
      {
        path: 'issues',
        name: 'Issues',
        component: Issues
      },
      {
        path: 'profile',
        name: 'Profile',
        component: Profile
      },
      {
        path: 'admin',
        name: 'AdminDashboard',
        component: AdminDashboard,
        meta: { requiresManager: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.requiresManager && userRole !== 'manager') {
    next('/dashboard')
  } else {
    next()
  }
})

export default router 