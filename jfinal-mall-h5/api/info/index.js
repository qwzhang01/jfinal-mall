/* jshint esversion: 6 */
import host from '../../config/domainConfig'
import axios from '../../http/config'
import qs from 'qs'

/**
 * 获取资讯列表
 */
export const infoList = params => {
  return axios.post(`${host.domainName}/article/list`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取资讯详情
 */
export const infoDetail = params => {
  return axios.post(`${host.domainName}/article/detail`, qs.stringify(params)).then(res => res.data)
}
