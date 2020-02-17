/* jshint esversion: 6 */
import service from '@/util/config'

//获取分页数据
export function pageList(params) {
    return service({
        url: '/app/set/page',
        method: 'post',
        params
    })
}
// 查询跳转页面下拉选项数据
export function findGoto(gotoType, keyword) {
    return service({
        url: '/app/set/findGoto',
        method: 'post',
        params: { gotoType, keyword }
    })
}

// APP设置表单提交
export function save(params) {
    return service({
        url: '/app/set/save',
        method: 'post',
        params
    })
}
// APP设置修改显示状态
export function showSave(params) {
    return service({
        url: '/app/set/showSave',
        method: 'post',
        params
    })
}
//删除
export function del(id) {
    return service({
        url: '/app/set/delete',
        method: 'post',
        params: { id }
    })
}
// 获取专题对应的商品
export function goodList(params) {
    return service({
        url: '/app/set/goodPage',
        method: 'post',
        params
    })
}
// 商品模糊查询
export function goodSearch(params) {
    return service({
        url: '/good/search',
        method: 'post',
        params
    })
}
// 保存专题添加商品
export function saveGood(params) {
    return service({
        url: '/app/set/save',
        method: 'post',
        params
    })
}
