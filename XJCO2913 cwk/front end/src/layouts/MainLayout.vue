<template>
  <div class="main-layout">
    <el-container>
      <el-aside width="200px">
        <el-menu
          :router="true"
          :default-active="activeRoute"
          class="side-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/map">
            <el-icon><Location /></el-icon>
            <span>滑板车地图</span>
          </el-menu-item>
          <el-menu-item index="/bookings">
            <el-icon><List /></el-icon>
            <span>我的预订</span>
          </el-menu-item>
          <el-menu-item index="/issues">
            <el-icon><Warning /></el-icon>
            <span>问题反馈</span>
          </el-menu-item>
          <el-menu-item v-if="isManager" index="/admin">
            <el-icon><Setting /></el-icon>
            <span>管理后台</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header>
          <div class="header-content">
            <h2>电动滑板车租赁系统</h2>
            <div class="user-info">
              <el-dropdown>
                <span class="user-dropdown">
                  {{ username }}
                  <el-icon><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="toProfile">个人信息</el-dropdown-item>
                    <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeRoute = computed(() => route.path)
const username = computed(() => userStore.username)
const isManager = computed(() => userStore.role === 'manager')

const logout = () => {
  userStore.logout()
  router.push('/login')
}

const toProfile = () => {
  router.push('/profile')
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.side-menu {
  height: 100%;
  border-right: none;
}

.user-dropdown {
  cursor: pointer;
  color: #409EFF;
}
</style> 