<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的成绩</span>
        </div>
      </template>
      
      <el-table :data="records" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="examTitle" label="考试名称" min-width="200" />
        <el-table-column prop="startTime" label="考试时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="useTime" label="用时" width="100">
          <template #default="{ row }">
            {{ formatDuration(row.useTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="score" label="得分" width="100">
          <template #default="{ row }">
            <span :class="row.isPass ? 'pass' : 'fail'">{{ row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column prop="passScore" label="及格分" width="80" />
        <el-table-column prop="isPass" label="是否通过" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isPass ? 'success' : 'danger'">
              {{ row.isPass ? '通过' : '未通过' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row.id)">
              查看详情
            </el-button>
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
      
      <el-empty v-if="records.length === 0 && !loading" description="暂无考试记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getExamRecords } from '@/api/exam'

const router = useRouter()

const records = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

const formatDuration = (seconds) => {
  if (!seconds) return '-'
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  if (minutes === 0) return `${secs}秒`
  return `${minutes}分${secs}秒`
}

const viewDetail = (recordId) => {
  router.push(`/student/result/${recordId}`)
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getExamRecords({
      page: page.value,
      size: size.value
    })
    records.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.pass {
  color: #67c23a;
  font-weight: bold;
}

.fail {
  color: #f56c6c;
  font-weight: bold;
}
</style>
