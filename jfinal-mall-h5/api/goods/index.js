/* jshint esversion: 6 */
import host from '../../config/domainConfig'
import axios from '../../http/config'
import qs from 'qs'
import {
  addGoodsToCart,
  hasPromLock
} from '../cart'
/**
 * 获取商品详情
 * joker
 */
export const goodsDetail = params => {
  return axios.post(`${host.domainName}/good/detail`, qs.stringify(params)).then(res => res.data)
}

/**
 * 收藏商品
 * joker
 */
export const Collect = params => {
  return axios.post(`${host.domainName}/good/collect`, qs.stringify(params)).then(res => res.data)
}
/**
 * 取消收藏商品
 * joker
 */
export const cancelCollect = params => {
  return axios.post(`${host.domainName}/good/cancelCollect`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取商品评价
 * joker
 */
export const commentPage = params => {
  return axios.post(`${host.domainName}/good/commentPage`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取服务说明
 */
export const serviceInfo = params => {
  return axios.post(`${host.domainName}/good/serviceInfo`, qs.stringify(params)).then(res => res.data)
}

/**
 * 凑单商品列表
 */
export const combineOrderGood = params => {
  return axios.post(`${host.domainName}/good/combineOrderGood`, qs.stringify(params)).then(res => res.data)
}

/**
 * 加入购物车
 */
export const addCart = addGoodsToCart
/**
 * 获取秒杀时间
 */
export const flashList = params => {
  return axios.post(`${host.domainName}/prom/flash/list`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取秒杀商品列表
 */
export const goodPage = params => {
  return axios.post(`${host.domainName}/prom/flash/goodPage`, qs.stringify(params)).then(res => res.data)
}

/**
 * 去凑单
 */
export const combineOrderGoods = params => {
  return axios.post(`${host.domainName}/good/combineOrderGood`, qs.stringify(params)).then(res => res.data)
}
/**
 *满额送锁
 */
export const hasPromLockFun = hasPromLock

/**
 * ==========================
 * 秒杀
 * ==========================
 */

/**
 * 获取秒杀时间
 * joker
 */
export const flashListTime = params => {
  return axios.post(`${host.domainName}/prom/flash/list`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取秒杀商品列表
 * joker
 */
export const flashGoodPage = params => {
  return axios.post(`${host.domainName}/prom/flash/goodPage`, qs.stringify(params)).then(res => res.data)
}

/**
 * ==========================
 * 一元抢购
 * ==========================
 */

/**
 * 获取活动banner&活动时间列表
 * joker
 */
export const lotteryTimeList = params => {
  return axios.post(`${host.domainName}/prom/lottery/list`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取活动日期列表
 */
export const lotteryDateList = () => {
  return axios.post(`${host.domainName}/prom/lottery/selectDate`).then(res => res.data)
}

/**
 * 获取活动商品列表
 */
export const lotteryList = params => {
  return axios.post(`${host.domainName}/prom/lottery/goodList`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取活动参与人数
 */
export const attendLotteryList = params => {
  return axios.post(`${host.domainName}/prom/lottery/attendLotteryList`, qs.stringify(params)).then(res => res.data)
}
/**
 * 获取用户抽奖活动列表
 * joker
 */
export const userLotteryList = params => {
  return axios.post(`${host.domainName}/prom/lottery/userLotteryList`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取中奖名单
 * joker
 */
export const whoWin = params => {
  return axios.post(`${host.domainName}/prom/lottery/whoWin`, qs.stringify(params)).then(res => res.data)
}

/**
 * 抽奖详情
 * joker
 */
export const lotteryDetail = params => {
  return axios.post(`${host.domainName}/prom/lottery/lotteryDetail`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取预约时间下拉数据
 * joker
 */
export const appointTimeParam = params => {
  return axios.post(`${host.domainName}/door/server/appointTimeParam`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取所有经营类目
 * joker
 */
export const parentTree = params => {
  return axios.post(`${host.domainName}/good/parentTree`, qs.stringify(params)).then(res => res.data)
}
