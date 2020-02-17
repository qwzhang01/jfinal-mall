/* jshint esversion: 6 */
import qs from 'qs'
import service from '@/util/config'
//获取分页数据
export function pageList(params) {
    return service({
        url: '/role/pageList',
        method: 'get',
        params
    })
}
// 获取权限树
export function operatorTree(params) {
    return service({
        url: '/role/operatorTree',
        method: 'get',
        params
    })
}
export function userRole(params) {
    return service({
        url: '/role/userRole',
        method: 'get',
        params
    })
}
//角色设置权限
export function editPermission(params) {
    return service({
        url: '/role/savePermission',
        method: 'post',
        data: qs.stringify(params, { indices: false })
    })
}
// 获取角色详情
export function get(id) {
    return service({
        url: '/role/getById',
        method: 'get',
        params: { id }
    })
}
// 角色表单提交
export function save(params) {
    return service({
        url: '/role/save',
        method: 'post',
        params
    })
}
// 删除角色
export function del(id) {
    return service({
        url: '/role/delete',
        method: 'post',
        params: { id }
    })
}