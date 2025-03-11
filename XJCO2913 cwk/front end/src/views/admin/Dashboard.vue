<template>
  <div class="admin-dashboard">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6" v-for="stat in statistics" :key="stat.title">
        <el-card class="stat-card">
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-title">{{ stat.title }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 收入图表 -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <h3>收入统计</h3>
          <el-radio-group v-model="timeRange" @change="fetchRevenueData">
            <el-radio-button label="week">本周</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
            <el-radio-button label="year">本年</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div class="chart-container">
        <canvas ref="revenueChart"></canvas>
      </div>
    </el-card>

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

const timeRange = ref('week')
const revenueChart = ref(null)
let chart = null

const statistics = ref([
  { title: '总用户数', value: 0 },
  { title: '今日订单', value: 0 },
  { title: '今日收入', value: '¥0' },
  { title: '活跃滑板车', value: 0 }
])

const pricingForm = ref({
  basePrice: 0.5,
  weeklyPrice: 99,
  monthlyPrice: 299,
  discountThreshold: 6,
  discountPercentage: 20
})

const pendingIssues = ref([])

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const response = await axios.get('/api/admin/statistics')
    statistics.value = response.data
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 获取收入数据并绘制图表
const fetchRevenueData = async () => {
  try {
    const response = await axios.get(`/api/admin/revenue?range=${timeRange.value}`)
    const data = response.data
    
    if (chart) {
      chart.destroy()
    }
    
    const ctx = revenueChart.value.getContext('2d')
    chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: data.labels,
        datasets: [{
          label: '收入',
          data: data.values,
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
    ElMessage.error('获取收入数据失败')
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

onMounted(() => {
  fetchStatistics()
  fetchRevenueData()
  fetchPendingIssues()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.stat-card {
  text-align: center;
  margin-bottom: 20px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.stat-title {
  margin-top: 10px;
  color: #666;
}

.chart-card,
.pricing-card,
.issues-card {
  margin-bottom: 20px;
}

.chart-container {
  height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 