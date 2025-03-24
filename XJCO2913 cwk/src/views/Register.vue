<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <h2>注册账号</h2>
        </div>
      </template>
      
      <el-form 
        ref="registerForm"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model.trim="form.username"
            placeholder="请输入用户名"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input 
            v-model.trim="form.email"
            placeholder="请输入邮箱"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input 
            v-model.trim="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model.trim="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            @keyup.enter="handleRegister"
          />
        </el-form-item>

        <el-form-item label="注册类型" prop="role">
          <el-radio-group v-model="form.role">
            <el-radio label="USER">普通用户</el-radio>
            <el-radio label="MANAGER">管理员</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item 
          v-if="form.role === 'MANAGER'" 
          label="邀请码" 
          prop="inviteCode"
        >
          <el-input 
            v-model.trim="form.inviteCode"
            placeholder="请输入管理员邀请码"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister">
            注册
          </el-button>
          <el-button @click="$router.push('/login')">
            返回登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'

const router = useRouter()
const loading = ref(false)
const registerForm = ref(null)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  role: 'USER',
  inviteCode: ''
})

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const validateInviteCode = (rule, value, callback) => {
  if (form.role === 'MANAGER' && !value) {
    callback(new Error('请输入管理员邀请码'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度必须在3-20个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePass2, trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择注册类型', trigger: 'change' }
  ],
  inviteCode: [
    { validator: validateInviteCode, trigger: 'blur' }
  ]
}

// 监听角色变化，重置邀请码
watch(() => form.role, (newRole) => {
  if (newRole === 'USER') {
    form.inviteCode = ''
  }
})

const handleRegister = async () => {
  if (!registerForm.value) return
  
  try {
    await registerForm.value.validate()
    loading.value = true
    
    const registerData = {
      username: form.username,
      email: form.email,
      password: form.password,
      role: form.role,
      inviteCode: form.role === 'MANAGER' ? form.inviteCode : null
    }
    
    console.log('提交注册数据:', registerData)
    const response = await api.post('/auth/register', registerData)
    console.log('注册响应:', response)
    
    ElMessage.success('注册成功！')
    router.push('/login')
  } catch (error) {
    console.error('注册错误:', error)
    if (error.response?.data) {
      ElMessage.error(error.response.data)
    } else if (error.message) {
      ElMessage.error(error.message)
    } else {
      ElMessage.error('注册失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.register-card {
  width: 100%;
  max-width: 460px;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}
</style> 