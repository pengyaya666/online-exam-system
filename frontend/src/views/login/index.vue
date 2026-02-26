<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>在线考试系统</h1>
        <p>Online Exam System</p>
      </div>
      
      <el-form
        ref="loginForm"
        :model="form"
        :rules="rules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
        
        <div class="login-options">
          <el-link type="primary" @click="goRegister">还没有账号？立即注册</el-link>
        </div>
        
        <el-divider>测试账号</el-divider>
        
        <div class="test-accounts">
          <el-tag size="small" @click="fillAccount('admin', 'admin123')">管理员</el-tag>
          <el-tag size="small" type="success" @click="fillAccount('teacher', 'teacher123')">教师</el-tag>
          <el-tag size="small" type="warning" @click="fillAccount('student', 'student123')">学生</el-tag>
        </div>
      </el-form>
    </div>
    
    <div class="login-footer">
      <p>基于 Spring Boot + Vue3 的在线考试系统</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login } from '@/api/auth'

const router = useRouter()
const store = useStore()

const loginForm = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  const valid = await loginForm.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const res = await login(form)
    store.dispatch('login', res)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const goRegister = () => {
  router.push('/register')
}

const fillAccount = (username, password) => {
  form.username = username
  form.password = password
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  padding: 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}

.login-header p {
  color: #999;
  font-size: 14px;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
}

.login-options {
  text-align: center;
  margin-top: 15px;
}

.test-accounts {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.test-accounts .el-tag {
  cursor: pointer;
}

.login-footer {
  margin-top: 30px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}
</style>
