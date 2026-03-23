<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item>
          <el-select
            v-model="searchForm.bankId"
            placeholder="按科目筛选"
            clearable
            style="width: 200px"
            @change="handleSearch"
          >
            <el-option
              v-for="bank in bankList"
              :key="bank.id"
              :label="bank.name"
              :value="bank.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="danger" plain @click="handleClearAll">清空所有错题</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="24">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background-color: #f56c6c;">
            <el-icon :size="32"><Warning /></el-icon>
          </div>
          <div class="stat-info">
            <p class="stat-value">{{ wrongCount }}</p>
            <p class="stat-title">我的错题数</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 错题列表 -->
    <el-card shadow="hover">
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="bankName" label="科目" width="150" />
        <el-table-column prop="examTitle" label="来源考试" width="200" />
        <el-table-column prop="questionContent" label="题目内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="questionTypeName" label="题型" width="100" />
        <el-table-column prop="studentAnswer" label="你的答案" width="150">
          <template #default="{ row }">
            <el-tag type="danger" size="small">{{ formatAnswer(row.studentAnswer) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="correctAnswer" label="正确答案" width="150">
          <template #default="{ row }">
            <el-tag type="success" size="small">{{ formatAnswer(row.correctAnswer) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">查看详情</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
    
    <!-- 错题详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      :title="'错题详情'"
      width="700px"
    >
      <div v-if="currentWrong">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="题目内容">
            {{ currentWrong.questionContent }}
          </el-descriptions-item>
          <el-descriptions-item label="题型">
            {{ currentWrong.questionTypeName }}
          </el-descriptions-item>
          <el-descriptions-item label="你的答案">
            <el-tag type="danger">{{ formatAnswer(currentWrong.studentAnswer) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="正确答案">
            <el-tag type="success">{{ formatAnswer(currentWrong.correctAnswer) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="知识点">
            {{ currentWrong.knowledgePoint || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="难度">
            <el-tag :type="currentWrong.difficulty === 3 ? 'danger' : currentWrong.difficulty === 2 ? 'warning' : 'success'">
              {{ currentWrong.difficultyName }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="答案解析">
            {{ currentWrong.analysis || '暂无解析' }}
          </el-descriptions-item>
          <el-descriptions-item label="错题时间">
            {{ formatDateTime(currentWrong.wrongTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getWrongQuestionPage, getWrongCount, deleteWrongQuestion, clearWrongQuestions } from '@/api/wrong'
import { getBankList } from '@/api/bank'

const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const wrongCount = ref(0)

const searchForm = ref({
  bankId: null
})

const bankList = ref([])
const detailVisible = ref(false)
const currentWrong = ref(null)

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

const formatAnswer = (answer) => {
  if (!answer) return '未作答'
  if (answer === 'true') return '正确'
  if (answer === 'false') return '错误'
  return answer
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getWrongQuestionPage({
      page: page.value,
      size: size.value,
      bankId: searchForm.value.bankId
    })
    tableData.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadWrongCount = async () => {
  try {
    const res = await getWrongCount()
    wrongCount.value = res
  } catch (error) {
    console.error(error)
  }
}

const loadBanks = async () => {
  try {
    const res = await getBankList()
    bankList.value = res
  } catch (error) {
    console.error(error)
  }
}

const handleSearch = () => {
  page.value = 1
  loadData()
}

const resetSearch = () => {
  searchForm.value.bankId = null
  handleSearch()
}

const viewDetail = (row) => {
  currentWrong.value = row
  detailVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除这道错题吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteWrongQuestion(row.id)
    ElMessage.success('删除成功')
    loadData()
    loadWrongCount()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleClearAll = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要清空所有错题吗？此操作不可恢复。`,
      '确认清空',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await clearWrongQuestions()
    ElMessage.success('清空成功')
    loadData()
    loadWrongCount()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadData()
  loadWrongCount()
  loadBanks()
})
</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 4px;
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>