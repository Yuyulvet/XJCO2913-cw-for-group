<template>
  <div class="dashboard-container">
    <!-- 用户统计卡片 -->
    <el-row :gutter="20" class="dashboard-stats">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总骑行时长</span>
            </div>
          </template>
          <div class="card-value">{{ stats.totalDuration || '0' }}分钟</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>本周骑行时长</span>
            </div>
          </template>
          <div class="card-value">{{ stats.weeklyDuration || '0' }}分钟</div>
          <div class="discount-info" v-if="stats.weeklyDuration >= 360">
            <el-tag type="success">已获得优惠资格</el-tag>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>总消费金额</span>
            </div>
          </template>
          <div class="card-value">¥{{ stats.totalSpending || '0' }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>活跃订单</span>
            </div>
          </template>
          <div class="card-value">{{ stats.activeBookings || '0' }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 功能区域 -->
    <el-row :gutter="20" class="dashboard-features">
      <el-col :span="16">
        <!-- 当前订单 -->
        <el-card v-if="currentBooking" class="booking-card">
          <template #header>
            <div class="card-header">
              <span>当前订单</span>
              <div class="header-actions">
                <el-button type="primary" size="small" @click="extendBooking">
                  延长租用
                </el-button>
                <el-button type="danger" size="small" @click="endBooking">
                  结束租用
                </el-button>
              </div>
            </div>
          </template>
          <div class="booking-info">
            <p>滑板车编号：{{ currentBooking.scooterId }}</p>
            <p>开始时间：{{ formatTime(currentBooking.startTime) }}</p>
            <p>已用时长：{{ calculateDuration(currentBooking.startTime) }}分钟</p>
            <p>预计费用：¥{{ currentBooking.estimatedCost }}</p>
          </div>
        </el-card>

        <!-- 使用记录图表 -->
        <el-card class="usage-chart">
          <template #header>
            <div class="card-header">
              <span>使用记录</span>
              <el-radio-group v-model="chartPeriod" size="small">
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <canvas ref="usageChart"></canvas>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <!-- 问题反馈 -->
        <el-card class="issues-card">
          <template #header>
            <div class="card-header">
              <span>我的反馈</span>
              <el-button type="primary" size="small" @click="createIssue">
                新建反馈
              </el-button>
            </div>
          </template>
          <el-table :data="issues" style="width: 100%" :max-height="400">
            <el-table-column prop="scooterId" label="滑板车" width="80" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getIssueStatusType(row.status)">
                  {{ getIssueStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="feedback" label="处理结果">
              <template #default="{ row }">
                {{ row.feedback || '暂无反馈' }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 延长租用对话框 -->
    <el-dialog
      v-model="extendDialogVisible"
      title="延长租用时间"
      width="30%"
    >
      <el-form :model="extendForm">
        <el-form-item label="延长时间">
          <el-input-number
            v-model="extendForm.minutes"
            :min="15"
            :step="15"
            step-strictly
          />
          分钟
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="extendDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmExtend">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新建反馈对话框 -->
    <el-dialog
      v-model="issueDialogVisible"
      title="新建反馈"
      width="40%"
    >
      <el-form :model="issueForm" :rules="issueRules" ref="issueFormRef">
        <el-form-item label="滑板车编号" prop="scooterId">
          <el-input v-model="issueForm.scooterId" />
        </el-form-item>
        <el-form-item label="问题描述" prop="description">
          <el-input
            type="textarea"
            v-model="issueForm.description"
            :rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="issueDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitIssue">
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import api from '@/api'
import Chart from 'chart.js/auto'
import { formatTime } from '@/utils/date'

const userStore = useUserStore()
const stats = ref({})
const currentBooking = ref(null)
const issues = ref([])
const chartPeriod = ref('week')
const usageChart = ref(null)
let chartInstance = null

// 对话框状态
const extendDialogVisible = ref(false)
const issueDialogVisible = ref(false)

// 表单数据
const extendForm = ref({
  minutes: 30
})

const issueForm = ref({
  scooterId: '',
  description: ''
})

const issueFormRef = ref(null)
const issueRules = {
  scooterId: [
    { required: true, message: '请输入滑板车编号', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入问题描述', trigger: 'blur' }
  ]
}

// 获取用户统计数据
const fetchStats = async () => {
  try {
    const response = await api.get('/users/stats')
    stats.value = response.data
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 获取当前订单
const fetchCurrentBooking = async () => {
  try {
    const response = await api.get('/bookings/current')
    currentBooking.value = response.data
  } catch (error) {
    // 可能没有当前订单，不显示错误
  }
}

// 获取用户反馈
const fetchIssues = async () => {
  try {
    const response = await api.get('/issues/user')
    issues.value = response.data
  } catch (error) {
    ElMessage.error('获取反馈记录失败')
  }
}

// 初始化使用记录图表
const initUsageChart = async () => {
  const ctx = usageChart.value.getContext('2d')
  const response = await api.get(`/bookings/stats/${chartPeriod.value}`)
  
  if (chartInstance) {
    chartInstance.destroy()
  }

  chartInstance = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: response.data.labels,
      datasets: [{
        label: '使用时长（分钟）',
        data: response.data.data,
        backgroundColor: '#409EFF'
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false
    }
  })
}

// 计算已用时长
const calculateDuration = (startTime) => {
  if (!startTime) return 0
  const start = new Date(startTime)
  const now = new Date()
  return Math.floor((now - start) / 60000)
}

// 延长租用
const extendBooking = () => {
  if (!currentBooking.value) return
  extendDialogVisible.value = true
}

const confirmExtend = async () => {
  try {
    await api.put(`/bookings/${currentBooking.value.id}/extend`, {
      minutes: extendForm.value.minutes
    })
    ElMessage.success('延长租用成功')
    extendDialogVisible.value = false
    fetchCurrentBooking()
  } catch (error) {
    ElMessage.error('延长租用失败')
  }
}

// 结束租用
const endBooking = async () => {
  try {
    await ElMessageBox.confirm('确认结束当前租用？')
    await api.put(`/bookings/${currentBooking.value.id}/end`)
    ElMessage.success('租用已结束')
    currentBooking.value = null
    fetchStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('结束租用失败')
    }
  }
}

// 创建反馈
const createIssue = () => {
  issueForm.value = {
    scooterId: '',
    description: ''
  }
  issueDialogVisible.value = true
}

const submitIssue = async () => {
  if (!issueFormRef.value) return
  
  try {
    await issueFormRef.value.validate()
    await api.post('/issues', issueForm.value)
    ElMessage.success('反馈提交成功')
    issueDialogVisible.value = false
    fetchIssues()
  } catch (error) {
    if (error.response?.data) {
      ElMessage.error(error.response.data)
    }
  }
}

// 状态显示工具函数
const getIssueStatusType = (status) => {
  const types = {
    PENDING: 'info',
    IN_PROGRESS: 'warning',
    RESOLVED: 'success',
    CLOSED: ''
  }
  return types[status] || 'info'
}

const getIssueStatusText = (status) => {
  const texts = {
    PENDING: '待处理',
    IN_PROGRESS: '处理中',
    RESOLVED: '已解决',
    CLOSED: '已关闭'
  }
  return texts[status] || status
}

// 监听图表周期变化
watch(chartPeriod, () => {
  initUsageChart()
})

onMounted(async () => {
  await Promise.all([
    fetchStats(),
    fetchCurrentBooking(),
    fetchIssues()
  ])
  initUsageChart()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-stats {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin: 10px 0;
}

.discount-info {
  margin-top: 10px;
}

.booking-card {
  margin-bottom: 20px;
}

.booking-info {
  line-height: 1.8;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.usage-chart {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}

.issues-card {
  height: 100%;
}
</style> 