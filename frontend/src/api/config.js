import request from '../utils/request'

// 系统配置相关
export const getConfigPage = (params) => {
  return request({
    url: '/config/page',
    method: 'get',
    params
  })
}

export const getConfigById = (id) => {
  return request({
    url: `/config/${id}`,
    method: 'get'
  })
}

export const getAllConfig = () => {
  return request({
    url: '/config/all',
    method: 'get'
  })
}

export const saveConfig = (data) => {
  return request({
    url: '/config',
    method: 'post',
    data
  })
}

export const updateConfig = (data) => {
  return request({
    url: '/config',
    method: 'put',
    data
  })
}

export const deleteConfig = (id) => {
  return request({
    url: `/config/${id}`,
    method: 'delete'
  })
}

// 系统公告相关
export const getNoticePage = (params) => {
  return request({
    url: '/notice/page',
    method: 'get',
    params
  })
}

export const getNoticeById = (id) => {
  return request({
    url: `/notice/${id}`,
    method: 'get'
  })
}

export const getCurrentNotice = () => {
  return request({
    url: '/notice/current',
    method: 'get'
  })
}

export const saveNotice = (data) => {
  return request({
    url: '/notice',
    method: 'post',
    data
  })
}

export const updateNotice = (data) => {
  return request({
    url: '/notice',
    method: 'put',
    data
  })
}

export const deleteNotice = (id) => {
  return request({
    url: `/notice/${id}`,
    method: 'delete'
  })
}