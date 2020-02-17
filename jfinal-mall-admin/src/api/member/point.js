/* jshint esversion: 6 */
import service from '@/util/config'

export function page(params) {
    return service({
        url: '/point/page',
        method: 'post',
        params
    })
}
