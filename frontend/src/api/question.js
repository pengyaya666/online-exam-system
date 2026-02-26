import request from '../utils/request'

export const getQuestionPage = (params) => {
  return request({
    url: '/question/page',
    method: 'get',
    params
  })
}

export const getQuestionList = (params) => {
  return request({
    url: '/question/list',
    method: 'get',
    params
  })
}

export const getQuestionById = (id) => {
  return request({
    url: `/question/${id}`,
    method: 'get'
  })
}

export const addQuestion = (data) => {
  return request({
    url: '/question',
    method: 'post',
    data
  })
}

export const updateQuestion = (data) => {
  return request({
    url: '/question',
    method: 'put',
    data
  })
}

export const deleteQuestion = (id) => {
  return request({
    url: `/question/${id}`,
    method: 'delete'
  })
}

export const importQuestions = (bankId, file) => {
  const formData = new FormData()
  formData.append('bankId', bankId)
  formData.append('file', file)
  
  return request({
    url: '/question/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const downloadTemplate = () => {
  return request({
    url: '/question/template',
    method: 'get',
    responseType: 'blob'
  })
}
