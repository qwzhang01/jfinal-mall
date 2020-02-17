/* jshint esversion: 6 */
import host from '../../config/domainConfig'
import axios from '../../http/config'

/**
 * 获取积分汇总信息
 * yl
 */
export const getStatis = () => {
  return axios.get(`${host.domainName}/point/sum`).then(res => res.data)
}
/**
 * 获取下线汇总
 */
export const getMyCustomerSum = () => {
  return axios.get(`${host.domainName}/user/sublineSum`).then(res => res.data)
}

/**
 * 获取我的用户列表
 */
export const getMyCustomers = params => {
  return axios.get(`${host.domainName}/user/sublinePage`, { params: params }).then(res => res.data)
}

/**
 * 获取我的投资
 */
export const getMyInvest = params => {
  return axios.get(`${host.domainName}/point/mineInvestPage`, { params: params }).then(res => res.data)
}

/**
 * 获取推广业绩
 */
export const getMySubInvest = params => {
  return axios.get(`${host.domainName}/point/subInvestPage`, { params: params }).then(res => res.data)
}
/**
 * 获取收益明细
 */
export const getMyEarnings = params => {
  return axios.get(`${host.domainName}/point/incomeDetail`, { params: params }).then(res => res.data)
}

/**
 * 获取提现记录
 */
export const getMyWithdraw = params => {
  return axios.get(`${host.domainName}/point/withdrawDetail`, { params: params }).then(res => res.data)
}
// 申请提现
export const withdrawApply = params => {
  return axios.get(`${host.domainName}/point/withdraw`, { params: params }).then(res => res.data)
}
