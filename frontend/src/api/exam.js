import request from '../utils/request'

export const getExamPage = (params) => {
  return request({
    url: '/exam/page',
    method: 'get',
    params
  })
}

export const getActiveExams = () => {
  return request({
    url: '/exam/active',
    method: 'get'
  })
}

export const getExamById = (id) => {
  return request({
    url: `/exam/${id}`,
    method: 'get'
  })
}

export const createExam = (data) => {
  return request({
    url: '/exam',
    method: 'post',
    data
  })
}

export const updateExam = (data) => {
  return request({
    url: '/exam',
    method: 'put',
    data
  })
}

export const deleteExam = (id) => {
  return request({
    url: `/exam/${id}`,
    method: 'delete'
  })
}

export const publishExam = (id) => {
  return request({
    url: `/exam/${id}/publish`,
    method: 'put'
  })
}

export const startExam = (id) => {
  return request({
    url: `/exam/${id}/start`,
    method: 'post'
  })
}

export const submitExam = (data) => {
  return request({
    url: '/exam/submit',
    method: 'post',
    data
  })
}

export const getExamResult = (recordId) => {
  return request({
    url: `/exam/record/${recordId}`,
    method: 'get'
  })
}

export const getExamRecords = (params) => {
  return request({
    url: '/exam/records',
    method: 'get',
    params
  })
}

export const getScoreStatistics = (id) => {
  return request({
    url: `/exam/${id}/statistics`,
    method: 'get'
  })
}
