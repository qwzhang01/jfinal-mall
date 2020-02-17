/* jshint esversion: 6 */
import service from '@/util/config'

//商品分类树形结构数据
export function tree() {
    return service({
        url: '/category/tree',
        method: 'post'
    })
}
//商品分类父级的数据结构
export function parentTree() {
    return service({
        url: '/category/parentTree',
        method: 'post'
    })
}
//删除商品分类
export function deleteCategory(id) {
    return service({
        url: '/category/delete',
        method: 'post',
        params: { id }
    })
}
// 保存分类
export function saveCategory(params) {
    return service({
        url: '/category/save',
        method: 'post',
        params
    })
}
// 分类详情
export function detail(id) {
    return service({
        url: '/category/detail',
        method: 'post',
        params: { id }
    })
}