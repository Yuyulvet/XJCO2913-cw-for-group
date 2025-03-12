import { defineStore } from 'pinia'
import api from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    id: null,
    username: '',
    email: '',
    role: '',
    token: localStorage.getItem('token') || '',
    isAuthenticated: false
  }),

  getters: {
    isManager: (state) => state.role === 'MANAGER',
    getUserId: (state) => state.id
  },

  actions: {
    async login(username, password) {
      try {
        const response = await api.post('/auth/login', { username, password })
        this.setUserInfo(response.user)
        this.setToken(response.token)
        return true
      } catch (error) {
        console.error('登录失败:', error)
        return false
      }
    },

    async logout() {
      this.clearUserInfo()
      this.clearToken()
    },

    setUserInfo(user) {
      this.id = user.id
      this.username = user.username
      this.email = user.email
      this.role = user.role
      this.isAuthenticated = true
    },

    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },

    clearUserInfo() {
      this.id = null
      this.username = ''
      this.email = ''
      this.role = ''
      this.isAuthenticated = false
    },

    clearToken() {
      this.token = ''
      localStorage.removeItem('token')
    }
  }
}) 