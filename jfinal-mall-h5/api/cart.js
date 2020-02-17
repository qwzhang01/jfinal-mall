/* jshint esversion: 6 */
import host from '../config/domainConfig'
import axios from '../http/config'
import qs from 'qs'

/**
 * 加入购物车
 */
export const addGoodsToCart = params => {
  return axios.post(`${host.domainName}/cart/addToCart`, qs.stringify(params)).then(res => res.data)
}

/**
 *  商品详情
 */
export const goodsDetails = params => {
  return axios.post(`${host.domainName}/good/detail`, qs.stringify(params)).then(res => res.data)
}

/**
 * 购列表列表
 */
export const cartList = params => {
  return axios.post(`${host.domainName}/cart/cartList`, qs.stringify(params)).then(res => res.data)
}

/**
 * 删除购物车的商品
 */
export const deleteCart = params => {
  return axios.post(`${host.domainName}/cart/deleteCart`, params, {
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  }).then(res => res.data)
}

/**
 * 修改购物车商品数量
 */
export const updateCartNum = params => {
  return axios.post(`${host.domainName}/cart/updateCartNum`, qs.stringify(params)).then(res => res.data)
}

/**
 * 购物车-去结算
 */
export const confirmOrder = params => {
  return axios.post(`${host.domainName}/order/preCartOrder`, params, {
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  }).then(res => res.data)
}

/**
 * 移除收藏夹
 */
export const moveToCollect = params => {
  return axios.post(`${host.domainName}/cart/moveToCollect`, params, {
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  }).then(res => res.data)
}

/**
 * 满额送锁
 */
export const hasPromLock = params => {
  return axios.post(`${host.domainName}/prom/order/getPropOrder`, qs.stringify(params)).then(res => res.data)
}
