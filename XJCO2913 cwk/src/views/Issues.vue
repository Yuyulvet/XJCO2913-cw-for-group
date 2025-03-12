<template>
  <div class="issues">
    <div class="issues-header">
      <h3>问题反馈</h3>
      <el-button type="primary" @click="showReportDialog">
        报告新问题
      </el-button>
    </div>
    
    <el-table :data="issues" stripe>
      <el-table-column prop="scooter_id" label="滑板车编号" width="120" />
      <el-table-column prop="description" label="问题描述" />
      <el-table-column prop="created_at" label="报告时间" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.created_at) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="scope">
          <el-tag :type="getIssueStatusType(scope.row.status)">
            {{ getIssueStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="feedback" label="处理反馈" />
    </el-table>
    
    <el-dialog
      v-model="dialogVisible"
      title="报告问题"
      width="500px"
    >
      <el-form :model="issueForm" label-width="100px">
        <el-form-item label="滑板车编号">
          <el-input v-model="issueForm.scooter_id" />
        </el-form-item>
        <el-form-item label="问题描述">
          <el-input
            v-model="issueForm.description"
            type="textarea"
            rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitIssue">
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { formatTime } from '@/utils/date'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const issues = ref([])
const dialogVisible = ref(false)
const issueForm = ref({
  scooter_id: '',
  description: ''
})

const fetchIssues = async () => {
  try {
    const response = await axios.get('/api/issues')
    issues.value = response.data
  } catch (error) {
    console.error('获取问题列表失败:', error)
  }
}

const showReportDialog = () => {
  issueForm.value = {
    scooter_id: '',
    description: ''
  }
  dialogVisible.value = true
}

const submitIssue = async () => {
  try {
    await axios.post('/api/issues', issueForm.value)
    ElMessage.success('问题已提交')
    dialogVisible.value = false
    fetchIssues()
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

const getIssueStatusType = (status) => {
  const types = {
    pending: 'warning',
    'in_progress': 'primary',
    resolved: 'success'
  }
  return types[status] || 'info'
}

const getIssueStatusText = (status) => {
  const texts = {
    pending: '待处理',
    'in_progress': '处理中',
    resolved: '已解决'
  }
  return texts[status] || status
}

onMounted(() => {
  fetchIssues()
})
</script>

<style scoped>
.issues-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style> 