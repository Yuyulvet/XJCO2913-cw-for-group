<template>
  <div class="notification-bell">
    <el-badge :value="unreadCount" :hidden="unreadCount === 0">
      <el-button circle @click="showNotifications">
        <el-icon><Bell /></el-icon>
      </el-button>
    </el-badge>

    <el-drawer
      v-model="drawerVisible"
      title="消息通知"
      direction="rtl"
      size="300px"
    >
      <div class="notification-header">
        <span>{{ unreadCount }} 条未读消息</span>
        <el-button 
          v-if="unreadCount > 0" 
          type="primary" 
          link 
          @click="markAllAsRead"
        >
          全部标记为已读
        </el-button>
      </div>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部消息" name="all">
          <notification-list
            :notifications="notifications"
            @read="markAsRead"
          />
        </el-tab-pane>
        <el-tab-pane label="未读消息" name="unread">
          <notification-list
            :notifications="unreadNotifications"
            @read="markAsRead"
          />
        </el-tab-pane>
      </el-tabs>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import NotificationList from './NotificationList.vue'
import axios from 'axios'

const drawerVisible = ref(false)
const activeTab = ref('all')
const notifications = ref([])

const unreadNotifications = computed(() => 
  notifications.value.filter(n => !n.is_read)
)
const unreadCount = computed(() => unreadNotifications.value.length)

const fetchNotifications = async () => {
  try {
    const response = await axios.get('/api/notifications')
    notifications.value = response.data
  } catch (error) {
    console.error('获取通知失败:', error)
  }
}

const showNotifications = () => {
  drawerVisible.value = true
  fetchNotifications()
}

const markAsRead = async (notificationId) => {
  try {
    await axios.post(`/api/notifications/${notificationId}/read`)
    const notification = notifications.value.find(n => n.id === notificationId)
    if (notification) {
      notification.is_read = true
    }
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

const markAllAsRead = async () => {
  try {
    await axios.post('/api/notifications/read-all')
    notifications.value.forEach(n => n.is_read = true)
  } catch (error) {
    console.error('标记全部已读失败:', error)
  }
}

onMounted(() => {
  fetchNotifications()
})
</script>

<style scoped>
.notification-bell {
  margin-right: 20px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  border-bottom: 1px solid #eee;
}
</style> 