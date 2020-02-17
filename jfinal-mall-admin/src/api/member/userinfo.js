/* jshint esversion: 6 */
import service from '@/util/config'
import util from "@/util/util";

export function userList(params) {
    return service({
        url: '/member/page',
        method: 'post',
        params
    })
}
// 会员信息导出
export function download(params) {
    util.download('/member/exportExcel', '会员信息', params)
}
// 线下积分
export function savePoint(params) {
    return service({
        url: '/member/savePoint',
        method: 'post',
        params
    })
}

export function auth(userId) {
    return service({
        url: '/member/auth',
        method: 'post',
        params: { userId }
    })
}
export function authSpec(userId) {
    return service({
        url: '/member/authSpec',
        method: 'post',
        params: { userId }
    })
}
