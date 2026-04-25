<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">用户管理系统</h2>
      <el-form ref="formRef" :model="loginForm" :rules="rules">
        <el-form-item prop="username" class="login-form-item">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password" class="login-form-item">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: center; color: #999; font-size: 12px;">
        默认账号：admin / 123
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const formRef = ref(null)
const loginForm = reactive({
  username: '',
  password: ''
})
const loading = ref(false)

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  loading.value = true
  try {
    const res = await request.post('/auth/login', {
      username: loginForm.username,
      password: loginForm.password
    })
    
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('username', res.data.username)
    ElMessage.success('登录成功')
    router.push('/user-manage')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
</style>
