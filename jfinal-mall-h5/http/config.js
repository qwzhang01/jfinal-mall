/* jshint esversion: 6 */
import axios from 'axios'
// 域名配置
import host from '../config/domainConfig'
import { Toast } from 'vue-ydui/dist/lib.rem/dialog'
const { Dialog } = require('vant')
// 全局的 axios 默认值
axios.defaults.baseURL = host
const instance = axios.create({
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
  },
  timeout: 10000
})

// 添加请求拦截器
instance.interceptors.request.use(config => {
  // console.log(config.params)
  // 在发送请求之前做某事
  config.headers['token'] = sessionStorage.getItem('token') ? sessionStorage.getItem('token') : ''
  config.headers['userId'] = sessionStorage.getItem('userId') ? sessionStorage.getItem('userId') : ''
  return config
}, error => {
  // 请求错误时做些事
  return Promise.reject(error)
})

// 添加响应拦截器
instance.interceptors.response.use(response => {
  // 如果接口返回未登录状态，清除sessionStorage中的userId token
  if (response.data.status === -101) {
    sessionStorage.clear()
    Dialog.confirm({
      title: '温馨提示',
      message: '您的登录过期，请重新登录？'
    }).then(res => {
      sessionStorage.setItem('recordUrl', '/')
      window.location.href = '/#/login/'
    })
    return response
  }
  if (response.data.status !== 0) {
    Toast({ mes: response.data.msg, timeout: 1500, icon: 'error' })
  }
  return response
}, error => {
  return Promise.reject(error.response.data) // 返回接口返回的错误信息
})

export default instance
