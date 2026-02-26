import request from '../utils/request'

export const getBankPage = (params) => {
  return request({
    url: '/bank/page',
    method: 'get',
    params
  })
}

export const getBankList = (params) => {
  return request({
    url: '/bank/list',
    method: 'get',
    params
  })
}

export const getBankById = (id) => {
  return request({
    url: `/bank/${id}`,
    method: 'get'
  })
}

export const createBank = (data) => {
  return request({
    url: '/bank',
    method: 'post',
    data
  })
}

export const updateBank = (data) => {
  return request({
    url: '/bank',
    method: 'put',
    data
  })
}

export const deleteBank = (id) => {
  return request({
    url: `/bank/${id}`,
    method: 'delete'
  })
}
