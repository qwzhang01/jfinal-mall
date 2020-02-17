/* jshint esversion: 6 */
import service from '@/util/config'
import qs from 'qs'

// 用户统计
export function userInfo(params, data = null) {
    return service({
        url: '/home/userInfo',
        method: 'post',
        params,
        data
    })
}
// 订单统计
export function orderInfo(params, data = null) {
    return service({
        url: '/home/orderInfo',
        method: 'post',
        params,
        data
    })
}
// 订单统计折线图
export function orderStatic(params, data = null) {
    return service({
        url: '/home/orderStatic',
        method: 'post',
        params,
        data
    })
}
