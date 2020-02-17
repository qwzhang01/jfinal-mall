/* jshint esversion: 6 */
import service from '@/util/config'
// 获取分页数据
export function doorServiceList(params){
    return service({
        url: '/door/server/pageList',
        method: 'post',
        params,
    })
}
// 分配服务员
export function assignWork(params){
    return service({
        url: '/door/server/assignWork',
        method: 'post',
        params
    })
}
// 取消分配
export function cancelAssign(params){
    return service({
        url: '/door/server/cancelAssign',
        method: 'post',
        params
    })
}
// 完成服务
export function doneDoorService(params){
    return service({
        url: '/door/server/done',
        method: 'post',
        params
    })
}