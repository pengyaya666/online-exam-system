<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="profile-header">
            <el-avatar :size="100" :icon="UserFilled" />
            <h3>{{ userInfo.realName || userInfo.username }}</h3>
            <p>{{ userInfo.roleName }}</p>
          </div>
          <div class="profile-info">
            <div class="info-item">
              <span class="label">用户名：</span>
              <span>{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱：</span>
              <span>{{ userInfo.email || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机号：</span>
              <span>{{ userInfo.phone || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">注册时间：</span>
              <span>{{ formatDateTime(userInfo.createTime) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>编辑资料</span>
          </template>
          
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            style="max-width: 500px"
          >
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmit" :loading="loading">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card shadow="hover" style="margin-top: 20px">
          <template #header>
            <span>修改密码</span>
          </template>
          
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            style="max-width: 500px"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                show-password
                placeholder="请输入原密码"
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                show-password
                placeholder="请输入新密码"
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                show-password
                placeholder="请再次输入新密码"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handlePasswordSubmit" :loading="passwordLoading">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { getUserInfo } from '@/api/auth'
import { updateUser, changePassword } from '@/api/user'

const store = useStore()

const userInfo = ref({})
const loading = ref(false)
const passwordLoading = ref(false)

const formRef = ref(null)
const passwordFormRef = ref(null)

const form = reactive({
  realName: '',
  email: '',
  phone: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    userInfo.value = res
    form.realName = res.realName || ''
    form.email = res.email || ''
    form.phone = res.phone || ''
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await updateUser({
      id: userInfo.value.id,
      ...form
    })
    ElMessage.success('保存成功')
    loadUserInfo()
    store.dispatch('updateUserInfo', { ...userInfo.value, ...form })
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handlePasswordSubmit = async () => {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  passwordLoading.value = true
  try {
    await changePassword(passwordForm.oldPassword, passwordForm.newPassword)
    ElMessage.success('密码修改成功，请重新登录')
    store.dispatch('logout')
    window.location.href = '/login'
  } catch (error) {
    console.error(error)
  } finally {
    passwordLoading.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-header {
  text-align: center;
  padding: 20px 0;
}

.profile-header h3 {
  margin: 15px 0 5px;
  font-size: 20px;
}

.profile-header p {
  color: #909399;
}

.profile-info {
  padding: 20px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: #909399;
}
</style>
