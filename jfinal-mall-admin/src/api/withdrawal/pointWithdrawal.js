/* jshint esversion: 6 */
import service from '@/util/config'
import util from "@/util/util";

export function authList(params) {
    return service({
        url: '/point/withdrawal/page',
        method: 'post',
        params
    })
}

// 导出
export function downloadAuth(params) {
    util.download('/point/withdrawal/download', '提现打款汇总统计表', params)
}
export function auth(params) {
    return service({
        url: '/point/withdrawal/author',
        method: 'post',
        params
    })
}
export function pay(params) {
    return service({
        url: '/point/withdrawal/pay',
        method: 'post',
        params
    })
}
export function payHand(params) {
    return service({
        url: '/point/withdrawal/payHand',
        method: 'post',
        params
    })
}
