/* jshint esversion: 6 */
import host from '../config/domainConfig'
import axios from '../http/config'
import qs from 'qs'

/**
 * 后台生成订单信息，但是还没有保存，客户做二次确认后再保存
 * @param {flashGoodId 秒杀商品活动ID} params
 */
export const PreOrder = params => {
  return axios.post(`${host.domainName}/order/preOrder`, qs.stringify(params)).then(res => res.data)
}
/**
 * 订单提交，保存订单至mysql或redis ，返回用以发起支付的相关信息
 * @param {*} params
 */
export const SubOrder = params => {
  return axios.post(`${host.domainName}/order/subOrder`, params, {
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  }).then(res => res.data)
}
/**
 * 秒杀-校验是否有购买资格
 * @param {flashGoodId 秒杀商品活动ID} params
 */
export const PreFlashOrderValid = params => {
  return axios.post(`${host.domainName}/order/preFlashOrderValid`, qs.stringify(params)).then(res => res.data)
}
/**
 * 后台生成订单信息，但是还没有保存，客户做二次确认后再保存
 * @param {flashGoodId 秒杀商品活动ID} params
 */
export const PreFlashOrder = params => {
  return axios.post(`${host.domainName}/order/preFlashOrder`, qs.stringify(params)).then(res => res.data)
}
/**
 * 订单提交，保存订单至mysql或redis ，返回用以发起支付的相关信息
 * @param {*} params
 */
export const SubFlashOrder = params => {
  return axios.post(`${host.domainName}/order/subFlashOrder`, qs.stringify(params)).then(res => res.data)
}

export const PreLotteryOrder = params => {
  return axios.post(`${host.domainName}/order/preLotteryOrder`, qs.stringify(params)).then(res => res.data)
}
export const SubLotteryOrder = params => {
  return axios.post(`${host.domainName}/order/subLotteryOrder`, qs.stringify(params)).then(res => res.data)
}
