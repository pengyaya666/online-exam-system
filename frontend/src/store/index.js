import { createStore } from 'vuex'

export default createStore({
  state: {
    token: localStorage.getItem('token') || '',
    refreshToken: localStorage.getItem('refreshToken') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
  },
  getters: {
    isLoggedIn: state => !!state.token,
    userRole: state => state.userInfo?.role,
    username: state => state.userInfo?.username,
    realName: state => state.userInfo?.realName
  },
  mutations: {
    SET_TOKEN(state, { token, refreshToken }) {
      state.token = token
      state.refreshToken = refreshToken
      localStorage.setItem('token', token)
      localStorage.setItem('refreshToken', refreshToken)
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    CLEAR_AUTH(state) {
      state.token = ''
      state.refreshToken = ''
      state.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userInfo')
    }
  },
  actions: {
    login({ commit }, data) {
      commit('SET_TOKEN', { token: data.token, refreshToken: data.refreshToken })
      commit('SET_USER_INFO', data.userInfo)
    },
    logout({ commit }) {
      commit('CLEAR_AUTH')
    },
    updateUserInfo({ commit }, userInfo) {
      commit('SET_USER_INFO', userInfo)
    }
  }
})
