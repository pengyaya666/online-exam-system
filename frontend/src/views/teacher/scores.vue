<template>
  <div class="page-container">
    <!-- 考试选择 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true">
        <el-form-item label="选择考试">
          <el-select
            v-model="selectedExamId"
            placeholder="请选择考试"
            style="width: 300px"
            @change="handleExamChange"
          >
            <el-option
              v-for="exam in examList"
              :key="exam.id"
              :label="exam.title"
              :value="exam.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row" v-if="statistics">
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <p class="stat-label">参加人数</p>
          <p class="stat-value">{{ statistics.submittedCount }}</p>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <p class="stat-label">平均分</p>
          <p class="stat-value">{{ statistics.averageScore?.toFixed(1) }}</p>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <p class="stat-label">最高分</p>
          <p class="stat-value">{{ statistics.maxScore }}</p>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <p class="stat-label">最低分</p>
          <p class="stat-value">{{ statistics.minScore }}</p>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <p class="stat-label">及格率</p>
          <p class="stat-value">{{ statistics.passRate?.toFixed(1) }}%</p>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <p class="stat-label">总分</p>
          <p class="stat-value">{{ currentExam?.totalScore }}</p>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row" v-if="statistics">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>分数段分布</span>
          </template>
          <div ref="scoreChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>各题正确率</span>
          </template>
          <div ref="questionChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 成绩列表 -->
    <el-card shadow="hover" v-if="selectedExamId">
      <template #header>
        <div class="card-header">
          <span>成绩列表</span>
          <el-button type="primary" @click="exportScores">导出成绩</el-button>
        </div>
      </template>
      
      <!-- 搜索和排序栏 -->
      <div class="search-bar">
        <el-form :inline="true" :model="searchForm">
          <el-form-item>
            <el-input
              v-model="searchForm.keyword"
              placeholder="请输入学生姓名"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item>
            <el-select
              v-model="searchForm.sortBy"
              placeholder="排序方式"
              clearable
              style="width: 150px"
              @change="handleSearch"
            >
              <el-option label="按分数降序" value="score_desc" />
              <el-option label="按分数升序" value="score_asc" />
              <el-option label="按用时升序" value="time_asc" />
              <el-option label="按用时降序" value="time_desc" />
              <el-option label="按切屏次数降序" value="switch_desc" />
              <el-option label="按切屏次数升序" value="switch_asc" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>搜索
            </el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <el-table :data="records" v-loading="loading" style="width: 100%">
        <el-table-column type="index" label="排名" width="80">
          <template #default="{ $index }">{{ (page - 1) * size + $index + 1 }}</template>
        </el-table-column>
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="studentName" label="学号/用户名" width="150" />
        <el-table-column prop="startTime" label="开始时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column prop="useTime" label="用时" width="100">
          <template #default="{ row }">{{ formatDuration(row.useTime) }}</template>
        </el-table-column>
        <el-table-column prop="score" label="得分" width="100">
          <template #default="{ row }">
            <span :class="row.isPass ? 'pass' : 'fail'">{{ row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="objectiveScore" label="客观题得分" width="100" />
        <el-table-column prop="isPass" label="是否通过" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isPass ? 'success' : 'danger'">
              {{ row.isPass ? '通过' : '未通过' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="switchCount" label="切屏次数" width="100">
          <template #default="{ row }">
            <span :class="{ warning: row.switchCount > 0 }">{{ row.switchCount }}</span>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <el-empty v-if="!selectedExamId" description="请选择考试查看成绩统计" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getExamPage, getExamRecords, getScoreStatistics } from '@/api/exam'
import * as echarts from 'echarts'

const route = useRoute()

const examList = ref([])
const selectedExamId = ref(route.query.examId ? parseInt(route.query.examId) : null)
const currentExam = ref(null)
const statistics = ref(null)
const records = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

const searchForm = reactive({
  keyword: '',
  sortBy: 'score_desc'
})

const scoreChart = ref(null)
const questionChart = ref(null)

let scoreChartInstance = null
let questionChartInstance = null

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

const formatDuration = (seconds) => {
  if (!seconds) return '-'
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}分${secs}秒`
}

const loadExams = async () => {
  try {
    const res = await getExamPage({ page: 1, size: 100, status: 1 })
    examList.value = res.records
    
    if (selectedExamId.value) {
      currentExam.value = examList.value.find(e => e.id === selectedExamId.value)
      await loadStatistics()
      await loadRecords()
    }
  } catch (error) {
    console.error(error)
  }
}

const loadStatistics = async () => {
  if (!selectedExamId.value) return
  
  try {
    const res = await getScoreStatistics(selectedExamId.value)
    statistics.value = res
    
    nextTick(() => {
      initScoreChart(res.scoreDistribution)
      initQuestionChart(res.questionCorrectRate)
    })
  } catch (error) {
    console.error(error)
  }
}

const loadRecords = async () => {
  if (!selectedExamId.value) return
  
  loading.value = true
  try {
    const res = await getExamRecords({
      page: page.value,
      size: size.value,
      examId: selectedExamId.value,
      keyword: searchForm.keyword,
      sortBy: searchForm.sortBy
    })
    records.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleExamChange = async () => {
  currentExam.value = examList.value.find(e => e.id === selectedExamId.value)
  page.value = 1
  searchForm.keyword = ''
  searchForm.sortBy = 'score_desc'
  await loadStatistics()
  await loadRecords()
}

const handleSearch = () => {
  page.value = 1
  loadRecords()
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.sortBy = 'score_desc'
  handleSearch()
}

const initScoreChart = (data) => {
  if (!scoreChart.value) return
  
  if (scoreChartInstance) {
    scoreChartInstance.dispose()
  }
  
  scoreChartInstance = echarts.init(scoreChart.value)
  scoreChartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: data.map(d => d.range),
      axisLabel: { interval: 0 }
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [{
      data: data.map(d => d.count),
      type: 'bar',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      },
      label: {
        show: true,
        position: 'top'
      }
    }]
  })
}

const initQuestionChart = (data) => {
  if (!questionChart.value) return
  
  if (questionChartInstance) {
    questionChartInstance.dispose()
  }
  
  questionChartInstance = echarts.init(questionChart.value)
  questionChartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: data.map((d, i) => `第${i + 1}题`),
      axisLabel: { interval: 0, rotate: 30 }
    },
    yAxis: {
      type: 'value',
      name: '正确率(%)',
      max: 100
    },
    series: [{
      data: data.map(d => d.correctRate.toFixed(1)),
      type: 'bar',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#67c23a' },
          { offset: 1, color: '#95d475' }
        ])
      },
      label: {
        show: true,
        position: 'top',
        formatter: '{c}%'
      }
    }]
  })
}

const exportScores = () => {
  // 导出成绩逻辑
  const csvContent = [
    ['排名', '姓名', '学号/用户名', '开始时间', '用时', '得分', '客观题得分', '是否通过', '切屏次数'],
    ...records.value.map((r, i) => [
      (page.value - 1) * size.value + i + 1,
      r.realName,
      r.studentName,
      formatDateTime(r.startTime),
      formatDuration(r.useTime),
      r.score,
      r.objectiveScore,
      r.isPass ? '通过' : '未通过',
      r.switchCount
    ])
  ].map(row => row.join(',')).join('\n')
  
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `${currentExam.value?.title}_成绩.csv`
  link.click()
  
  ElMessage.success('导出成功')
}

const handleSizeChange = (val) => {
  size.value = val
  loadRecords()
}

const handleCurrentChange = (val) => {
  page.value = val
  loadRecords()
}

onMounted(() => {
  loadExams()
  
  window.addEventListener('resize', () => {
    scoreChartInstance?.resize()
    questionChartInstance?.resize()
  })
})
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 15px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}

.chart-row {
  margin-bottom: 20px;
}

.search-bar {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.pass {
  color: #67c23a;
  font-weight: bold;
}

.fail {
  color: #f56c6c;
  font-weight: bold;
}

.warning {
  color: #f56c6c;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>