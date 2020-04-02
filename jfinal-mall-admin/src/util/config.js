/* jshint esversion: 6 */
import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import { getToken, getUserId, logOut } from '@/util/cookie'

const service = axios.create({
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
  },
  baseURL: process.env.BASE_API + "/web",
})

service.interceptors.request.use(config => {
  config.headers['userId'] = getUserId() ? getUserId() : ''
  config.headers['token'] = getToken() ? getToken() : ''
  return config
}, error => {
  return Promise.reject(error)
})

service.interceptors.response.use(response => {
  const res = response.data
  if (res.status === 0) {
    return res
  }
  // 未登录
  if (res.status === -101) {
    logOut()
    MessageBox.alert('您的登录已过期，请重新登录', '登录过期', {
      confirmButtonText: '重新登录',
      type: 'warning'
    }).then(() => {
      location.reload()
      window.location.href = '/'
    })
  } else if (res.status === 501) {
    // 没有权限
    Message({
      message: res.msg ? res.msg : '系统异常',
      type: 'error',
      duration: 3 * 1000
    })
    return res
  } else {
    Message({
      message: res.msg ? res.msg : '系统异常',
      type: 'error',
      duration: 3 * 1000
    })
    return res
  }
}, error => {
  Message({
    message: error,
    type: 'error',
    duration: 3 * 1000
  })
  return Promise.reject(error)
})

export default service