<template>
  <div class="admin-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>注册用户数</span>
            </div>
          </template>
          <div class="card-value">{{ stats.totalUsers || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>本月收入</span>
            </div>
          </template>
          <div class="card-value">¥{{ stats.monthlyRevenue || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>活跃订单</span>
            </div>
          </template>
          <div class="card-value">{{ stats.activeBookings || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待处理问题</span>
            </div>
          </template>
          <div class="card-value">{{ stats.pendingIssues || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>收入趋势</span>
              <el-radio-group v-model="revenueChartPeriod" size="small">
                <el-radio-button label="week">周</el-radio-button>
                <el-radio-button label="month">月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <canvas ref="revenueChart"></canvas>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>用户活跃度</span>
              <el-radio-group v-model="usageChartPeriod" size="small">
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
    </el-row>

    <!-- 功能区域 -->
    <el-row :gutter="20" class="function-row">
      <el-col :span="14">
        <!-- 问题管理 -->
        <el-card class="issues-card">
          <template #header>
            <div class="card-header">
              <span>问题管理</span>
              <el-select v-model="issueFilter" size="small">
                <el-option label="全部" value="all" />
                <el-option label="待处理" value="PENDING" />
                <el-option label="处理中" value="IN_PROGRESS" />
                <el-option label="已解决" value="RESOLVED" />
              </el-select>
            </div>
          </template>
          <el-table :data="filteredIssues" style="width: 100%">
            <el-table-column prop="scooterId" label="滑板车" width="80" />
            <el-table-column prop="description" label="问题描述" />
            <el-table-column prop="priority" label="优先级" width="100">
              <template #default="{ row }">
                <el-tag :type="getPriorityType(row.priority)">
                  {{ getPriorityText(row.priority) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button-group>
                  <el-button 
                    size="small" 
                    :type="row.status === 'PENDING' ? 'primary' : 'info'"
                    @click="handleIssue(row)"
                  >
                    {{ row.status === 'PENDING' ? '处理' : '更新' }}
                  </el-button>
                  <el-button 
                    size="small" 
                    @click="updatePriority(row)"
                  >
                    优先级
                  </el-button>
                </el-button-group>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="10">
        <!-- 价格配置 -->
        <el-card class="pricing-card">
          <template #header>
            <div class="card-header">
              <span>价格配置</span>
              <el-button type="primary" size="small" @click="savePricing">
                保存配置
              </el-button>
            </div>
          </template>
          <el-form :model="pricingForm" label-width="120px">
            <el-form-item label="基础价格">
              <el-input-number 
                v-model="pricingForm.basePrice" 
                :precision="2" 
                :step="0.1"
              />
              元/分钟
            </el-form-item>
            <el-form-item label="优惠条件">
              <el-input-number 
                v-model="pricingForm.discountThreshold" 
                :min="1"
              />
              小时/周
            </el-form-item>
            <el-form-item label="优惠折扣">
              <el-input-number 
                v-model="pricingForm.discountPercentage" 
                :min="0" 
                :max="100"
              />
              %
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 用户统计 -->
        <el-card class="users-card">
          <template #header>
            <div class="card-header">
              <span>用户统计</span>
            </div>
          </template>
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-label">新增用户（本周）</div>
              <div class="stat-number">{{ stats.newUsersWeek || 0 }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">活跃用户（本周）</div>
              <div class="stat-number">{{ stats.activeUsersWeek || 0 }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">优惠用户数量</div>
              <div class="stat-number">{{ stats.discountUsers || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 问题处理对话框 -->
    <el-dialog
      v-model="issueDialogVisible"
      :title="currentIssue?.status === 'PENDING' ? '处理问题' : '更新状态'"
      width="40%"
    >
      <el-form :model="issueForm" label-width="80px">
        <el-form-item label="状态">
          <el-select v-model="issueForm.status">
            <el-option label="处理中" value="IN_PROGRESS" />
            <el-option label="已解决" value="RESOLVED" />
            <el-option label="已关闭" value="CLOSED" />
          </el-select>
        </el-form-item>
        <el-form-item label="反馈">
          <el-input
            type="textarea"
            v-model="issueForm.feedback"
            :rows="4"
            placeholder="请输入处理反馈"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="issueDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitIssueUpdate">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 优先级设置对话框 -->
    <el-dialog
      v-model="priorityDialogVisible"
      title="设置优先级"
      width="30%"
    >
      <el-form :model="priorityForm" label-width="80px">
        <el-form-item label="优先级">
          <el-select v-model="priorityForm.priority">
            <el-option label="低" value="LOW" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="高" value="HIGH" />
            <el-option label="紧急" value="CRITICAL" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="priorityDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPriorityUpdate">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import Chart from 'chart.js/auto'
import api from '@/api'

// 状态数据
const stats = ref({})
const issues = ref([])
const issueFilter = ref('all')
const revenueChartPeriod = ref('week')
const usageChartPeriod = ref('week')

// 图表引用
const revenueChart = ref(null)
const usageChart = ref(null)
let revenueChartInstance = null
let usageChartInstance = null

// 对话框状态
const issueDialogVisible = ref(false)
const priorityDialogVisible = ref(false)
const currentIssue = ref(null)

// 表单数据
const pricingForm = ref({
  basePrice: 0.5,
  discountThreshold: 6,
  discountPercentage: 20
})

const issueForm = ref({
  status: '',
  feedback: ''
})

const priorityForm = ref({
  priority: ''
})

// 计算属性
const filteredIssues = computed(() => {
  if (issueFilter.value === 'all') {
    return issues.value
  }
  return issues.value.filter(issue => issue.status === issueFilter.value)
})

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await api.get('/admin/stats')
    stats.value = response.data
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 获取问题列表
const fetchIssues = async () => {
  try {
    const response = await api.get('/admin/issues')
    issues.value = response.data
  } catch (error) {
    ElMessage.error('获取问题列表失败')
  }
}

// 获取价格配置
const fetchPricing = async () => {
  try {
    const response = await api.get('/admin/pricing/current')
    pricingForm.value = response.data
  } catch (error) {
    ElMessage.error('获取价格配置失败')
  }
}

// 初始化收入图表
const initRevenueChart = async () => {
  const ctx = revenueChart.value.getContext('2d')
  try {
    const response = await api.get(`/admin/stats/revenue/${revenueChartPeriod.value}`)
    
    if (revenueChartInstance) {
      revenueChartInstance.destroy()
    }

    revenueChartInstance = new Chart(ctx, {
      type: 'line',
      data: {
        labels: response.data.labels,
        datasets: [{
          label: '收入（元）',
          data: response.data.data,
          borderColor: '#67C23A',
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

// 初始化使用量图表
const initUsageChart = async () => {
  const ctx = usageChart.value.getContext('2d')
  try {
    const response = await api.get(`/admin/stats/usage/${usageChartPeriod.value}`)
    
    if (usageChartInstance) {
      usageChartInstance.destroy()
    }

    usageChartInstance = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: response.data.labels,
        datasets: [{
          label: '使用时长（小时）',
          data: response.data.data,
          backgroundColor: '#409EFF'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false
      }
    })
  } catch (error) {
    ElMessage.error('获取使用量数据失败')
  }
}

// 保存价格配置
const savePricing = async () => {
  try {
    await api.put('/admin/pricing', pricingForm.value)
    ElMessage.success('价格配置已更新')
  } catch (error) {
    ElMessage.error('保存价格配置失败')
  }
}

// 处理问题
const handleIssue = (issue) => {
  currentIssue.value = issue
  issueForm.value = {
    status: issue.status === 'PENDING' ? 'IN_PROGRESS' : issue.status,
    feedback: issue.feedback || ''
  }
  issueDialogVisible.value = true
}

// 提交问题更新
const submitIssueUpdate = async () => {
  try {
    await api.put(`/admin/issues/${currentIssue.value.id}`, issueForm.value)
    ElMessage.success('问题状态已更新')
    issueDialogVisible.value = false
    fetchIssues()
  } catch (error) {
    ElMessage.error('更新问题状态失败')
  }
}

// 更新优先级
const updatePriority = (issue) => {
  currentIssue.value = issue
  priorityForm.value = {
    priority: issue.priority
  }
  priorityDialogVisible.value = true
}

// 提交优先级更新
const submitPriorityUpdate = async () => {
  try {
    await api.put(`/admin/issues/${currentIssue.value.id}/priority`, {
      priority: priorityForm.value.priority
    })
    ElMessage.success('优先级已更新')
    priorityDialogVisible.value = false
    fetchIssues()
  } catch (error) {
    ElMessage.error('更新优先级失败')
  }
}

// 状态显示工具函数
const getPriorityType = (priority) => {
  const types = {
    LOW: 'info',
    MEDIUM: 'warning',
    HIGH: 'danger',
    CRITICAL: 'danger'
  }
  return types[priority] || 'info'
}

const getPriorityText = (priority) => {
  const texts = {
    LOW: '低',
    MEDIUM: '中',
    HIGH: '高',
    CRITICAL: '紧急'
  }
  return texts[priority] || priority
}

const getStatusType = (status) => {
  const types = {
    PENDING: 'info',
    IN_PROGRESS: 'warning',
    RESOLVED: 'success',
    CLOSED: ''
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    PENDING: '待处理',
    IN_PROGRESS: '处理中',
    RESOLVED: '已解决',
    CLOSED: '已关闭'
  }
  return texts[status] || status
}

// 监听图表周期变化
watch(revenueChartPeriod, () => {
  initRevenueChart()
})

watch(usageChartPeriod, () => {
  initUsageChart()
})

// 生命周期钩子
onMounted(async () => {
  await Promise.all([
    fetchStats(),
    fetchIssues(),
    fetchPricing()
  ])
  initRevenueChart()
  initUsageChart()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.stat-cards {
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

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}

.function-row {
  margin-bottom: 20px;
}

.issues-card {
  margin-bottom: 20px;
}

.pricing-card {
  margin-bottom: 20px;
}

.users-card {
  margin-bottom: 20px;
}

.user-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 10px;
}

.stat-label {
  color: #606266;
  margin-bottom: 5px;
}

.stat-number {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}
</style> 