import request from '../utils/request'

// 获取错题列表
export const getWrongQuestionPage = (params) => {
  return request({
    url: '/wrong/page',
    method: 'get',
    params
  })
}

// 获取错题数量
export const getWrongCount = () => {
  return request({
    url: '/wrong/count',
    method: 'get'
  })
}

// 删除单道错题
export const deleteWrongQuestion = (id) => {
  return request({
    url: `/wrong/${id}`,
    method: 'delete'
  })
}

// 清空所有错题
export const clearWrongQuestions = () => {
  return request({
    url: '/wrong/clear',
    method: 'delete'
  })
}