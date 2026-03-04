<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item>
          <el-select
            v-model="searchForm.bankId"
            placeholder="选择题库"
            clearable
            style="width: 200px"
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
          <el-select
            v-model="searchForm.type"
            placeholder="题目类型"
            clearable
            style="width: 150px"
          >
            <el-option label="单选题" :value="1" />
            <el-option label="多选题" :value="2" />
            <el-option label="判断题" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入题目内容"
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
        <el-icon><Plus /></el-icon>新增题目
      </el-button>
      <el-button type="success" @click="handleImport">
        <el-icon><Upload /></el-icon>批量导入
      </el-button>
      <el-button @click="handleDownloadTemplate">
        <el-icon><Download /></el-icon>下载模板
      </el-button>
    </div>
    
    <!-- 数据表格 -->
    <el-card shadow="hover">
      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="content" label="题目内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="typeName" label="题型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeType(row.type)">{{ row.typeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="bankName" label="所属题库" width="150" />
        <el-table-column prop="score" label="分值" width="80" />
        <el-table-column prop="difficultyName" label="难度" width="80">
          <template #default="{ row }">
            <el-tag size="small" :type="getDifficultyType(row.difficulty)">
              {{ row.difficultyName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="knowledgePoint" label="知识点" width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
        label-width="100px"
      >
        <el-form-item label="所属题库" prop="bankId">
          <el-select v-model="form.bankId" placeholder="选择题库" style="width: 100%">
            <el-option
              v-for="bank in bankList"
              :key="bank.id"
              :label="bank.name"
              :value="bank.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="题目类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio-button :label="1">单选题</el-radio-button>
            <el-radio-button :label="2">多选题</el-radio-button>
            <el-radio-button :label="3">判断题</el-radio-button>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="题目内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="3"
            placeholder="请输入题目内容"
          />
        </el-form-item>
        
        <!-- 单选题和多选题选项 -->
        <template v-if="form.type === 1 || form.type === 2">
          <el-form-item label="选项" prop="options">
            <div v-for="(option, index) in optionList" :key="index" class="option-input">
              <el-input v-model="optionList[index]" :placeholder="`选项${String.fromCharCode(65 + index)}`">
                <template #prepend>{{ String.fromCharCode(65 + index) }}</template>
              </el-input>
              <el-button type="danger" link @click="removeOption(index)" v-if="optionList.length > 2">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <el-button type="primary" link @click="addOption" v-if="optionList.length < 8">
              <el-icon><Plus /></el-icon>添加选项
            </el-button>
          </el-form-item>
        </template>
        
        <el-form-item label="正确答案" prop="answer">
          <!-- 单选题 -->
          <el-radio-group v-if="form.type === 1" v-model="form.answer">
            <el-radio
              v-for="(_, index) in optionList"
              :key="index"
              :label="String.fromCharCode(65 + index)"
            >
              {{ String.fromCharCode(65 + index) }}
            </el-radio>
          </el-radio-group>
          
          <!-- 多选题 -->
          <el-checkbox-group v-else-if="form.type === 2" v-model="multiAnswer">
            <el-checkbox
              v-for="(_, index) in optionList"
              :key="index"
              :label="String.fromCharCode(65 + index)"
            >
              {{ String.fromCharCode(65 + index) }}
            </el-checkbox>
          </el-checkbox-group>
          
          <!-- 判断题 -->
          <el-radio-group v-else-if="form.type === 3" v-model="form.answer">
            <el-radio label="true">正确</el-radio>
            <el-radio label="false">错误</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="分值" prop="score">
          <el-input-number v-model="form.score" :min="1" :max="100" />
        </el-form-item>
        
        <el-form-item label="难度" prop="difficulty">
          <el-radio-group v-model="form.difficulty">
            <el-radio-button :label="1">简单</el-radio-button>
            <el-radio-button :label="2">中等</el-radio-button>
            <el-radio-button :label="3">困难</el-radio-button>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="知识点" prop="knowledgePoint">
          <el-input v-model="form.knowledgePoint" placeholder="请输入知识点" />
        </el-form-item>
        
        <el-form-item label="答案解析" prop="analysis">
          <el-input
            v-model="form.analysis"
            type="textarea"
            :rows="3"
            placeholder="请输入答案解析（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 导入对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="批量导入题目"
      width="500px"
    >
      <el-form label-width="100px">
        <el-form-item label="选择题库" required>
          <el-select v-model="importBankId" placeholder="选择题库" style="width: 100%">
            <el-option
              v-for="bank in bankList"
              :key="bank.id"
              :label="bank.name"
              :value="bank.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择文件" required>
          <el-upload
            ref="uploadRef"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
            accept=".xlsx,.xls"
          >
            <el-button type="primary">选择Excel文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                请下载模板并按照格式填写后上传，仅支持.xlsx/.xls文件
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImportSubmit" :loading="importLoading">导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getQuestionPage, addQuestion, updateQuestion, deleteQuestion, importQuestions, downloadTemplate } from '@/api/question'
import { getBankList } from '@/api/bank'

const route = useRoute()

const loading = ref(false)
const submitLoading = ref(false)
const importLoading = ref(false)
const tableData = ref([])
const bankList = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const searchForm = reactive({
  bankId: route.query.bankId ? parseInt(route.query.bankId) : null,
  type: null,
  keyword: ''
})

const dialogVisible = ref(false)
const importDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)

const form = reactive({
  id: null,
  bankId: null,
  type: 1,
  content: '',
  options: '',
  answer: '',
  score: 5,
  difficulty: 1,
  knowledgePoint: '',
  analysis: ''
})

const optionList = ref(['', '', '', ''])
const multiAnswer = ref([])

const importBankId = ref(null)
const importFile = ref(null)
const uploadRef = ref(null)

const rules = {
  bankId: [{ required: true, message: '请选择所属题库', trigger: 'change' }],
  type: [{ required: true, message: '请选择题目类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  answer: [{ required: true, message: '请选择正确答案', trigger: 'change' }],
  score: [{ required: true, message: '请输入分值', trigger: 'blur' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }]
}

const getTypeType = (type) => {
  const types = { 1: '', 2: 'success', 3: 'warning' }
  return types[type] || ''
}

const getDifficultyType = (difficulty) => {
  const types = { 1: 'success', 2: 'warning', 3: 'danger' }
  return types[difficulty] || ''
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getQuestionPage({
      page: page.value,
      size: size.value,
      bankId: searchForm.bankId,
      type: searchForm.type,
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
  searchForm.bankId = null
  searchForm.type = null
  searchForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增题目'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑题目'
  Object.assign(form, row)
  
  // 解析选项
  if (row.optionList && row.optionList.length > 0) {
    optionList.value = row.optionList.map(opt => opt.substring(3))
  }
  
  // 解析多选答案
  if (row.type === 2 && row.answer) {
    multiAnswer.value = row.answer.split('')
  } else {
    multiAnswer.value = []
  }
  
  dialogVisible.value = true
}

const resetForm = () => {
  form.id = null
  form.bankId = searchForm.bankId
  form.type = 1
  form.content = ''
  form.options = ''
  form.answer = ''
  form.score = 5
  form.difficulty = 1
  form.knowledgePoint = ''
  form.analysis = ''
  optionList.value = ['', '', '', '']
  multiAnswer.value = []
}

const addOption = () => {
  optionList.value.push('')
}

const removeOption = (index) => {
  optionList.value.splice(index, 1)
}

const buildOptions = () => {
  const options = {}
  optionList.value.forEach((opt, index) => {
    if (opt.trim()) {
      options[String.fromCharCode(65 + index)] = opt.trim()
    }
  })
  return JSON.stringify(options)
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  // 构建选项JSON
  if (form.type === 1 || form.type === 2) {
    form.options = buildOptions()
    
    // 多选题答案排序
    if (form.type === 2) {
      form.answer = multiAnswer.value.sort().join('')
    }
  }
  
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateQuestion(form)
      ElMessage.success('更新成功')
    } else {
      await addQuestion(form)
      ElMessage.success('添加成功')
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
      `确定要删除这道题目吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteQuestion(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleImport = () => {
  importBankId.value = searchForm.bankId
  importFile.value = null
  importDialogVisible.value = true
}

const handleFileChange = (file) => {
  importFile.value = file.raw
}

const handleImportSubmit = async () => {
  if (!importBankId.value) {
    ElMessage.warning('请选择题库')
    return
  }
  if (!importFile.value) {
    ElMessage.warning('请选择文件')
    return
  }
  
  importLoading.value = true
  try {
    const res = await importQuestions(importBankId.value, importFile.value)
    console.log('导入返回:', res)
    
    // 强行从各种可能的位置获取数据
    let successCount = 0
    let failCount = 0
    
    // 情况1：直接是 {success: 3, fail: 0}
    if (res && typeof res === 'object') {
      if (res.success !== undefined) {
        successCount = res.success
        failCount = res.fail
      }
      // 情况2：是 {data: {success: 3, fail: 0}}
      else if (res.data && res.data.success !== undefined) {
        successCount = res.data.success
        failCount = res.data.fail
      }
      // 情况3：是完整的 Result {code:200, data:{success:3, fail:0}}
      else if (res.code === 200 && res.data && res.data.success !== undefined) {
        successCount = res.data.success
        failCount = res.data.fail
      }
    }
    
    // 如果成功条数大于0，说明导入成功
    if (successCount > 0) {
      ElMessage.success(`导入完成：成功${successCount}条，失败${failCount}条`)
    } else {
      ElMessage.success('导入成功')
    }
    
    importDialogVisible.value = false
    loadData()
    importFile.value = null
    if (uploadRef.value) {
      uploadRef.value.clearFiles()
    }
  } catch (error) {
    console.error('导入失败:', error)
    ElMessage.error('导入失败')
  } finally {
    importLoading.value = false
  }
}

const handleDownloadTemplate = async () => {
  try {
    const response = await downloadTemplate()
    console.log('下载响应:', response)  // 加个日志看看返回的是什么
    
    // 判断返回的是不是已经是 blob
    let blob
    if (response.data) {
      blob = response.data
    } else {
      blob = response
    }
    
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '题目导入模板.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('下载模板失败')
  }
}
const handleSizeChange = (val) => {
  size.value = val
  loadData()
}

const handleCurrentChange = (val) => {
  page.value = val
  loadData()
}

watch(() => form.type, (newType) => {
  form.answer = ''
  multiAnswer.value = []
})

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

.option-input {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
