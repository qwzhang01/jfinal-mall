/* jshint esversion: 6 */
import service from '@/util/config'

// 获取后台所有参数列表
export function pageList(params) {
    return service({
        url: '/param/pageList',
        method: 'get',
        params
    })
}
// 获取list参数
export function dict(type) {
    return service({
        url: '/param/dict',
        method: 'get',
        params: {
            type
        }
    })
}
// 获取key-value参数
export function basic(key) {
    return service({
        url: '/param/basic',
        method: 'get',
        params: {
            key
        }
    })
}
// 获取快递公司
export function expressCompany() {
    return service({
        url: '/param/expressCompany',
        method: 'post'
    })
}