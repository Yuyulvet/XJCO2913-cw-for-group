<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>当前预订</span>
            </div>
          </template>
          <div v-if="activeBooking">
            <p>滑板车编号: #{{ activeBooking.scooter_id }}</p>
            <p>开始时间: {{ formatTime(activeBooking.start_time) }}</p>
            <el-button type="primary" @click="endBooking">结束预订</el-button>
          </div>
          <div v-else>
            <p>暂无活动预订</p>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>使用统计</span>
            </div>
          </template>
          <div>
            <p>总使用时长: {{ stats.totalDuration }}分钟</p>
            <p>总费用: ¥{{ stats.totalCost }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from 'axios'

export default {
  setup() {
    const activeBooking = ref(null)
    const stats = ref({
      totalDuration: 0,
      totalCost: 0
    })

    const fetchActiveBooking = async () => {
      const response = await axios.get('/api/bookings/active')
      activeBooking.value = response.data
    }

    const fetchStats = async () => {
      const response = await axios.get('/api/users/stats')
      stats.value = response.data
    }

    onMounted(() => {
      fetchActiveBooking()
      fetchStats()
    })

    return {
      activeBooking,
      stats
    }
  }
}
</script> 