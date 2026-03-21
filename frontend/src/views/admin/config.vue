<template>
  <div class="page-container">
    <!-- 标签页切换 -->
    <el-tabs v-model="activeTab">
      <el-tab-pane label="系统配置" name="config">
        <!-- 搜索栏 -->
        <div class="search-form">
          <el-form :inline="true" :model="configSearchForm">
            <el-form-item>
              <el-input
                v-model="configSearchForm.keyword"
                placeholder="请输入配置键或说明"
                clearable
                @keyup.enter="handleConfigSearch"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleConfigSearch">
                <el-icon><Search /></el-icon>搜索
              </el-button>
              <el-button @click="resetConfigSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 操作栏 -->
        <div class="toolbar">
          <el-button type="primary" @click="handleAddConfig">
            <el-icon><Plus /></el-icon>新增配置
          </el-button>
        </div>
        
        <!-- 数据表格 -->
        <el-card shadow="hover">
          <el-table :data="configTable" v-loading="configLoading" style="width: 100%">
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="configKey" label="配置键" width="200" />
            <el-table-column prop="configValue" label="配置值" min-width="200" />
            <el-table-column prop="description" label="说明" min-width="200" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditConfig(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteConfig(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="configPage"
              v-model:page-size="configSize"
              :total="configTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              @size-change="loadConfigData"
              @current-change="loadConfigData"
            />
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="系统公告" name="notice">
        <!-- 搜索栏 -->
        <div class="search-form">
          <el-form :inline="true" :model="noticeSearchForm">
            <el-form-item>
              <el-select
                v-model="noticeSearchForm.status"
                placeholder="公告状态"
                clearable
                style="width: 120px"
              >
                <el-option label="启用" :value="1" />
                <el-option label="禁用" :value="0" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input
                v-model="noticeSearchForm.keyword"
                placeholder="请输入标题或内容"
                clearable
                @keyup.enter="handleNoticeSearch"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleNoticeSearch">
                <el-icon><Search /></el-icon>搜索
              </el-button>
              <el-button @click="resetNoticeSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 操作栏 -->
        <div class="toolbar">
          <el-button type="primary" @click="handleAddNotice">
            <el-icon><Plus /></el-icon>新增公告
          </el-button>
        </div>
        
        <!-- 数据表格 -->
        <el-card shadow="hover">
          <el-table :data="noticeTable" v-loading="noticeLoading" style="width: 100%">
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="title" label="公告标题" width="200" />
            <el-table-column prop="content" label="公告内容" min-width="200" show-overflow-tooltip />
            <el-table-column prop="typeName" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="row.type === 1 ? 'primary' : 'success'">{{ row.typeName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="statusName" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.statusName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="startTime" label="开始时间" width="160">
              <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
            </el-table-column>
            <el-table-column prop="endTime" label="结束时间" width="160">
              <template #default="{ row }">{{ formatDateTime(row.endTime) }}</template>
            </el-table-column>
            <el-table-column prop="createByName" label="创建人" width="100" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEditNotice(row)">编辑</el-button>
                <el-button type="danger" link @click="handleDeleteNotice(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="noticePage"
              v-model:page-size="noticeSize"
              :total="noticeTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
              @size-change="loadNoticeData"
              @current-change="loadNoticeData"
            />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 配置新增/编辑对话框 -->
    <el-dialog
      v-model="configDialogVisible"
      :title="configDialogTitle"
      width="500px"
    >
      <el-form
        ref="configFormRef"
        :model="configForm"
        :rules="configRules"
        label-width="100px"
      >
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="configForm.configKey" placeholder="请输入配置键" :disabled="configIsEdit" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input
            v-model="configForm.configValue"
            type="textarea"
            :rows="3"
            placeholder="请输入配置值"
          />
        </el-form-item>
        <el-form-item label="说明" prop="description">
          <el-input v-model="configForm.description" placeholder="请输入说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfigSubmit" :loading="configSubmitLoading">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 公告新增/编辑对话框 -->
    <el-dialog
      v-model="noticeDialogVisible"
      :title="noticeDialogTitle"
      width="600px"
    >
      <el-form
        ref="noticeFormRef"
        :model="noticeForm"
        :rules="noticeRules"
        label-width="100px"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="noticeForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="noticeForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-radio-group v-model="noticeForm.type">
            <el-radio-button :label="1">系统公告</el-radio-button>
            <el-radio-button :label="2">考试通知</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="noticeForm.status">
            <el-radio-button :label="1">启用</el-radio-button>
            <el-radio-button :label="0">禁用</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
              v-model="noticeForm.startTime"
              type="datetime"
              placeholder="选择开始时间"
              style="width: 100%"
              value-format="YYYY-MM-DDTHH:mm:ss"
              format="YYYY-MM-DD HH:mm:ss"
            />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
              v-model="noticeForm.endTime"
              type="datetime"
              placeholder="选择结束时间"
              style="width: 100%"
              value-format="YYYY-MM-DDTHH:mm:ss"
              format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="noticeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleNoticeSubmit" :loading="noticeSubmitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getConfigPage, saveConfig, updateConfig, deleteConfig,
  getNoticePage, saveNotice, updateNotice, deleteNotice
} from '@/api/config'

// ========== 系统配置相关 ==========
const activeTab = ref('config')
const configLoading = ref(false)
const configSubmitLoading = ref(false)
const configTable = ref([])
const configPage = ref(1)
const configSize = ref(10)
const configTotal = ref(0)

const configSearchForm = reactive({
  keyword: ''
})

const configDialogVisible = ref(false)
const configDialogTitle = ref('')
const configFormRef = ref(null)
const configIsEdit = ref(false)

const configForm = reactive({
  id: null,
  configKey: '',
  configValue: '',
  description: ''
})

const configRules = {
  configKey: [{ required: true, message: '请输入配置键', trigger: 'blur' }],
  configValue: [{ required: true, message: '请输入配置值', trigger: 'blur' }]
}

const loadConfigData = async () => {
  configLoading.value = true
  try {
    const res = await getConfigPage({
      page: configPage.value,
      size: configSize.value,
      keyword: configSearchForm.keyword
    })
    configTable.value = res.records
    configTotal.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    configLoading.value = false
  }
}

const handleConfigSearch = () => {
  configPage.value = 1
  loadConfigData()
}

const resetConfigSearch = () => {
  configSearchForm.keyword = ''
  handleConfigSearch()
}

const handleAddConfig = () => {
  configIsEdit.value = false
  configDialogTitle.value = '新增配置'
  configForm.id = null
  configForm.configKey = ''
  configForm.configValue = ''
  configForm.description = ''
  configDialogVisible.value = true
}

const handleEditConfig = (row) => {
  configIsEdit.value = true
  configDialogTitle.value = '编辑配置'
  configForm.id = row.id
  configForm.configKey = row.configKey
  configForm.configValue = row.configValue
  configForm.description = row.description
  configDialogVisible.value = true
}

const handleConfigSubmit = async () => {
  const valid = await configFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  configSubmitLoading.value = true
  try {
    if (configIsEdit.value) {
      await updateConfig(configForm)
      ElMessage.success('更新成功')
    } else {
      await saveConfig(configForm)
      ElMessage.success('添加成功')
    }
    configDialogVisible.value = false
    loadConfigData()
  } catch (error) {
    console.error(error)
  } finally {
    configSubmitLoading.value = false
  }
}

const handleDeleteConfig = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除配置"${row.configKey}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteConfig(row.id)
    ElMessage.success('删除成功')
    loadConfigData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

// ========== 系统公告相关 ==========
const noticeLoading = ref(false)
const noticeSubmitLoading = ref(false)
const noticeTable = ref([])
const noticePage = ref(1)
const noticeSize = ref(10)
const noticeTotal = ref(0)

const noticeSearchForm = reactive({
  status: null,
  keyword: ''
})

const noticeDialogVisible = ref(false)
const noticeDialogTitle = ref('')
const noticeFormRef = ref(null)
const noticeIsEdit = ref(false)

const noticeForm = reactive({
  id: null,
  title: '',
  content: '',
  type: 1,
  status: 1,
  startTime: '',
  endTime: ''
})

const noticeRules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

const loadNoticeData = async () => {
  noticeLoading.value = true
  try {
    const res = await getNoticePage({
      page: noticePage.value,
      size: noticeSize.value,
      status: noticeSearchForm.status,
      keyword: noticeSearchForm.keyword
    })
    noticeTable.value = res.records
    noticeTotal.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    noticeLoading.value = false
  }
}

const handleNoticeSearch = () => {
  noticePage.value = 1
  loadNoticeData()
}

const resetNoticeSearch = () => {
  noticeSearchForm.status = null
  noticeSearchForm.keyword = ''
  handleNoticeSearch()
}

const handleAddNotice = () => {
  noticeIsEdit.value = false
  noticeDialogTitle.value = '新增公告'
  noticeForm.id = null
  noticeForm.title = ''
  noticeForm.content = ''
  noticeForm.type = 1
  noticeForm.status = 1
  noticeForm.startTime = ''
  noticeForm.endTime = ''
  noticeDialogVisible.value = true
}

const handleEditNotice = (row) => {
  noticeIsEdit.value = true
  noticeDialogTitle.value = '编辑公告'
  noticeForm.id = row.id
  noticeForm.title = row.title
  noticeForm.content = row.content
  noticeForm.type = row.type
  noticeForm.status = row.status
  noticeForm.startTime = row.startTime
  noticeForm.endTime = row.endTime
  noticeDialogVisible.value = true
}

const handleNoticeSubmit = async () => {
  const valid = await noticeFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  // 验证时间
  if (noticeForm.startTime && noticeForm.endTime && noticeForm.startTime >= noticeForm.endTime) {
    ElMessage.warning('结束时间必须晚于开始时间')
    return
  }
  
  noticeSubmitLoading.value = true
  try {
    if (noticeIsEdit.value) {
      await updateNotice(noticeForm)
      ElMessage.success('更新成功')
    } else {
      await saveNotice(noticeForm)
      ElMessage.success('添加成功')
    }
    noticeDialogVisible.value = false
    loadNoticeData()
  } catch (error) {
    console.error(error)
  } finally {
    noticeSubmitLoading.value = false
  }
}

const handleDeleteNotice = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除公告"${row.title}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteNotice(row.id)
    ElMessage.success('删除成功')
    loadNoticeData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadConfigData()
  loadNoticeData()
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>