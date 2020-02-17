import host from '../../config/domainConfig'
import axios from '../../http/config'
import qs from 'qs'

/**
 * 获取店铺收藏夹列表信息
 * joker
 */
export const storecollectPage = params => {
  return axios.post(`${host.domainName}/store/collectPage`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取商品收藏夹列表信息
 * joker
 */
export const goodscollectPage = params => {
  return axios.post(`${host.domainName}/good/collectPage`, qs.stringify(params)).then(res => res.data)
}

/**
 * 批量取消收藏店铺
 * joker
 */
export const storecancelCollectBatch = params => {
  return axios.post(`${host.domainName}/store/cancelCollectBatch`, qs.stringify(params)).then(res => res.data)
}

/**
 * 批量取消收藏商品
 * joker
 */
export const goodscancelCollectBatch = params => {
  return axios.post(`${host.domainName}/good/cancelCollectBatch`, qs.stringify(params)).then(res => res.data)
}
