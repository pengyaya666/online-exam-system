<template>
  <div class="page-container">
    <el-page-header @back="$router.back()" title="返回" />
    
    <el-card shadow="hover" v-if="examInfo" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>考试信息</span>
          <el-tag :type="getStatusType(examInfo.status)">{{ examInfo.statusName }}</el-tag>
        </div>
      </template>
      
      <el-descriptions :column="3" border>
        <el-descriptions-item label="考试标题">{{ examInfo.title }}</el-descriptions-item>
        <el-descriptions-item label="关联题库">{{ examInfo.bankName }}</el-descriptions-item>
        <el-descriptions-item label="考试时长">{{ examInfo.duration }}分钟</el-descriptions-item>
        <el-descriptions-item label="总分">{{ examInfo.totalScore }}分</el-descriptions-item>
        <el-descriptions-item label="及格分">{{ examInfo.passScore }}分</el-descriptions-item>
        <el-descriptions-item label="参加人数">{{ examInfo.participatedCount }}人</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDateTime(examInfo.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatDateTime(examInfo.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ examInfo.createByName }}</el-descriptions-item>
      </el-descriptions>
      
      <div class="exam-description" v-if="examInfo.description">
        <h4>考试说明</h4>
        <p>{{ examInfo.description }}</p>
      </div>
    </el-card>
    
    <el-card shadow="hover" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>题目列表 (共{{ examInfo?.questionCount }}题)</span>
        </div>
      </template>
      
      <div
        v-for="(question, index) in examInfo?.questions"
        :key="question.id"
        class="question-item"
      >
        <div class="question-header">
          <span class="question-number">第 {{ index + 1 }} 题</span>
          <el-tag size="small">{{ question.typeName }}</el-tag>
          <span class="question-score">{{ question.score }}分</span>
        </div>
        <div class="question-content">{{ question.content }}</div>
        <div class="question-options" v-if="question.optionList">
          <div v-for="(opt, optIndex) in question.optionList" :key="optIndex" class="option">
            {{ opt }}
          </div>
        </div>
        <div class="question-answer">
          <span class="label">正确答案：</span>
          <span class="value">{{ formatAnswer(question.answer) }}</span>
        </div>
      </div>
      
      <el-empty v-if="!examInfo?.questions?.length" description="暂无题目" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getExamById } from '@/api/exam'

const route = useRoute()
const examId = route.params.id

const examInfo = ref(null)

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

const formatAnswer = (answer) => {
  if (!answer) return '-'
  if (answer === 'true') return '正确'
  if (answer === 'false') return '错误'
  return answer
}

const loadExamDetail = async () => {
  try {
    const res = await getExamById(examId)
    examInfo.value = res
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadExamDetail()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exam-description {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.exam-description h4 {
  margin-bottom: 10px;
  color: #303133;
}

.exam-description p {
  color: #606266;
  line-height: 1.6;
}

.question-item {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.question-item:last-child {
  border-bottom: none;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.question-number {
  font-weight: bold;
  color: #409eff;
}

.question-score {
  margin-left: auto;
  color: #f56c6c;
  font-weight: bold;
}

.question-content {
  font-size: 15px;
  line-height: 1.8;
  margin-bottom: 15px;
  color: #303133;
}

.question-options {
  padding-left: 20px;
  margin-bottom: 15px;
}

.option {
  padding: 5px 0;
  color: #606266;
}

.question-answer {
  padding: 10px;
  background-color: #f0f9ff;
  border-radius: 4px;
}

.question-answer .label {
  color: #909399;
}

.question-answer .value {
  color: #67c23a;
  font-weight: bold;
}
</style>
