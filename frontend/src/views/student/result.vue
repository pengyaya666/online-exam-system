<template>
  <div class="page-container">
    <!-- 成绩头部 -->
    <div class="result-header" :class="resultClass">
      <h2>考试结果</h2>
      <div class="result-score">{{ record.score }}</div>
      <div class="result-status">{{ record.isPass ? '通过' : '未通过' }}</div>
      <p class="exam-title">{{ record.examTitle }}</p>
    </div>
    
    <!-- 成绩详情 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-item">
          <p class="stat-label">总分</p>
          <p class="stat-value">{{ record.totalScore }}</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-item">
          <p class="stat-label">及格分</p>
          <p class="stat-value">{{ record.passScore }}</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-item">
          <p class="stat-label">用时</p>
          <p class="stat-value">{{ formatDuration(record.useTime) }}</p>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-item">
          <p class="stat-label">切屏次数</p>
          <p class="stat-value" :class="{ warning: record.switchCount > 0 }">{{ record.switchCount || 0 }}</p>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 答题详情 -->
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>答题详情</span>
          <el-radio-group v-model="filterType" size="small">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="correct">正确</el-radio-button>
            <el-radio-button label="wrong">错误</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      
      <div
        v-for="(answer, index) in filteredAnswers"
        :key="answer.id"
        class="answer-detail"
        :class="{ correct: answer.isCorrect === 1, wrong: answer.isCorrect === 0 }"
      >
        <div class="answer-header">
          <span class="question-number">第 {{ index + 1 }} 题</span>
          <el-tag :type="answer.isCorrect === 1 ? 'success' : 'danger'" size="small">
            {{ answer.isCorrect === 1 ? '正确' : '错误' }}
          </el-tag>
          <span class="question-score">{{ answer.score }}/{{ answer.fullScore }}分</span>
        </div>
        
        <div class="question-content">
          <p>{{ answer.question.content }}</p>
        </div>
        
        <div class="answer-info">
          <div class="answer-row">
            <span class="label">您的答案：</span>
            <span class="value" :class="{ correct: answer.isCorrect === 1, wrong: answer.isCorrect === 0 }">
              {{ formatAnswer(answer.answer) || '未作答' }}
            </span>
          </div>
          <div class="answer-row">
            <span class="label">正确答案：</span>
            <span class="value correct">{{ formatAnswer(answer.correctAnswer) }}</span>
          </div>
          <div v-if="answer.question.analysis" class="answer-row analysis">
            <span class="label">答案解析：</span>
            <span class="value">{{ answer.question.analysis }}</span>
          </div>
        </div>
      </div>
      
      <el-empty v-if="filteredAnswers.length === 0" description="暂无答题记录" />
    </el-card>
    
    <div class="action-buttons">
      <el-button type="primary" @click="$router.push('/student/exams')">返回考试列表</el-button>
      <el-button @click="$router.push('/student/scores')">查看所有成绩</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getExamResult } from '@/api/exam'

const route = useRoute()
const router = useRouter()

const recordId = route.params.recordId
const record = ref({})
const filterType = ref('all')
const examId = ref(null)

const resultClass = computed(() => {
  return record.value.isPass ? 'pass' : 'fail'
})

const filteredAnswers = computed(() => {
  if (!record.value.answers) return []
  
  switch (filterType.value) {
    case 'correct':
      return record.value.answers.filter(a => a.isCorrect === 1)
    case 'wrong':
      return record.value.answers.filter(a => a.isCorrect === 0)
    default:
      return record.value.answers
  }
})

const formatDuration = (seconds) => {
  if (!seconds) return '-'
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}分${secs}秒`
}

const formatAnswer = (answer) => {
  if (!answer) return ''
  if (answer === 'true') return '正确'
  if (answer === 'false') return '错误'
  return answer
}

const loadResult = async () => {
  try {
    const res = await getExamResult(recordId)
    record.value = res
    examId.value = res.examId
    
    // 从 sessionStorage 读取切屏次数
    const savedSwitchCount = sessionStorage.getItem(`exam_${examId.value}_result_switchCount`)
    if (savedSwitchCount) {
      record.value.switchCount = parseInt(savedSwitchCount)
      sessionStorage.removeItem(`exam_${examId.value}_result_switchCount`)
    }
    
  } catch (error) {
    console.error(error)
    router.push('/student/scores')
  }
}

onMounted(() => {
  loadResult()
})
</script>

<style scoped>
.result-header {
  text-align: center;
  padding: 50px 20px;
  border-radius: 8px;
  margin-bottom: 30px;
  color: #fff;
}

.result-header.pass {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.result-header.fail {
  background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%);
}

.result-header h2 {
  margin-bottom: 20px;
  font-size: 24px;
}

.result-score {
  font-size: 80px;
  font-weight: bold;
  margin: 20px 0;
}

.result-status {
  font-size: 24px;
  padding: 10px 30px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: inline-block;
  margin-bottom: 15px;
}

.exam-title {
  font-size: 16px;
  opacity: 0.9;
}

.stat-row {
  margin-bottom: 30px;
}

.stat-item {
  text-align: center;
  padding: 20px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-value.warning {
  color: #f56c6c;
}

.answer-detail {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #fff;
}

.answer-detail:last-child {
  border-bottom: none;
}

.answer-detail.correct {
  background-color: #f0f9ff;
}

.answer-detail.wrong {
  background-color: #fef0f0;
}

.answer-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.question-number {
  font-weight: bold;
  color: #303133;
}

.question-score {
  margin-left: auto;
  color: #f56c6c;
  font-weight: bold;
}

.question-content {
  font-size: 16px;
  line-height: 1.8;
  margin-bottom: 15px;
  color: #303133;
}

.answer-info {
  padding-left: 20px;
}

.answer-row {
  margin: 8px 0;
  display: flex;
}

.answer-row .label {
  color: #909399;
  width: 100px;
  flex-shrink: 0;
}

.answer-row .value {
  flex: 1;
}

.answer-row .value.correct {
  color: #67c23a;
  font-weight: bold;
}

.answer-row .value.wrong {
  color: #f56c6c;
  text-decoration: line-through;
}

.answer-row.analysis {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.action-buttons {
  text-align: center;
  margin-top: 30px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>