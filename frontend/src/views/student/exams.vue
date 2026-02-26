<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的考试</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="8" v-for="exam in examList" :key="exam.id">
          <el-card shadow="hover" class="exam-card">
            <div class="exam-header">
              <h3 class="exam-title">{{ exam.title }}</h3>
              <el-tag :type="exam.isParticipated ? 'info' : 'success'">
                {{ exam.isParticipated ? '已参加' : '未参加' }}
              </el-tag>
            </div>
            
            <div class="exam-info">
              <p><el-icon><Timer /></el-icon> 时长：{{ exam.duration }}分钟</p>
              <p><el-icon><Document /></el-icon> 总分：{{ exam.totalScore }}分</p>
              <p><el-icon><Trophy /></el-icon> 及格：{{ exam.passScore }}分</p>
              <p><el-icon><User /></el-icon> 已参加：{{ exam.participatedCount }}人</p>
            </div>
            
            <div class="exam-time">
              <p>开始：{{ formatDateTime(exam.startTime) }}</p>
              <p>结束：{{ formatDateTime(exam.endTime) }}</p>
            </div>
            
            <div class="exam-actions">
              <el-button
                type="primary"
                :disabled="exam.isParticipated || !isExamAvailable(exam)"
                @click="startExam(exam)"
              >
                {{ getExamButtonText(exam) }}
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-empty v-if="examList.length === 0" description="暂无考试" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getActiveExams, startExam as apiStartExam } from '@/api/exam'

const router = useRouter()

const examList = ref([])

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

const isExamAvailable = (exam) => {
  const now = new Date()
  const startTime = new Date(exam.startTime)
  const endTime = new Date(exam.endTime)
  return now >= startTime && now <= endTime
}

const getExamButtonText = (exam) => {
  if (exam.isParticipated) return '已参加'
  
  const now = new Date()
  const startTime = new Date(exam.startTime)
  const endTime = new Date(exam.endTime)
  
  if (now < startTime) return '未开始'
  if (now > endTime) return '已结束'
  return '进入考试'
}

const startExam = async (exam) => {
  try {
    await ElMessageBox.confirm(
      `确定要开始考试"${exam.title}"吗？考试时长${exam.duration}分钟，请在规定时间内完成。`,
      '开始考试',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const res = await apiStartExam(exam.id)
    ElMessage.success('考试开始')
    router.push(`/student/take-exam/${exam.id}`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const loadData = async () => {
  try {
    const res = await getActiveExams()
    examList.value = res
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.exam-card {
  margin-bottom: 20px;
  transition: all 0.3s;
}

.exam-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.exam-title {
  font-size: 18px;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.exam-info {
  margin-bottom: 15px;
}

.exam-info p {
  margin: 8px 0;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 8px;
}

.exam-time {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 15px;
}

.exam-time p {
  margin: 4px 0;
  font-size: 13px;
  color: #909399;
}

.exam-actions {
  text-align: center;
}

.exam-actions .el-button {
  width: 100%;
}
</style>
