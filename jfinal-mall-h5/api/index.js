import host from '../config/domainConfig'
import axios from '../http/config'
import qs from 'qs'

/**
 * 获取微信配置信息
 */
export const WxJsConfig = params => {
  return axios.post(`${host.domainName}/wechat/jsConfig`, qs.stringify(params)).then(res => res.data)
}

/**
 *================================
 * 首页管理
 *
 * 说明：
 * 1)跳转（jump_type）字段说明
 * 1.咨询 2活动  3: 商品 4：商品分类 5：web链接  6：模块分类
 *================================
 * banner
 */
export const PegeBanner = params => {
  return axios.post(`${host.domainName}/home/banner`, qs.stringify(params)).then(res => res.data)
}
/**
 * 首页默认搜索
 */
export const PageSearch = params => {
  return axios.post(`${host.domainName}/home/recommendGood`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取首页金刚区按钮
 * joker
 */
export const PageNav = params => {
  return axios.post(`${host.domainName}/home/module`, qs.stringify(params)).then(res => res.data)
}
/**
 * 获取首页咨询
 * joker
 */
export const PageZixun = params => {
  return axios.post(`${host.domainName}/home/news`, qs.stringify(params)).then(res => res.data)
}
/**
 * 获取首页推广信息
 * joker
 */
export const PagePromote = params => {
  return axios.post(`${host.domainName}/home/promote`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取主打分类产品
 * joker
 */
export const PageflagshipCategory = params => {
  return axios.post(`${host.domainName}/home/flagshipCategory`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取一级主打产品(智慧生活)
 * joker
 */
export const PageflagshipMajor = params => {
  return axios.post(`${host.domainName}/home/flagshipMajor`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取热销榜
 * joker
 */
export const PagehotRank = params => {
  return axios.post(`${host.domainName}/home/hotRank`, qs.stringify(params)).then(res => res.data)
}

/**
 * 获取二级主打产品信息(旅游出行)
 * joker
 */
export const PageflagshipVice = params => {
  return axios.post(`${host.domainName}/home/flagshipVice`, qs.stringify(params)).then(res => res.data)
}

/**
 * 猜你喜欢
 * joker
 */
export const PageFavourite = params => {
  return axios.get(`${host.domainName}/home/favourite`, {
    params
  }).then(res => res.data)
}

/**
 * 秒杀拼团
 * joker
 */
export const PageFascinate = params => {
  return axios.get(`${host.domainName}/home/fascinate`, {
    params
  }).then(res => res.data)
}

/**
 *================================
 * 我的
 *================================
 */

/**
 *================================
 * 钱包管理
 *================================
 */

/**
 * 钱包详情
 * joker
 */
export const BalanceDetail = params => {
  return axios.get(`${host.domainName}/balance/detail`, {
    params
  }).then(res => res.data)
}

/**
 * 收支明细
 * joker
 */
export const BalanceList = params => {
  return axios.get(`${host.domainName}/balance/list`, {
    params
  }).then(res => res.data)
}

/**
 * 收支明细
 * joker
 */
export const ChargeList = params => {
  return axios.get(`${host.domainName}/balance/chargeList`, {
    params
  }).then(res => res.data)
}

/**
 * 收支明细
 * joker
 */
export const WithdrawalList = params => {
  return axios.get(`${host.domainName}/balance/withdrawalList`, {
    params
  }).then(res => res.data)
}

/**
 * 手机短信验证码
 * joker
 */
export const Sms = params => {
  return axios.get(`${host.domainName}/captcha/sms`, {
    params
  }).then(res => res.data)
}

/**
 * 申请提现
 * joker
 */
export const Apply = params => {
  return axios.get(`${host.domainName}/balance/apply`, {
    params
  }).then(res => res.data)
}

/**
 * 充值
 * joker
 */
export const Charge = params => {
  return axios.get(`${host.domainName}/balance/charge`, {
    params
  }).then(res => res.data)
}

/**
 *  微信发起支付 （商品支付、钱包充值）(APP/H5/公众号/PC)
 *  joker
 */
export const WxAppLanuch = params => {
  return axios.get(`${host.domainName}/pay/wxAppLanuch`, {
    params
  }).then(res => res.data)
}

/**
 * 支付宝发起支付
 */
export const AliAppLanuch = params => {
  return axios.get(`${host.domainName}/pay/aliAppLanuch`, {
    params
  }).then(res => res.data)
}

/**
 * 微信支付回调
 */
export const WxNotify = params => {
  return axios.get(`${host.domainName}/pay/wxNotify`, {
    params
  }).then(res => res.data)
}

/**
 * 支付宝支付回调
 */
export const AliNotify = params => {
  return axios.get(`${host.domainName}/pay/aliNotify`, {
    params
  }).then(res => res.data)
}

/**
 *================================
 * 订单
 *================================
 */

/**
 * 订单列表
 * joker
 */
export const OrderList = params => {
  return axios.get(`${host.domainName}/order/list`, {
    params
  }).then(res => res.data)
}

/**
 * 获取订单详情
 * joker
 */
export const OrderDetail = params => {
  return axios.get(`${host.domainName}/order/detail`, {
    params
  }).then(res => res.data)
}

/**
 * 提醒发货
 * joker
 */
export const RemindGoods = params => {
  return axios.post(`${host.domainName}/order/remindShipping`, qs.stringify(params)).then(res => res.data)
}
/**
 * 取消订单
 * joker
 */
export const Cancel = params => {
  return axios.post(`${host.domainName}/order/cancel`, qs.stringify(params)).then(res => res.data)
}
/**
 * 申请退款
 * joker
 */
export const Refund = params => {
  return axios.post(`${host.domainName}/order/refund`, qs.stringify(params)).then(res => res.data)
}

/**
 * 查看物流
 * joker
 */
export const ShipDetail = params => {
  return axios.post(`${host.domainName}/order/shipDetail`, qs.stringify(params)).then(res => res.data)
}

/**
 * 确定收货
 * joker
 */
export const Confirm = params => {
  return axios.post(`${host.domainName}/order/confirm`, qs.stringify(params)).then(res => res.data)
}

/**
 * 服务评价
 * joker
 */
export const Comment = params => {
  return axios.post(`${host.domainName}/order/comment`, params, {
    headers: {
      'Content-Type': 'application/json;charset=utf-8'
    }
  }).then(res => res.data)
}
/**
 * 根据订单id获取商品id和图片
 * joker
 */
export const GetGood = params => {
  return axios.post(`${host.domainName}/order/getGood`, qs.stringify(params)).then(res => res.data)
}

/**
 *================================
 * 收货地址
 *================================
 */

/**
 * 获取收货地址
 * joker
 */
export const GetAddress = params => {
  return axios.post(`${host.domainName}/address/getAddress`, qs.stringify(params)).then(res => res.data)
}

/**
 * 修改地址
 * joker
 */
export const ModifyAddress = params => {
  return axios.post(`${host.domainName}/address/modifyAddress`, qs.stringify(params)).then(res => res.data)
}
/**
 * 增加地址
 * joker
 */
export const AddAddress = params => {
  return axios.post(`${host.domainName}/address/addAddress`, qs.stringify(params)).then(res => res.data)
}
/**
 * 删除地址
 * joker
 */
export const DelAddress = params => {
  return axios.post(`${host.domainName}/address/delAddress`, qs.stringify(params)).then(res => res.data)
}
/**
 * 获取省市区三级联动数据
 * joker
 */

export const Regin = params => {
  return axios.get(`${host.domainName}/param/regin`, {
    params
  }).then(res => res.data)
}

/**
 * 上传图片(base64)
 */
export const uploadImgBase64 = params => {
  return axios.post(`${host.domainName}/file/updateString`, params, {
    headers: {
      'Content-Type': 'application/json;charset=utf-8'
    }
  }).then(res => res.data)
}

/**
 * 上传图片(直接传文件)
 */
export const uploadImgFile = params => {
  return axios.post(`${host.domainName}/file/updateFilet`, params, {
    headers: {
      'Content-Type': 'application/json;charset=utf-8'
    }
  }).then(res => res.data)
}

/**
 *================================
 * 用户信息
 *================================
 */
/**
 * 用户信息
 */
export const UserDetail = params => {
  return axios.get(`${host.domainName}/user/detail`, {
    params
  }).then(res => res.data)
}
/**
 * 用户简单信息 全局共享使用
 * @param {用户ID} userId
 */
export const simpleDetail = userId => {
  return axios.get(`${host.domainName}/user/simpleDetail`, {
    params: { userId }
  }).then(res => res.data)
}
/**
 * 绑定银行卡
 * @param {*} params
 */
export const BindBank = params => {
  return axios.get(`${host.domainName}/user/bindBank`, {
    params
  }).then(res => res.data)
}
/**
 * 更新用户信息
 */
export const UserDdit = params => {
  return axios.get(`${host.domainName}/user/edit`, {
    params
  }).then(res => res.data)
}

/**
 *================================
 * 注册和登录
 *================================
 */

/**
 * 发送验证
 * joker
 */
export const sms = params => {
  return axios.get(`${host.domainName}/captcha/sms`, {
    params
  }).then(res => res.data)
}

/**
 * 用户注册
 * joker
 */
export const signUp = params => {
  return axios.post(`${host.domainName}/login/signUp`, qs.stringify(params)).then(res => res.data)
}

/**
 * 密码登录
 * joker
 */
export const signInPw = params => {
  return axios.post(`${host.domainName}/login/signInPw`, qs.stringify(params)).then(res => res.data)
}

/**
 * 手机短信登录
 * joker
 */
export const signInSmsCode = params => {
  return axios.post(`${host.domainName}/login/signInSmsCode`, qs.stringify(params)).then(res => res.data)
}

/**
 * 退出登录
 * joker
 */
export const logout = params => {
  return axios.post(`${host.domainName}/login/logout`, qs.stringify(params)).then(res => res.data)
}

/**
 * 手机短信重置密码
 * joker
 */
export const reSetPw = params => {
  return axios.post(`${host.domainName}/login/reSetPw`, qs.stringify(params)).then(res => res.data)
}

/**
 * 微信公众号登录
 * joker
 */
export const signInWechatMp = params => {
  return axios.post(`${host.domainName}/login/signInWechatMp`, qs.stringify(params)).then(res => res.data)
}
/**
 * 名称、关键字搜索
 * joker
 */
export const searchPageByName = params => {
  return axios.post(`${host.domainName}/good/searchPage`, qs.stringify(params)).then(res => res.data)
}

/**
 * 热搜
 * joker
 */
export const searKey = params => {
  return axios.post(`${host.domainName}/home/searchKey`, qs.stringify(params)).then(res => res.data)
}

/**
 * 自定义查询条件
 * joker
 */
export const searchForms = params => {
  return axios.post(`${host.domainName}/good/searchForm`, qs.stringify(params)).then(res => res.data)
}


/**
 *================================
 * 积分
 *================================
 */
export const pointSum = params => {
  return axios.post(`${host.domainName}/point/sum`, qs.stringify(params)).then(res => res.data)
}


/**
 *================================
 * 分类信息
 *================================
 */
export const firstCategory = params => {
  return axios.post(`${host.domainName}/home/category`, qs.stringify(params)).then(res => res.data)
}
