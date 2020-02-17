/* jshint esversion: 6 */
import service from '@/util/config'
export function pageList(params) {
    return service({
        url: '/balance/page',
        method: 'post',
        params
    })
}