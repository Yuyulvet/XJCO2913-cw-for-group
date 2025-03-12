<template>
  <div class="login-container">
    <el-form :model="loginForm" @submit.prevent="handleLogin">
      <el-form-item label="用户名">
        <el-input v-model="loginForm.username" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input type="password" v-model="loginForm.password" />
      </el-form-item>
      <el-button type="primary" native-type="submit">登录</el-button>
    </el-form>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

export default {
  setup() {
    const router = useRouter()
    const loginForm = ref({
      username: '',
      password: ''
    })

    const handleLogin = async () => {
      try {
        const response = await axios.post('/api/auth/login', loginForm.value)
        localStorage.setItem('token', response.data.token)
        router.push('/dashboard')
      } catch (error) {
        console.error('登录失败:', error)
      }
    }

    return {
      loginForm,
      handleLogin
    }
  }
}
</script> 