/* jshint esversion: 6 */
import service from '@/util/config'
import util from "@/util/util"

//商品分页数据
export function goodList(params, data = null) {
    return service({
        url: '/good/pageList',
        method: 'post',
        params,
        data
    })
}
//商品SKU-list
export function skuPage(goodId) {
    return service({
        url: '/good/skuPage',
        method: 'post',
        params: { goodId }
    })
}
// 商品详情
export function detail(goodId) {
    return service({
        url: '/good/detail',
        method: 'post',
        params: { goodId },
    })
}
// 保存商品
export function save(params) {
    return service({
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        url: '/good/save',
        method: 'post',
        data: params
    })
}
// 单个上下架
export function onOffSale(params) {
    return service({
        url: '/good/onOffSale',
        method: 'post',
        params: params
    })
}
export function earnPointSave(params) {
    return service({
        url: '/good/earnPoint',
        method: 'post',
        params: params
    })
}
export function recommendSave(params) {
    return service({
        url: '/good/recommend',
        method: 'post',
        params: params
    })
}
// 批量上下架
export function putOffSale(data) {
    return service({
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        },
        url: '/good/putOffSale',
        method: 'post',
        data: data
    })
}
export function hotSave(params) {
    return service({
        url: '/good/hotSave',
        method: 'post',
        params: params
    })
}
export function newSave(params) {
    return service({
        url: '/good/newSave',
        method: 'post',
        params: params
    })
}
// 删除商品
export function del(id) {
    return service({
        url: '/good/delete',
        method: 'post',
        params: { id }
    })
}
// 商品导出
export function download(params) {
    util.download('/good/download', '商品信息', params)
}