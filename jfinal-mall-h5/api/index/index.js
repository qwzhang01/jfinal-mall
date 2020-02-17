/* jshint esversion: 6 */
import host from '../../config/domainConfig'
import axios from '../../http/config'
import qs from 'qs'
import { flashList } from '../goods/index'

/**
 * 所有分类
 */
export const Navs = params => {
  return axios.post(`${host.domainName}/home/nav`, qs.stringify(params)).then(res => res.data)
}

/**
 * 秒杀商品列表
 */
export const flashGoodsList = params => {
  return axios.post(`${host.domainName}/prom/flash/homeGood`, qs.stringify(params)).then(res => res.data)
}

/**
 * 秒杀时间
 */
export const flashTimeList = flashList

/**
 * 首页头部（banner、背景色）
 */
export const Heads = params => {
  return axios.post(`${host.domainName}/home/head`, qs.stringify(params)).then(res => res.data)
}

/**
 * 首页金刚区
 */
export const Diamond = params => {
  return axios.post(`${host.domainName}/home/diamond`, qs.stringify(params)).then(res => res.data)
}

/**
 * 首页资讯
 */
export const Info = params => {
  return axios.post(`${host.domainName}/home/info`, qs.stringify(params)).then(res => res.data)
}

/**
 * 首页广告
 */
export const Advertise = params => {
  return axios.post(`${host.domainName}/home/advertise`, qs.stringify(params)).then(res => res.data)
}

/**
 * 首页专场
 */
export const SpecialShow = params => {
  return axios.post(`${host.domainName}/home/specialShow`, qs.stringify(params)).then(res => res.data)
}
