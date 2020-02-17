/* jshint esversion: 6 */
import service from '@/util/config'

//品牌分页数据
export function brandList(params, data = null) {
    return service({
        url: '/brand/pageList',
        method: 'post',
        params,
        data
    })
}
//根据分类获取所有品牌
export function brandSelect(categoryId, data = null) {
    return service({
        url: '/brand/select',
        method: 'post',
        params: { categoryId },
        data
    })
}
// 获取详情
export function detailBrand(id) {
    return service({
        url: '/brand/detail',
        method: 'post',
        params: { id }
    })
}
// 保存品牌
export function saveBrand(params) {
    return service({
        url: '/brand/save',
        method: 'post',
        params
    })
}
// 删除品牌
export function deleteBrand(ids) {
    return service({
        url: '/brand/delete',
        method: 'post',
        params: { ids }
    })
}

