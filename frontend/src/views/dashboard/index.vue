<template>
  <div class="dashboard">
    <!-- 欢迎信息 -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ realName || username }}！</h2>
          <p>{{ welcomeMessage }}</p>
        </div>
        <div class="welcome-time">
          <p>{{ currentDate }}</p>
          <p>{{ currentTime }}</p>
        </div>
      </div>
    </el-card>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="stat in statistics" :key="stat.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :style="{ backgroundColor: stat.color }">
            <el-icon :size="32"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ stat.value }}</p>
            <p class="stat-title">{{ stat.title }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 学生端内容 -->
    <template v-if="userRole === 2">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>待考考试</span>
                <el-button type="primary" text @click="$router.push('/student/exams')">
                  查看更多
                </el-button>
              </div>
            </template>
            <el-table :data="pendingExams" style="width: 100%">
              <el-table-column prop="title" label="考试名称" />
              <el-table-column prop="startTime" label="开始时间" width="180">
                <template #default="{ row }">
                  {{ formatDateTime(row.startTime) }}
                </template>
              </el-table-column>
              <el-table-column prop="duration" label="时长" width="100">
                <template #default="{ row }">
                  {{ row.duration }}分钟
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="startExam(row.id)">
                    进入考试
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-if="pendingExams.length === 0" description="暂无待考考试" />
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>最近成绩</span>
                <el-button type="primary" text @click="$router.push('/student/scores')">
                  查看更多
                </el-button>
              </div>
            </template>
            <div v-for="record in recentScores" :key="record.id" class="score-item">
              <div class="score-info">
                <p class="score-title">{{ record.examTitle }}</p>
                <p class="score-time">{{ formatDateTime(record.createTime) }}</p>
              </div>
              <div class="score-value" :class="record.isPass ? 'pass' : 'fail'">
                {{ record.score }}分
              </div>
            </div>
            <el-empty v-if="recentScores.length === 0" description="暂无考试记录" />
          </el-card>
        </el-col>
      </el-row>
    </template>
    
    <!-- 教师端内容 -->
    <template v-if="userRole === 1">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>我创建的考试</span>
                <el-button type="primary" text @click="$router.push('/teacher/exams')">
                  查看更多
                </el-button>
              </div>
            </template>
            <el-table :data="myExams" style="width: 100%">
              <el-table-column prop="title" label="考试名称" />
              <el-table-column prop="statusName" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">{{ row.statusName }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="participatedCount" label="参加人数" width="100" />
            </el-table>
            <el-empty v-if="myExams.length === 0" description="暂无考试" />
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>我的题库</span>
                <el-button type="primary" text @click="$router.push('/teacher/banks')">
                  查看更多
                </el-button>
              </div>
            </template>
            <el-table :data="myBanks" style="width: 100%">
              <el-table-column prop="name" label="题库名称" />
              <el-table-column prop="createTime" label="创建时间" width="180">
                <template #default="{ row }">
                  {{ formatDateTime(row.createTime) }}
                </template>
              </el-table-column>
            </el-table>
            <el-empty v-if="myBanks.length === 0" description="暂无题库" />
          </el-card>
        </el-col>
      </el-row>
    </template>
    
    <!-- 管理员端内容 -->
    <template v-if="userRole === 0">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>用户统计</span>
              </div>
            </template>
            <div ref="userChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>考试统计</span>
              </div>
            </template>
            <div ref="examChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>
    </template>
    
    <!-- 系统公告弹窗 -->
    <el-dialog
      v-model="noticeDialogVisible"
      :title="currentNotice?.title || '系统公告'"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="notice-content" v-html="currentNotice?.content"></div>
      <template #footer>
        <el-button type="primary" @click="noticeDialogVisible = false">知道了</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getActiveExams, getExamRecords } from '@/api/exam'
import { getExamPage } from '@/api/exam'
import { getBankList } from '@/api/bank'
import { getUserPage } from '@/api/user'
import { getCurrentNotice } from '@/api/config'
import * as echarts from 'echarts'

const store = useStore()
const router = useRouter()

const userRole = computed(() => store.getters.userRole)
const totalUsers = ref(0)
const totalExams = ref(0)
const teacherCount = ref(0)
const studentCount = ref(0)
const username = computed(() => store.getters.username)
const realName = computed(() => store.getters.realName)

const currentDate = ref('')
const currentTime = ref('')
const timer = ref(null)

const pendingExams = ref([])
const recentScores = ref([])
const myExams = ref([])
const myBanks = ref([])

const userChart = ref(null)
const examChart = ref(null)

// 公告相关变量
const noticeDialogVisible = ref(false)
const currentNotice = ref(null)

const welcomeMessage = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '祝您有个美好的一天！'
  if (hour < 18) return '下午好，继续加油！'
  return '晚上好，注意休息！'
})

