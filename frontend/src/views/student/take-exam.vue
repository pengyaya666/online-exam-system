<template>
  <div class="exam-page">
    <!-- 考试头部 -->
    <div class="exam-header-bar">
      <div class="header-left">
        <h2>{{ examInfo.title }}</h2>
        <span class="exam-status" :class="{ warning: remainingTime < 300 }">
          <el-icon><Timer /></el-icon>
          剩余时间：{{ formatTime(remainingTime) }}
        </span>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="submitExam">交卷</el-button>
      </div>
    </div>
    
    <!-- 考试内容 -->
    <div class="exam-content">
      <div class="questions-area">
        <el-card
          v-for="(question, index) in questions"
          :key="question.id"
          :id="`question-${index}`"
          class="question-card"
          :class="{ current: currentQuestionIndex === index }"
        >
          <div class="question-header">
            <span class="question-number">第 {{ index + 1 }} 题</span>
            <span class="question-type">[{{ getQuestionTypeName(question.type) }}]</span>
            <span class="question-score">{{ question.score }}分</span>
          </div>
          
          <div class="question-content">
            <p>{{ question.content }}</p>
          </div>
          
          <!-- 单选题 -->
          <div v-if="question.type === 1" class="options">
            <el-radio-group v-model="answers[question.id]">
              <el-radio
                v-for="(option, optIndex) in question.optionList"
                :key="optIndex"
                :label="getOptionKey(option)"
                class="option-item"
              >
                {{ option }}
              </el-radio>
            </el-radio-group>
          </div>
          
          <!-- 多选题 -->
          <div v-else-if="question.type === 2" class="options">
            <el-checkbox-group v-model="answers[question.id]">
              <el-checkbox
                v-for="(option, optIndex) in question.optionList"
                :key="optIndex"
                :label="getOptionKey(option)"
                class="option-item"
              >
                {{ option }}
              </el-checkbox>
            </el-checkbox-group>
          </div>
          
          <!-- 判断题 -->
          <div v-else-if="question.type === 3" class="options">
            <el-radio-group v-model="answers[question.id]">
              <el-radio label="true" class="option-item">正确</el-radio>
              <el-radio label="false" class="option-item">错误</el-radio>
            </el-radio-group>
          </div>
        </el-card>
      </div>
      
      <!-- 答题卡 -->
      <div class="answer-sheet">
        <el-card>
          <template #header>
            <div class="answer-sheet-header">
              <span>答题卡</span>
              <span class="progress">{{ answeredCount }}/{{ questions.length }}</span>
            </div>
          </template>
          
          <div class="answer-sheet-grid">
            <div
              v-for="(question, index) in questions"
              :key="index"
              class="answer-sheet-item"
              :class="{ answered: isAnswered(question.id), current: currentQuestionIndex === index }"
              @click="scrollToQuestion(index)"
            >
              {{ index + 1 }}
            </div>
          </div>
          
          <div class="answer-sheet-legend">
            <div class="legend-item">
              <span class="legend-dot answered"></span>
              <span>已答</span>
            </div>
            <div class="legend-item">
              <span class="legend-dot"></span>
              <span>未答</span>
            </div>
          </div>
          
          <el-divider />
          
          <el-button type="primary" size="large" @click="submitExam" style="width: 100%">
            交卷
          </el-button>
        </el-card>
      </div>
    </div>
    
    <!-- 切屏警告 -->
    <el-dialog
      v-model="switchDialogVisible"
      title="警告"
      :close-on-click-modal="false"
      :show-close="false"
      width="400px"
    >
      <p>您已切屏 {{ switchCount }} 次，请专注于考试！</p>
      <p v-if="switchCount >= 3" style="color: #f56c6c;">多次切屏可能会被判定为作弊！</p>
      <template #footer>
        <el-button type="primary" @click="switchDialogVisible = false">我知道了</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExamById, startExam, submitExam as apiSubmitExam } from '@/api/exam'

const route = useRoute()
const router = useRouter()

const examId = route.params.id
const examInfo = ref({})
const questions = ref([])
const answers = ref({})
const recordId = ref(null)

const remainingTime = ref(0)
const timer = ref(null)
const currentQuestionIndex = ref(0)
const switchCount = ref(0)
const switchDialogVisible = ref(false)

// 多选题答案用数组存储
const multipleAnswers = ref({})

const answeredCount = computed(() => {
  return questions.value.filter(q => isAnswered(q.id)).length
})

const formatTime = (seconds) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const getQuestionTypeName = (type) => {
  const types = { 1: '单选题', 2: '多选题', 3: '判断题', 4: '填空题' }
  return types[type] || '未知'
}

const getOptionKey = (option) => {
  return option.charAt(0)
}

const isAnswered = (questionId) => {
  const answer = answers.value[questionId]
  if (Array.isArray(answer)) {
    return answer.length > 0
  }
  return answer !== undefined && answer !== ''
}

const scrollToQuestion = (index) => {
  currentQuestionIndex.value = index
  const element = document.getElementById(`question-${index}`)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth', block: 'center' })
  }
}

