/* jshint esversion: 6 */
import service from '@/util/config'
import qs from 'qs'

// 资讯分页数据
export function articleList(params, data = null) {
    return service({
        url: '/article/pageList',
        method: 'post',
        params,
        data
    })
}
// 保存资讯
export function saveArticle(params) {
    return service({
        url: '/article/save',
        method: 'post',
        data: qs.stringify(params)
    })
}
// 删除资讯
export function deleteArticle(id) {
    return service({
        url: '/article/delete',
        method: 'post',
        params: { id }
    })
}
export function detailArticle(id) {
    return service({
        url: '/article/detail',
        method: 'post',
        params: { id }
    })
}