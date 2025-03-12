<template>
  <div class="admin-dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <h3>总用户数</h3>
          <div class="stat-value">{{ stats.totalUsers }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <h3>活跃预订</h3>
          <div class="stat-value">{{ stats.activeBookings }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <h3>可用滑板车</h3>
          <div class="stat-value">{{ stats.availableScooters }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <h3>待处理问题</h3>
          <div class="stat-value">{{ stats.pendingIssues }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>每日预订量</span>
            </div>
          </template>
          <canvas ref="bookingsChart"></canvas>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>收入统计</span>
            </div>
          </template>
          <canvas ref="revenueChart"></canvas>
        </el-card>
      </el-col>
    </el-row>

    <!-- 价格配置 -->
    <el-card class="pricing-card">
      <template #header>
        <div class="card-header">
          <h3>价格配置</h3>
          <el-button type="primary" @click="savePricing">
            保存配置
          </el-button>
        </div>
      </template>

      <el-form :model="pricingForm" label-width="120px">
        <el-form-item label="基础价格(元/分钟)">
          <el-input-number 
            v-model="pricingForm.basePrice" 
            :precision="2" 
            :step="0.1" 
          />
        </el-form-item>
        
        <el-form-item label="周套餐价格">
          <el-input-number 
            v-model="pricingForm.weeklyPrice" 
            :precision="2" 
          />
        </el-form-item>
        
        <el-form-item label="月套餐价格">
          <el-input-number 
            v-model="pricingForm.monthlyPrice" 
            :precision="2" 
          />
        </el-form-item>
        
        <el-form-item label="折扣阈值(小时/周)">
          <el-input-number 
            v-model="pricingForm.discountThreshold" 
          />
        </el-form-item>
        
        <el-form-item label="折扣比例(%)">
          <el-input-number 
            v-model="pricingForm.discountPercentage"
            :min="0"
            :max="100"
          />
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 问题管理 -->
    <el-card class="issues-card">
      <template #header>
        <div class="card-header">
          <h3>待处理问题</h3>
        </div>
      </template>

      <el-table :data="pendingIssues" stripe>
        <el-table-column prop="scooter_id" label="滑板车编号" width="120" />
        <el-table-column prop="description" label="问题描述" />
        <el-table-column prop="created_at" label="报告时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.created_at) }}
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="scope">
            <el-tag :type="getPriorityType(scope.row.priority)">
              {{ getPriorityText(scope.row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button 
              size="small" 
              @click="handleIssue(scope.row)"
            >
              处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import Chart from 'chart.js/auto'
import { formatTime } from '@/utils/date'

const stats = ref({
  totalUsers: 0,
  activeBookings: 0,
  availableScooters: 0,
  pendingIssues: 0
})

const bookingsChart = ref(null)
const revenueChart = ref(null)
let bookingsChartInstance = null
let revenueChartInstance = null

const pricingForm = ref({
  basePrice: 0.5,
  weeklyPrice: 99,
  monthlyPrice: 299,
  discountThreshold: 6,
  discountPercentage: 20
})

const pendingIssues = ref([])

// 获取统计数据
const fetchStats = async () => {
  try {
    const [users, bookings, scooters, issues] = await Promise.all([
      axios.get('/api/admin/statistics'),
      axios.get('/api/admin/bookings/active'),
      axios.get('/api/admin/scooters/available'),
      axios.get('/api/admin/issues/count/pending')
    ])

    stats.value = {
      totalUsers: users.data.totalUsers,
      activeBookings: bookings.data.activeBookings,
      availableScooters: scooters.data.availableScooters,
      pendingIssues: issues.data.pendingIssues
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 初始化预订图表
const initBookingsChart = async () => {
  try {
    const response = await axios.get('/api/admin/bookings/stats/daily')
    const ctx = bookingsChart.value.getContext('2d')
    
    bookingsChartInstance = new Chart(ctx, {
      type: 'line',
      data: {
        labels: response.data.dates,
        datasets: [{
          label: '每日预订量',
          data: response.data.counts,
          borderColor: '#409EFF',
          tension: 0.1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    })
  } catch (error) {
    ElMessage.error('获取预订统计失败')
  }
}

// 初始化收入图表
const initRevenueChart = async () => {
  try {
    const response = await axios.get('/api/admin/bookings/stats/revenue')
    const ctx = revenueChart.value.getContext('2d')
    
    revenueChartInstance = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: response.data.dates,
        datasets: [{
          label: '每日收入',
          data: response.data.revenue,
          backgroundColor: '#67C23A'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    })
  } catch (error) {
    ElMessage.error('获取收入统计失败')
  }
}

// 保存价格配置
const savePricing = async () => {
  try {
    await axios.put('/api/admin/pricing', pricingForm.value)
    ElMessage.success('价格配置已更新')
  } catch (error) {
    ElMessage.error('保存价格配置失败')
  }
}

// 获取待处理问题
const fetchPendingIssues = async () => {
  try {
    const response = await axios.get('/api/admin/issues/pending')
    pendingIssues.value = response.data
  } catch (error) {
    ElMessage.error('获取待处理问题失败')
  }
}

// 处理问题
const handleIssue = (issue) => {
  // 实现问题处理逻辑
}

const getPriorityType = (priority) => {
  const types = {
    1: 'info',
    2: 'warning',
    3: 'danger'
  }
  return types[priority] || 'info'
}

const getPriorityText = (priority) => {
  const texts = {
    1: '低',
    2: '中',
    3: '高'
  }
  return texts[priority] || '未知'
}

onMounted(async () => {
  await fetchStats()
  await initBookingsChart()
  await initRevenueChart()
  fetchPendingIssues()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-top: 10px;
}

.chart-row {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

canvas {
  min-height: 300px;
}
</style> 