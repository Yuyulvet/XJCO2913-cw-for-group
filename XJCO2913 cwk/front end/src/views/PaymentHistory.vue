<template>
  <div class="payment-history">
    <h3>支付记录</h3>
    
    <el-tabs v-model="activeTab">
      <el-tab-pane label="订单记录" name="orders">
        <el-table :data="payments" stripe>
          <el-table-column prop="order_no" label="订单号" width="180" />
          <el-table-column prop="created_at" label="支付时间" width="180">
            <template #default="scope">
              {{ formatTime(scope.row.created_at) }}
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="120">
            <template #default="scope">
              ¥{{ scope.row.amount.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="payment_type" label="支付方式" width="120">
            <template #default="scope">
              {{ getPaymentTypeText(scope.row.payment_type) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button 
                v-if="scope.row.status === 'pending'"
                size="small" 
                type="primary"
                @click="handlePay(scope.row)"
              >
                去支付
              </el-button>
              <el-button
                size="small"
                @click="showDetail(scope.row)"
              >
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange"
          layout="prev, pager, next"
        />
      </el-tab-pane>
      
      <el-tab-pane label="套餐订阅" name="subscriptions">
        <el-table :data="subscriptions" stripe>
          <el-table-column prop="plan_name" label="套餐名称" />
          <el-table-column prop="start_date" label="开始日期">
            <template #default="scope">
              {{ formatDate(scope.row.start_date) }}
            </template>
          </el-table-column>
          <el-table-column prop="end_date" label="结束日期">
            <template #default="scope">
              {{ formatDate(scope.row.end_date) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getSubscriptionStatusType(scope.row.status)">
                {{ getSubscriptionStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button 
                v-if="scope.row.status === 'active'"
                size="small" 
                type="danger"
                @click="cancelSubscription(scope.row)"
              >
                取消订阅
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 支付详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="500px"
    >
      <div v-if="selectedPayment" class="payment-detail">
        <p><strong>订单号：</strong>{{ selectedPayment.order_no }}</p>
        <p><strong>创建时间：</strong>{{ formatTime(selectedPayment.created_at) }}</p>
        <p><strong>支付时间：</strong>{{ formatTime(selectedPayment.paid_at) }}</p>
        <p><strong>支付金额：</strong>¥{{ selectedPayment.amount.toFixed(2) }}</p>
        <p><strong>支付方式：</strong>{{ getPaymentTypeText(selectedPayment.payment_type) }}</p>
        <p><strong>订单状态：</strong>{{ getStatusText(selectedPayment.status) }}</p>
        <p><strong>订单描述：</strong>{{ selectedPayment.description }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatTime, formatDate } from '@/utils/date'
import axios from 'axios'

const activeTab = ref('orders')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const payments = ref([])
const subscriptions = ref([])
const detailDialogVisible = ref(false)
const selectedPayment = ref(null)

// 获取支付记录
const fetchPayments = async (page) => {
  try {
    const response = await axios.get(`/api/payments?page=${page}&size=${pageSize.value}`)
    payments.value = response.data.items
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('获取支付记录失败')
  }
}

// 获取订阅记录
const fetchSubscriptions = async () => {
  try {
    const response = await axios.get('/api/subscriptions')
    subscriptions.value = response.data
  } catch (error) {
    ElMessage.error('获取订阅记录失败')
  }
}

// 处理支付
const handlePay = async (payment) => {
  try {
    const response = await axios.post(`/api/payments/${payment.id}/pay`)
    // 这里应该跳转到支付页面或显示支付二维码
    window.location.href = response.data.pay_url
  } catch (error) {
    ElMessage.error('发起支付失败')
  }
}

// 取消订阅
const cancelSubscription = async (subscription) => {
  try {
    await ElMessageBox.confirm('确定要取消该订阅吗？', '提示', {
      type: 'warning'
    })
    
    await axios.post(`/api/subscriptions/${subscription.id}/cancel`)
    ElMessage.success('订阅已取消')
    fetchSubscriptions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订阅失败')
    }
  }
}

// 显示订单详情
const showDetail = (payment) => {
  selectedPayment.value = payment
  detailDialogVisible.value = true
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchPayments(page)
}

const getPaymentTypeText = (type) => {
  const types = {
    alipay: '支付宝',
    wechat: '微信支付',
    balance: '余额支付'
  }
  return types[type] || type
}

const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    paid: 'success',
    failed: 'danger',
    refunded: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    pending: '待支付',
    paid: '已支付',
    failed: '支付失败',
    refunded: '已退款'
  }
  return texts[status] || status
}

const getSubscriptionStatusType = (status) => {
  const types = {
    active: 'success',
    expired: 'info',
    cancelled: 'danger'
  }
  return types[status] || 'info'
}

const getSubscriptionStatusText = (status) => {
  const texts = {
    active: '使用中',
    expired: '已过期',
    cancelled: '已取消'
  }
  return texts[status] || status
}

onMounted(() => {
  fetchPayments(1)
  fetchSubscriptions()
})
</script>

<style scoped>
.payment-history {
  padding: 20px;
}

.payment-detail {
  line-height: 1.8;
}

.payment-detail p {
  margin: 10px 0;
}
</style> 