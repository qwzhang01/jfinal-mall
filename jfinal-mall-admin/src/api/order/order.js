/* jshint esversion: 6 */
import service from '@/util/config'
import util from "@/util/util";
// 订单分页
export function orderList(params, data = null) {
    return service({
        url: '/order/pageList',
        method: 'post',
        params,
        data
    })
}
// 订单导出
export function download(params) {
    util.download('/order/download', '订单汇总信息表', params)
}
export function downloadShippping(params) {
    util.download('/order/downloadShippping', '发货订单统计表', params)
}
// 未发货订单退款列表
export function refundPageList(params, data = null) {
    return service({
        url: '/order/refundPageList',
        method: 'post',
        params,
        data
    })
}
// 同意退款
export function refund(orderId) {
    return service({
        url: '/order/refund',
        method: 'post',
        params: { orderId }
    })
}
// 同意退款
export function refundLanuchTest(orderId) {
    return service({
        url: '/order/refundLanuchTest',
        method: 'post',
        params: { orderId }
    })
}
// 已发货订单退款 退货列表
export function returnPageList(params, data = null) {
    return service({
        url: '/order/returnPageList',
        method: 'post',
        params,
        data
    })
}
export function payOffLine(orderId, data = null) {
    return service({
        url: '/order/payOffLine',
        method: 'post',
        params: { orderId },
        data
    })
}
// 拼团抽奖订单分页
export function lotteryPageList(params, data = null) {
    return service({
        url: '/order/lotteryPageList',
        method: 'post',
        params,
        data
    })
}
// 保存发货信息
export function saveDeliver(params) {
    return service({
        url: '/order/saveDeliver',
        method: 'post',
        params
    })
}