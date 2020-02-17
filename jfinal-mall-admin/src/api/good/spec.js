/* jshint esversion: 6 */
import service from '@/util/config'

//规格分页数据
export function specList(params, data = null) {
    return service({
        url: '/spec/page',
        method: 'post',
        params,
        data
    })
}
//规格选项数据
export function select(categoryId, data = null) {
    return service({
        url: '/spec/select',
        method: 'post',
        params: { categoryId },
        data
    })
}
// 获取详情
export function detailSpec(id) {
    return service({
        url: '/spec/detail',
        method: 'post',
        params: { id }
    })
}
// 保存规格
export function saveSpec(params) {
    return service({
        url: '/spec/save',
        method: 'post',
        params
    })
}
// 删除规格
export function deleteSpec(id) {
    return service({
        url: '/spec/delete',
        method: 'post',
        params: { id }
    })
}
// 保存规格项
export function saveSpecItem(params) {
    return service({
        url: '/spec/saveItem',
        method: 'post',
        params
    })
}
// 删除规格项
export function deleteSpecItem(id) {
    return service({
        url: '/spec/deleteItem',
        method: 'post',
        params: { id }
    })
}