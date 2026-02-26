<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item>
          <el-select
            v-model="searchForm.status"
            placeholder="考试状态"
            clearable
            style="width: 150px"
          >
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="已结束" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入考试名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 操作栏 -->
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>创建考试
      </el-button>
    </div>
    
    <!-- 数据表格 -->
    <el-card shadow="hover">
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="title" label="考试名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="bankName" label="关联题库" width="150" />
        <el-table-column prop="duration" label="时长" width="100">
          <template #default="{ row }">{{ row.duration }}分钟</template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column prop="startTime" label="开始时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160">
          <template #default="{ row }">{{ formatDateTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="participatedCount" label="参加人数" width="100" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
            <el-button 
              v-if="row.status === 0" 
              type="success" 
              link 
              @click="handlePublish(row)"
            >发布</el-button>
            <el-button 
              v-if="row.status === 1" 
              type="primary" 
              link 
              @click="viewScores(row)"
            >成绩</el-button>
            <el-button 
              v-if="row.status === 0" 
              type="primary" 
              link 
              @click="handleEdit(row)"
            >编辑</el-button>
            <el-button 
              v-if="row.status === 0" 
              type="danger" 
              link 
              @click="handleDelete(row)"
            >删除</el-button>
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
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="考试标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入考试标题" />
        </el-form-item>
        
        <el-form-item label="考试说明" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="2"
            placeholder="请输入考试说明"
          />
        </el-form-item>
        
        <el-form-item label="选择题库" prop="bankId">
          <el-select v-model="form.bankId" placeholder="选择题库" style="width: 100%">
            <el-option
              v-for="bank in bankList"
              :key="bank.id"
              :label="bank.name"
              :value="bank.id"
            />
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="考试时长" prop="duration">
              <el-input-number v-model="form.duration" :min="1" :max="300" style="width: 100%" />
              <span class="form-tip">分钟</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总分" prop="totalScore">
              <el-input-number v-model="form.totalScore" :min="1" :max="500" style="width: 100%" />
              <span class="form-tip">分</span>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="及格分数" prop="passScore">
          <el-input-number v-model="form.passScore" :min="1" :max="form.totalScore" style="width: 100%" />
        </el-form-item>
        
        <el-divider>题目设置</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单选题数量" prop="singleCount">
              <el-input-number v-model="form.singleCount" :min="0" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单选题分值" prop="singleScore">
              <el-input-number v-model="form.singleScore" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="多选题数量" prop="multipleCount">
              <el-input-number v-model="form.multipleCount" :min="0" :max="50" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="多选题分值" prop="multipleScore">
              <el-input-number v-model="form.multipleScore" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="判断题数量" prop="judgeCount">
              <el-input-number v-model="form.judgeCount" :min="0" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="判断题分值" prop="judgeScore">
              <el-input-number v-model="form.judgeScore" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="预计总分">
          <span class="total-preview">{{ calculateTotalScore }} 分</span>
          <span v-if="calculateTotalScore !== form.totalScore" class="score-warning">
            （与设置的总分不一致）
          </span>
        </el-form-item>
        
        <el-divider>时间设置</el-divider>
        
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
            value-format="YYYY-MM-DDTHH:mm:ss"
            format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
            value-format="YYYY-MM-DDTHH:mm:ss"
            format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        
        <el-form-item label="其他设置">
          <el-checkbox v-model="form.allowMultiple" :true-label="1" :false-label="0">
            允许多次参加
          </el-checkbox>
          <el-checkbox v-model="form.randomOrder" :true-label="1" :false-label="0">
            题目乱序
          </el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExamPage, createExam, updateExam, deleteExam, publishExam } from '@/api/exam'
import { getBankList } from '@/api/bank'

const router = useRouter()

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const bankList = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const searchForm = reactive({
  status: null,
  keyword: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)

const form = reactive({
  id: null,
  title: '',
  description: '',
  bankId: null,
  duration: 60,
  totalScore: 100,
  passScore: 60,
  singleCount: 10,
  singleScore: 5,
  multipleCount: 5,
  multipleScore: 10,
  judgeCount: 10,
  judgeScore: 5,
  startTime: '',
  endTime: '',
  allowMultiple: 0,
  randomOrder: 1
})

const rules = {
  title: [{ required: true, message: '请输入考试标题', trigger: 'blur' }],
  bankId: [{ required: true, message: '请选择题库', trigger: 'change' }],
  duration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }],
  totalScore: [{ required: true, message: '请输入总分', trigger: 'blur' }],
  passScore: [{ required: true, message: '请输入及格分数', trigger: 'blur' }],
  singleCount: [{ required: true, message: '请输入单选题数量', trigger: 'blur' }],
  singleScore: [{ required: true, message: '请输入单选题分值', trigger: 'blur' }],
  multipleCount: [{ required: true, message: '请输入多选题数量', trigger: 'blur' }],
  multipleScore: [{ required: true, message: '请输入多选题分值', trigger: 'blur' }],
  judgeCount: [{ required: true, message: '请输入判断题数量', trigger: 'blur' }],
  judgeScore: [{ required: true, message: '请输入判断题分值', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const calculateTotalScore = computed(() => {
  return form.singleCount * form.singleScore +
         form.multipleCount * form.multipleScore +
         form.judgeCount * form.judgeScore
})

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getExamPage({
      page: page.value,
      size: size.value,
      status: searchForm.status,
      keyword: searchForm.keyword
    })
    tableData.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadBanks = async () => {
  try {
    bankList.value = await getBankList()
  } catch (error) {
    console.error(error)
  }
}

const handleSearch = () => {
  page.value = 1
  loadData()
}

const resetSearch = () => {
  searchForm.status = null
  searchForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '创建考试'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑考试'
  Object.assign(form, row)
  dialogVisible.value = true
}

const resetForm = () => {
  form.id = null
  form.title = ''
  form.description = ''
  form.bankId = null
  form.duration = 60
  form.totalScore = 100
  form.passScore = 60
  form.singleCount = 10
  form.singleScore = 5
  form.multipleCount = 5
  form.multipleScore = 10
  form.judgeCount = 10
  form.judgeScore = 5
  form.startTime = ''
  form.endTime = ''
  form.allowMultiple = 0
  form.randomOrder = 1
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  // 验证时间
  if (new Date(form.startTime) >= new Date(form.endTime)) {
    ElMessage.warning('结束时间必须晚于开始时间')
    return
  }
  
  // 验证总分
  if (calculateTotalScore.value !== form.totalScore) {
    try {
      await ElMessageBox.confirm(
        `题目总分(${calculateTotalScore.value})与设置的总分(${form.totalScore})不一致，是否继续？`,
        '提示',
        {
          confirmButtonText: '继续',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
    } catch {
      return
    }
  }
  
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateExam(form)
      ElMessage.success('更新成功')
    } else {
      await createExam(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除考试"${row.title}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteExam(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handlePublish = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要发布考试"${row.title}"吗？发布后学生可以参加考试。`,
      '确认发布',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await publishExam(row.id)
    ElMessage.success('发布成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const viewDetail = (row) => {
  router.push(`/teacher/exam-detail/${row.id}`)
}

const viewScores = (row) => {
  router.push(`/teacher/scores?examId=${row.id}`)
}

const handleSizeChange = (val) => {
  size.value = val
  loadData()
}

const handleCurrentChange = (val) => {
  page.value = val
  loadData()
}

onMounted(() => {
  loadData()
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

.toolbar {
  margin-bottom: 20px;
}

.form-tip {
  margin-left: 10px;
  color: #909399;
}

.total-preview {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}

.score-warning {
  color: #f56c6c;
  margin-left: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
