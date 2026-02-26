<template>
  <router-view />
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f5f7fa;
}

#app {
  min-height: 100vh;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

/* 通用样式 */
.page-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 4px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 考试相关样式 */
.exam-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.question-card {
  margin-bottom: 20px;
}

.question-title {
  font-size: 16px;
  line-height: 1.8;
  margin-bottom: 15px;
}

.question-index {
  color: #409eff;
  font-weight: bold;
  margin-right: 8px;
}

.question-score {
  color: #f56c6c;
  font-size: 14px;
  margin-left: 10px;
}

.option-item {
  display: block;
  margin: 10px 0;
  padding: 10px 15px;
  border-radius: 4px;
  transition: all 0.3s;
}

.option-item:hover {
  background-color: #f5f7fa;
}

/* 答题卡样式 */
.answer-sheet {
  position: fixed;
  right: 20px;
  top: 80px;
  width: 200px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 15px;
}

.answer-sheet-title {
  font-weight: bold;
  margin-bottom: 15px;
  text-align: center;
}

.answer-sheet-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
}

.answer-sheet-item {
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
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

/* 统计卡片 */
.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin: 10px 0;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

/* 结果页面 */
.result-header {
  text-align: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 8px;
  margin-bottom: 30px;
}

.result-score {
  font-size: 72px;
  font-weight: bold;
  margin: 20px 0;
}

.result-status {
  font-size: 24px;
  padding: 10px 30px;
  border-radius: 20px;
  display: inline-block;
}

.result-status.pass {
  background-color: #67c23a;
}

.result-status.fail {
  background-color: #f56c6c;
}
</style>
