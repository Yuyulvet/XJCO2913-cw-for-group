<template>
  <div class="booking-history">
    <h3>预订历史</h3>
    
    <el-table :data="bookings" stripe>
      <el-table-column prop="scooter_id" label="滑板车编号" width="120" />
      <el-table-column prop="start_time" label="开始时间" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.start_time) }}
        </template>
      </el-table-column>
      <el-table-column prop="end_time" label="结束时间" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.end_time) }}
        </template>
      </el-table-column>
      <el-table-column prop="duration" label="时长(分钟)" width="120" />
      <el-table-column prop="cost" label="费用(元)" width="120" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { formatTime } from '@/utils/date'
import axios from 'axios'

const bookings = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchBookings = async (page) => {
  try {
    const response = await axios.get(`/api/bookings/history?page=${page}&size=${pageSize.value}`)
    bookings.value = response.data.items
    total.value = response.data.total
  } catch (error) {
    console.error('获取预订历史失败:', error)
  }
}

const getStatusType = (status) => {
  const types = {
    active: 'primary',
    completed: 'success',
    cancelled: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    active: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return texts[status] || status
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchBookings(page)
}

onMounted(() => {
  fetchBookings(1)
})
</script> 