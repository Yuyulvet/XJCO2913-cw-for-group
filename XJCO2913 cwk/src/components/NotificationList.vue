<template>
  <div class="notification-list">
    <template v-if="notifications.length > 0">
      <div
        v-for="notification in notifications"
        :key="notification.id"
        class="notification-item"
        :class="{ unread: !notification.is_read }"
        @click="handleClick(notification)"
      >
        <div class="notification-icon">
          <el-icon :size="20">
            <component :is="getIcon(notification.type)" />
          </el-icon>
        </div>
        <div class="notification-content">
          <div class="notification-title">{{ notification.title }}</div>
          <div class="notification-text">{{ notification.content }}</div>
          <div class="notification-time">
            {{ formatTime(notification.created_at) }}
          </div>
        </div>
      </div>
    </template>
    <el-empty
      v-else
      description="暂无消息"
    />
  </div>
</template>

<script setup>
import { Bell, Document, Warning, CreditCard } from '@element-plus/icons-vue'
import { formatTime } from '@/utils/date'

const props = defineProps({
  notifications: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['read'])

const getIcon = (type) => {
  const icons = {
    system: Bell,
    booking: Document,
    issue: Warning,
    payment: CreditCard
  }
  return icons[type] || Bell
}

const handleClick = (notification) => {
  if (!notification.is_read) {
    emit('read', notification.id)
  }
}
</script>

<style scoped>
.notification-list {
  padding: 10px 0;
}

.notification-item {
  display: flex;
  padding: 12px 20px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.notification-item:hover {
  background-color: #f5f7fa;
}

.notification-item.unread {
  background-color: #ecf5ff;
}

.notification-icon {
  margin-right: 12px;
  color: #409EFF;
}

.notification-content {
  flex: 1;
}

.notification-title {
  font-weight: bold;
  margin-bottom: 4px;
}

.notification-text {
  color: #666;
  font-size: 14px;
}

.notification-time {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
}
</style> 