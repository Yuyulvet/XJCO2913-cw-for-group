<template>
  <div class="profile">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h3>个人资料</h3>
          <el-button type="primary" @click="startEdit" v-if="!isEditing">
            编辑资料
          </el-button>
        </div>
      </template>

      <el-form 
        :model="userForm" 
        :rules="rules"
        ref="formRef"
        label-width="100px"
        :disabled="!isEditing"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        
        <div v-if="isEditing" class="form-actions">
          <el-button @click="cancelEdit">取消</el-button>
          <el-button type="primary" @click="saveProfile">保存</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h3>修改密码</h3>
        </div>
      </template>

      <el-form 
        :model="passwordForm"
        :rules="passwordRules"
        ref="passwordFormRef"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input 
            v-model="passwordForm.currentPassword" 
            type="password" 
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
          />
        </el-form-item>
        
        <el-button type="primary" @click="changePassword">
          更新密码
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const formRef = ref(null)
const passwordFormRef = ref(null)
const isEditing = ref(false)

const userForm = ref({
  username: '',
  email: '',
  phone: ''
})

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const passwordRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const fetchUserProfile = async () => {
  try {
    const response = await axios.get('/api/user/profile')
    userForm.value = response.data
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

const startEdit = () => {
  isEditing.value = true
}

const cancelEdit = () => {
  isEditing.value = false
  fetchUserProfile()
}

const saveProfile = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    await axios.put('/api/user/profile', userForm.value)
    ElMessage.success('保存成功')
    isEditing.value = false
  } catch (error) {
    if (error.response) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('保存失败')
    }
  }
}

const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    await axios.put('/api/user/password', passwordForm.value)
    ElMessage.success('密码修改成功')
    passwordForm.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (error) {
    if (error.response) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('密码修改失败')
    }
  }
}

onMounted(() => {
  fetchUserProfile()
})
</script>

<style scoped>
.profile {
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style> 