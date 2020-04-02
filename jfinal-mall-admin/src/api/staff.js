/* jshint esversion: 6 */
import service from '@/util/config'
import qs from 'qs'

// 分页数据
export function pageList(params) {
    return service({
        url: '/staff/pageList',
        method: 'get',
        params
    })
}
// 主动修改密码
export function changePw(params) {
    return service({
        url: '/staff/changePw',
        method: 'post',
        params
    })
}
// 系统管理员重置密码
export function resetPw(userId) {
    return service({
        url: '/staff/resetPw',
        method: 'post',
        params: {
            userId
        }
    })
}

// 保存用户信息
export function staffForm(params) {
    return service({
        url: '/staff/form',
        method: 'post',
        data: qs.stringify(params, { indices: false })
    })
}

// 获取用户详情
export function staffDetail(id) {
    return service({
        url: '/staff/get',
        method: 'post',
        params: { id: id }
    })
}

// 删除用户
export function staffDelete(id) {
    return service({
        url: '/staff/delete',
        method: 'post',
        params: { id: id }
    })
}

// 获取用户权限、路由信息
export function staffOperator() {
    return service({
        url: '/staff/detail',
        method: 'post'
    })
}