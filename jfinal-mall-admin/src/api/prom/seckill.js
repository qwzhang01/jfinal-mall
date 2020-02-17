/**
 * 秒杀接口
 */
import axios from '../../util/config'
import qs from 'qs'
/**
 * 获取秒杀分页数据
 */
export const Pages = params => { return axios.get(`/flash/page`, { params }).then(res => res) }

/**
 * 新增/编辑秒杀活动
 */
export const Forms = params => { return axios.post(`/flash/save`, qs.stringify(params)).then(res => res) }

/**
 * 复制一场活动
 * @param {*} params 
 */
export const Copys = params => { return axios.post(`/flash/copyFlash`, qs.stringify(params)).then(res => res) }
/**
 * 删除秒杀活动
 * @param {*} params 
 */
export const Deletes = params => { return axios.post(`/flash/delete`, qs.stringify(params)).then(res => res) }
/**
 * 同步
 * @param {*} params 
 */
export const Syncs = params => { return axios.post(`/flash/synchronization`, qs.stringify(params)).then(res => res) }


/**
 * =============== 
 *  进场
 * ===============
 */

/**
 * 获取秒杀活动的商品列表
*/
export const GoodList = params => { return axios.post(`/flash/good`, qs.stringify(params)).then(res => res) }

/**
 * 添加商品
 * joker
 */
export const AddGood = params => { return axios.post(`/flash/addGood`, qs.stringify(params)).then(res => res) }

/**
 * 商品模糊查询
 */
export const SearchGoods = params => { return axios.post(`/good/search`, qs.stringify(params)).then(res => res) }

/**
 * 获取商品详情
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
export const OnSale = params => { return axios.post(`/flash/onSale`, qs.stringify(params)).then(res => res) }


/**
 * 下架
 */
export const OffSale = params => { return axios.get(`/flash/offSale`, { params }).then(res => res) }

/**
 * 设置虚假购买数量
 */
export const SetFakeBuyNum = params => { return axios.post(`/flash/setFakeBuyNum`, qs.stringify(params)).then(res => res) }

/**
 * 批量下架
 */
export const OffSaleBatch = params => { return axios.post(`/flash/offSaleBatch`, qs.stringify(params)).then(res => res) }

/**
 * 批量下架
 */
export const delGood = params => { return axios.post(`/flash/delGood`, qs.stringify(params)).then(res => res) }

/**
 * 商品排序
 */
export const sort = params => { return axios.post(`/flash/sort`, qs.stringify(params)).then(res => res) }