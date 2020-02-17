/* jshint esversion: 6 */
import service from '@/util/config'
export function pageList(params) {
    return service({
        url: '/client/page',
        method: 'post',
        params
    })
}
export function auth(params) {
    return service({
        url: '/client/auth',
        method: 'post',
        params
    })
}