const startTimer = () => {
  timer.value = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
      if (remainingTime.value === 300) {
        ElMessage.warning('考试还剩5分钟，请抓紧时间！')
      }
    } else {
      clearInterval(timer.value)
      ElMessage.warning('考试时间已到，系统将自动交卷')
      submitExam()
    }
  }, 1000)
}

const handleVisibilityChange = () => {
  if (document.hidden) {
    switchCount.value++
    switchDialogVisible.value = true
  }
}

const submitExam = async () => {
  const unansweredCount = questions.value.length - answeredCount.value
  
  if (unansweredCount > 0) {
    try {
      await ElMessageBox.confirm(
        `您还有 ${unansweredCount} 道题未作答，确定要交卷吗？`,
        '确认交卷',
        {
          confirmButtonText: '确定交卷',
          cancelButtonText: '继续答题',
          type: 'warning'
        }
      )
    } catch {
      return
    }
  }
  
  // 处理答案格式
  const formattedAnswers = questions.value.map(q => {
    let answer = answers.value[q.id]
    if (Array.isArray(answer)) {
      answer = answer.sort().join('')
    }
    return {
      questionId: q.id,
      answer: answer || ''
    }
  })
  
  try {
    await apiSubmitExam({
      recordId: recordId.value,
      answers: formattedAnswers
    })
    
    ElMessage.success('交卷成功')
    router.push(`/student/result/${recordId.value}`)
  } catch (error) {
    console.error(error)
  }
}

const loadExam = async () => {
  try {
    console.log('=== 开始加载考试 ===')
    console.log('考试ID:', examId)
    
    const res = await getExamById(examId)
    console.log('接口返回的原始数据:', res)
    
    examInfo.value = res
    console.log('questions数据:', res.questions)
    
    questions.value = res.questions || []
    
    // 初始化答案对象
    questions.value.forEach(q => {
      if (q.type === 2) {
        answers.value[q.id] = []
      } else {
        answers.value[q.id] = ''
      }
    })
    
    // 开始考试
    const startRes = await startExam(examId)
    recordId.value = startRes.id
    remainingTime.value = examInfo.value.duration * 60
    
    startTimer()
  } catch (error) {
    console.error('加载考试失败:', error)
    ElMessage.error('加载考试失败')
    router.push('/student/exams')
  }
}

onMounted(() => {
  loadExam()
  document.addEventListener('visibilitychange', handleVisibilityChange)
  
  // 监听滚动更新当前题目
  window.addEventListener('scroll', () => {
    const scrollPosition = window.scrollY + window.innerHeight / 2
    
    for (let i = 0; i < questions.value.length; i++) {
      const element = document.getElementById(`question-${i}`)
      if (element) {
        const rect = element.getBoundingClientRect()
        if (rect.top <= window.innerHeight / 2 && rect.bottom >= window.innerHeight / 2) {
          currentQuestionIndex.value = i
          break
        }
      }
    }
  })
})

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})
</script>

<style scoped>
.exam-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.exam-header-bar {
  position: fixed;
  top: 0;
  left: 220px;
  right: 0;
  height: 60px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-left h2 {
  margin: 0;
  font-size: 18px;
}

.exam-status {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 5px 15px;
  background-color: #f0f9ff;
  border-radius: 20px;
  color: #409eff;
  font-weight: bold;
}

.exam-status.warning {
  background-color: #fef0f0;
  color: #f56c6c;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.exam-content {
  display: flex;
  padding: 80px 20px 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.questions-area {
  flex: 1;
  margin-right: 20px;
}

.question-card {
  margin-bottom: 20px;
  scroll-margin-top: 80px;
}

.question-card.current {
  border-color: #409eff;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.question-number {
  font-weight: bold;
  color: #409eff;
}

.question-type {
  color: #909399;
  font-size: 14px;
}

.question-score {
  color: #f56c6c;
  font-size: 14px;
  margin-left: auto;
}

.question-content {
  font-size: 16px;
  line-height: 1.8;
  margin-bottom: 20px;
}

.options {
  padding-left: 20px;
}

.option-item {
  display: block;
  margin: 10px 0;
  padding: 10px;
  border-radius: 4px;
  transition: all 0.3s;
}

.option-item:hover {
  background-color: #f5f7fa;
}

.answer-sheet {
  width: 280px;
  position: sticky;
  top: 80px;
  align-self: flex-start;
}

.answer-sheet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress {
  color: #409eff;
  font-weight: bold;
}

.answer-sheet-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  margin-bottom: 20px;
}

.answer-sheet-item {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.answer-sheet-item:hover {
  border-color: #409eff;
}

.answer-sheet-item.answered {
  background-color: #409eff;
  color: #fff;
  border-color: #409eff;
}

.answer-sheet-item.current {
  border-color: #67c23a;
  border-width: 2px;
}

.answer-sheet-legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 15px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #606266;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 2px;
}

.legend-dot.answered {
  background-color: #409eff;
  border-color: #409eff;
}
</style>