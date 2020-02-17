/* jshint esversion: 6 */
import service from '@/util/config'

// 店铺信息分页列表
export function storeList(params) {
    return service({
        url: '/store/pageList',
        method: 'post',
        params
    })
}
// 设置店铺抽成比例
export function setButlerRate(params) {
    return service({
        url: '/store/setButlerRate',
        method: 'post',
        params
    })
}
// 审核店铺信息分页列表
export function pageAuth(params) {
    return service({
        url: '/store/pageAuth',
        method: 'post',
        params
    })
}
// 审核店铺
export function authStore(storeId) {
    return service({
        url: '/store/auth',
        method: 'post',
        params: { storeId }
    })
}
// 关闭店铺
export function closeStore(storeId) {
    return service({
        url: '/store/close',
        method: 'post',
        params: { storeId }
    })
}
// 店铺下拉数据
export function select(params) {
    return service({
        url: '/store/select',
        method: 'post',
        params
    })
}