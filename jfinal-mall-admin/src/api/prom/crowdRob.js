/**
 * 抢购接口
 */
import axios from '../../util/config'
import qs from 'qs'
/**
 * 抢购分页数据
 */
export const Pages = params => { return axios.get(`/lottery/page`, { params }).then(res => res) }

/**
 * 新增/编辑抢购活动
 */
export const Forms = params => { return axios.post(`/lottery/form`, qs.stringify(params)).then(res => res) }

/**
 * 设置首页是否显示
 */
export const setShow = params => { return axios.post(`/lottery/setShow`, qs.stringify(params)).then(res => res) }

/**
 * 设置首页图片
 */
export const setHomeImg = params => { return axios.post(`/lottery/setHomeImg`, qs.stringify(params)).then(res => res) }

/**
 * 设置活动banner图片
 */
export const setBanner = params => { return axios.post(`/lottery/setBanner`, qs.stringify(params)).then(res => res) }

/**
 * 设置活动规则图片
 */
export const setReg = params => { return axios.post(`/lottery/setRegulation`, qs.stringify(params)).then(res => res) }

/**
 * 获取抢购抽奖设置信息
 */
export const getSet = params => { return axios.post(`/lottery/getSet`, qs.stringify(params)).then(res => res) }





/**
 * =============== 
 *  进场
 * ===============
 */

/**
 * 抢购活动的商品列表
 */
export const GoodList = params => { return axios.post(`/lottery/good`, qs.stringify(params)).then(res => res) }

/**
 * 添加商品
 */
export const AddGood = params => { return axios.post(`/lottery/addGood`, qs.stringify(params)).then(res => res) }

/**
 * 商品模糊查询
 */
export const SearchGoods = params => { return axios.post(`/good/search`, qs.stringify(params)).then(res => res) }

/**
 * 抢购商品详情
 */

export const GoodsDetails = params => {
    return axios.get(`/good/simpleDetail`, { params }, {
        headers: {
            'Content-Type': 'application/json;charset=UTF-8'
        }
    }).then(res => res)
}


/**
 * 上架
 */
export const OnSale = params => { return axios.post(`/lottery/onSale`, qs.stringify(params)).then(res => res) }


/**
 * 下架
 */
export const OffSale = params => { return axios.get(`/lottery/offSale`, { params }).then(res => res) }

/**
 * 批量下架
 */
export const OffSaleBatch = params => { return axios.post(`/lottery/offSaleBatch`, qs.stringify(params)).then(res => res) }

/**
 * 删除
 */
export const delGood = params => { return axios.post(`/lottery/delGood`, qs.stringify(params)).then(res => res) }

/**
 * 商品排序
 */
export const sort = params => { return axios.post(`/lottery/sort`, qs.stringify(params)).then(res => res) }