const statistics = computed(() => {
  // 学生端
  if (userRole.value === 2) {
    return [
      { title: '进行中的考试', value: pendingExams.value.length, icon: 'Calendar', color: '#409eff' },
      { title: '已完成考试', value: recentScores.value.length, icon: 'DocumentChecked', color: '#67c23a' }
    ]
  }
  
  // 教师端
  if (userRole.value === 1) {
    return [
      { title: '我的题库', value: myBanks.value.length, icon: 'Collection', color: '#409eff' },
      { title: '我的考试', value: myExams.value.length, icon: 'Calendar', color: '#67c23a' },
      { title: '总参与人数', value: myExams.value.reduce((sum, e) => sum + (e.participatedCount || 0), 0), icon: 'User', color: '#e6a23c' },
      { title: '待发布考试', value: myExams.value.filter(e => e.status === 0).length, icon: 'Timer', color: '#f56c6c' }
    ]
  }
  
  // 管理员端
  if (userRole.value === 0) {
    return [
      { title: '总用户数', value: totalUsers.value || 0, icon: 'User', color: '#409eff' },
      { title: '考试总数', value: totalExams.value || 0, icon: 'Calendar', color: '#67c23a' },
      { title: '教师数量', value: teacherCount.value || 0, icon: 'UserFilled', color: '#e6a23c' },
      { title: '学生数量', value: studentCount.value || 0, icon: 'User', color: '#f56c6c' }
    ]
  }
  
  return []
})

const updateTime = () => {
  const now = new Date()
  currentDate.value = now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
  currentTime.value = now.toLocaleTimeString('zh-CN')
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const startExam = (id) => {
  router.push(`/student/take-exam/${id}`)
}

const loadStudentData = async () => {
  try {
    const exams = await getActiveExams()
    pendingExams.value = exams.filter(e => !e.isParticipated)
    
    const records = await getExamRecords({ page: 1, size: 5 })
    recentScores.value = records.records
  } catch (error) {
    console.error(error)
  }
}

const loadTeacherData = async () => {
  try {
    const exams = await getExamPage({ page: 1, size: 5 })
    myExams.value = exams.records
    
    const banks = await getBankList()
    myBanks.value = banks.slice(0, 5)
  } catch (error) {
    console.error(error)
  }
}

const loadAdminData = async () => {
  try {
    // 加载用户统计 - 获取所有用户
    const userRes = await getUserPage({ page: 1, size: 1000 })
    const allUsers = userRes.records || []
    
    // 统计各角色数量
    const adminCountNum = allUsers.filter(u => u.role === 0).length
    const teacherCountNum = allUsers.filter(u => u.role === 1).length
    const studentCountNum = allUsers.filter(u => u.role === 2).length
    
    const userStats = [
      { value: adminCountNum, name: '管理员' },
      { value: teacherCountNum, name: '教师' },
      { value: studentCountNum, name: '学生' }
    ]
    
    // 加载考试统计 - 获取所有考试
    const examRes = await getExamPage({ page: 1, size: 1000 })
    const allExams = examRes.records || []
    
    // 统计各状态考试数量
    const draftCount = allExams.filter(e => e.status === 0).length
    const publishedCount = allExams.filter(e => e.status === 1).length
    const endedCount = allExams.filter(e => e.status === 2).length
    
    // 初始化图表
    initUserChart(userStats)
    initExamChart([
      { value: draftCount, name: '草稿' },
      { value: publishedCount, name: '已发布' },
      { value: endedCount, name: '已结束' }
    ])
    
    // 更新统计卡片的值
    totalUsers.value = allUsers.length
    totalExams.value = allExams.length
    teacherCount.value = teacherCountNum
    studentCount.value = studentCountNum
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载当前公告
const loadCurrentNotice = async () => {
  try {
    const res = await getCurrentNotice()
    if (res && res.id) {
      currentNotice.value = res
      // 检查是否已经显示过该公告
      const shownNotices = JSON.parse(localStorage.getItem('shown_notices') || '{}')
      const today = new Date().toDateString()
      const noticeKey = `notice_${res.id}_${today}`
      
      if (!shownNotices[noticeKey]) {
        noticeDialogVisible.value = true
        shownNotices[noticeKey] = true
        localStorage.setItem('shown_notices', JSON.stringify(shownNotices))
      }
    }
  } catch (error) {
    console.error('获取公告失败:', error)
  }
}

const initUserChart = (data) => {
  if (!userChart.value) return
  
  const chart = echarts.init(userChart.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      data
    }]
  })
}

const initExamChart = (data) => {
  if (!examChart.value) return
  
  const chart = echarts.init(examChart.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.map(d => d.name) },
    yAxis: { type: 'value' },
    series: [{
      data: data.map(d => d.value),
      type: 'bar',
      itemStyle: { color: '#409eff' }
    }]
  })
}

onMounted(() => {
  updateTime()
  timer.value = setInterval(updateTime, 1000)
  
  if (userRole.value === 2) {
    loadStudentData()
  } else if (userRole.value === 1) {
    loadTeacherData()
  } else if (userRole.value === 0) {
    loadAdminData()
  }
  
  // 加载公告（所有角色都需要）
  loadCurrentNotice()
})

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h2 {
  margin-bottom: 8px;
}

.welcome-time {
  text-align: right;
}

.welcome-time p {
  margin: 4px 0;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-title {
  color: #909399;
  font-size: 14px;
}

.score-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.score-item:last-child {
  border-bottom: none;
}

.score-title {
  font-weight: 500;
  margin-bottom: 4px;
}

.score-time {
  font-size: 12px;
  color: #909399;
}

.score-value {
  font-size: 20px;
  font-weight: bold;
}

.score-value.pass {
  color: #67c23a;
}

.score-value.fail {
  color: #f56c6c;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notice-content {
  max-height: 400px;
  overflow-y: auto;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>