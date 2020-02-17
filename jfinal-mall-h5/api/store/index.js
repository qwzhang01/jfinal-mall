/* jshint esversion: 6 */
import host from '../../config/domainConfig'
import axios from '../../http/config'
import qs from 'qs'
import {
  searchForms,
  UserDetail,
  sms
} from '../index'
import {
  parentTree
} from '../goods/'

/**
 * 店铺首页详情
 * joker
 */
export const storeDetail = params => {
  return axios.post(`${host.domainName}/store/detail`, qs.stringify(params)).then(res => res.data)
}

/**
 * 收藏店铺
 * joker
 */
export const collect = params => {
  return axios.post(`${host.domainName}/store/collect`, qs.stringify(params)).then(res => res.data)
}
/**
 * 取消收藏店铺
 * joker
 */
export const cancelCollect = params => {
  return axios.post(`${host.domainName}/store/cancelCollect`, qs.stringify(params)).then(res => res.data)
}

/**
 * 店铺商品分页数据
 * joker
 */
export const goodList = params => {
  return axios.post(`${host.domainName}/store/good`, qs.stringify(params)).then(res => res.data)
}

/**
 * 自定义查询条件
 * joker
 */
export const searchForm = searchForms

/**
 * 推荐店铺
 * joker
 */
export const Recommend = params => {
  return axios.post(`${host.domainName}/store/recommend`, qs.stringify(params)).then(res => res.data)
}

/**
 * ==============
 * 我要开店
 * ==============
 */

/**
 * 开店申请验证
 * joker
 */
export const applyVerify = params => {
  return axios.post(`${host.domainName}/store/applyVerify`, qs.stringify(params)).then(res => res.data)
}

// 获取用户信息
export const userInfo = UserDetail

// 短信
export const Sms = sms

/**
 * 开店申请
 * joker
 */
export const apply = params => {
  return axios.post(`${host.domainName}/store/apply`, qs.stringify(params)).then(res => res.data)
}

/**
 * 审核失败原因
 * joker
 */
export const applyFailReason = params => {
  return axios.post(`${host.domainName}/store/applyFailReason`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取所有经营类目
 * joker
 */
export const parentTreeFun = parentTree
