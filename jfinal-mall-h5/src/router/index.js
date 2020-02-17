/* jshint esversion: 6 */
import Vue from 'vue'
import Router from 'vue-router'
import store from '../state/index.js'
import { simpleDetail } from '../../api/index.js'

import index from '@/page/index'
import info from '@/page/information/info'
import infoList from '@/page/information/list'
import cart from '@/page/cart/index'
import myself from '@/page/myself'
import set from '@/page/myself/set'
import cartSet from '@/page/myself/cartSet'
import myinfo from '@/page/myself/myinfo'
import orderlist from '@/page/myself/orderlist'
import orderinfo from '@/page/myself/orderinfo'
import wallet from '@/page/myself/wallet'
import walletlist from '@/page/myself/walletlist'
import authentication from '@/page/myself/authentication'
import verification from '@/page/myself/verification'
import cashwithdrawal from '@/page/myself/cashwithdrawal'
import recharge from '@/page/myself/recharge'
import addresslist from '@/page/myself/addresslist'
import logistics from '@/page/myself/logistics'
import evaluate from '@/page/myself/evaluate'
import modifyaddress from '@/page/myself/modifyaddress'
import register from '@/page/login/register'
import login from '@/page/login/'
import goodsinfo from '@/page/goods/info'
import valuateList from '@/page/goods/evaluate'
import collectbill from '@/page/goods/collectbill'
import flashSaleList from '@/page/goods/activity/flash_sale_list'
import storepage from '@/page/store/'
import storeClassList from '@/page/store/classList'
import recommendStore from '@/page/store/recommend'
import ordinaryorder from '@/page/order/'
import activityorder from '@/page/order/activityorder'
import loteryorder from '@/page/order/lotery'
import payment from '@/page/payment/'
import collection from '@/page/collection/'
import categoryList from '@/page/class/'
import search from '@/page/search/'
import result from '@/page/search/result'
import groupBuyingList from '@/page/goods/activity/groupBuying/list'
import robShoppingList from '@/page/goods/activity/robShopping/list'
import robShoppingorderList from '@/page/goods/activity/robShopping/orderList'
import robShoppingOrderDetails from '@/page/goods/activity/robShopping/details'
import step1 from '@/page/store/step1'
import step2 from '@/page/store/step2'
import step3 from '@/page/store/step3'
import step4 from '@/page/store/step4'
import step5 from '@/page/store/step5'
import step6 from '@/page/store/step6'
import step7 from '@/page/store/step7'

// 分销中心首页
import retail from '@/page/retail'
// 分销中心 --- 我的用户
import customer from '@/page/retail/customer'
// 分销中心 --- 我的投资
import invest from '@/page/retail/invest'
// 分销中心 --- 推广业绩
import expand from '@/page/retail/expand'
// 分销中心 --- 收益明细
import earnings from '@/page/retail/earnings'
// 分销中心 --- 体现记录
import record from '@/page/retail/record'

Vue.use(Router)

const router = new Router({
  routes: [
    // 首页
    {
      path: '/',
      name: 'home',
      component: index,
      meta: {
        keepAlive: true // 缓存
      }
    },
    // 咨询
    {
      path: '/info',
      name: 'info',
      component: info
    },
    // 资讯列表
    {
      path: '/infoList',
      name: 'infoList',
      component: infoList
    },
    // 购物车
    {
      path: '/cart',
      name: 'cart',
      component: cart
    },
    // 分类
    {
      path: '/categoryList',
      name: 'categoryList',
      component: categoryList
    },
    // 个人中心
    {
      path: '/myself',
      name: 'myself',
      component: myself
    },
    // 个人设置
    {
      path: '/set',
      name: 'set',
      component: set
    },
    // 提现设置
    {
      path: '/cartSet',
      name: 'cartSet',
      component: cartSet
    },
    // 个人信息
    {
      path: '/myinfo',
      name: 'myinfo',
      component: myinfo
    },
    // 订单列表
    {
      path: '/orderlist',
      name: 'orderlist',
      component: orderlist
    },
    // 订单详情
    {
      path: '/orderinfo',
      name: 'orderinfo',
      component: orderinfo
    },
    // 钱包
    {
      path: '/wallet',
      name: 'wallet',
      component: wallet
    },
    // 钱包列表
    {
      path: '/walletlist',
      name: 'walletlist',
      component: walletlist
    },
    // 提现
    {
      path: '/authentication',
      name: 'authentication',
      component: authentication
    },
    // 身份验证
    {
      path: '/verification',
      name: 'verification',
      component: verification
    },
    // 提现
    {
      path: '/cashwithdrawal',
      name: 'cashwithdrawal',
      component: cashwithdrawal
    },
    // 充值
    {
      path: '/recharge',
      name: 'recharge',
      component: recharge
    },
    // 地址列表
    {
      path: '/addresslist',
      name: 'addresslist',
      component: addresslist
    },
    // 查看物流
    {
      path: '/logistics',
      name: 'logistics',
      component: logistics
    },
    // 评价商品
    {
      path: '/evaluate',
      name: 'evaluate',
      component: evaluate
    },
    // 修改地址
    {
      path: '/modifyaddress',
      name: 'modifyaddress',
      component: modifyaddress
    },
    // 注册
    {
      path: '/register',
      name: 'register',
      component: register
    },
    // 登录
    {
      path: '/login',
      name: 'login',
      component: login
    },
    // 商品详情
    {
      path: '/goodsinfo',
      name: 'goodsinfo',
      component: goodsinfo
    },
    // 商品评价列表
    {
      path: '/valuateList',
      name: 'valuateList',
      component: valuateList
    },
    // 商铺首页
    {
      path: '/storepage',
      name: 'storepage',
      component: storepage
    },
    // 推荐店铺
    {
      path: '/recommendStore',
      name: 'recommendStore',
      component: recommendStore
    },
    // 普通订单
    {
      path: '/ordinaryorder',
      name: 'ordinaryorder',
      component: ordinaryorder
    },
    // 秒杀订单
    {
      path: '/activityorder',
      name: 'activityorder',
      component: activityorder
    },
    // 拼单订单
    {
      path: '/loteryorder',
      name: 'loteryorder',
      component: loteryorder
    },
    // 支付
    {
      path: '/payment',
      name: 'payment',
      component: payment
    },
    // 收藏列表
    {
      path: '/collection',
      name: 'collection',
      component: collection
    },
    // 凑单列表
    {
      path: '/collectbill',
      name: 'collectbill',
      component: collectbill
    },
    // 店铺分类列表
    {
      path: '/storeClassList',
      name: 'storeClassList',
      component: storeClassList
    },
    // 搜索
    {
      path: '/search',
      name: 'search',
      component: search
    },
    // 结果
    {
      path: '/result',
      name: 'result',
      component: result
    },
    // 秒杀列表
    {
      path: '/flash_sale_list',
      name: 'flash_sale_list',
      component: flashSaleList
    },
    // 团购列表
    {
      path: '/groupBuyingList',
      name: 'groupBuyingList',
      component: groupBuyingList
    },
    // 一元抢购列表
    {
      path: '/robShoppingList',
      name: 'robShoppingList',
      component: robShoppingList
    },
    // 一元抢购中奖列表
    {
      path: '/robShoppingorderList',
      name: 'robShoppingorderList',
      component: robShoppingorderList
    },
    // 一元请购订单详情
    {
      path: '/robShoppingOrderDetails',
      name: 'robShoppingOrderDetails',
      component: robShoppingOrderDetails
    },
    // 我要开店(填写用户信息)
    {
      path: '/step1',
      name: 'step1',
      component: step1
    },
    // 我要开店(填写店铺信息)
    {
      path: '/step2',
      name: 'step2',
      component: step2
    },
    // 我要开店(提交认证资料)
    {
      path: '/step3',
      name: 'step3',
      component: step3
    },
    // 我要开店(身份认证)
    {
      path: '/step4',
      name: 'step4',
      component: step4
    },
    // 我要开店(营业执照)
    {
      path: '/step5',
      name: 'step5',
      component: step5
    },
    // 我要开店(银行卡)
    {
      path: '/step6',
      name: 'step6',
      component: step6
    },
    // 我要开店(审核)
    {
      path: '/step7',
      name: 'step7',
      component: step7
    },
    // 分销中心首页
    {
      path: '/retail',
      name: 'retail',
      component: retail
    },
    // 邀请好友
    {
      path: '/retail/invite',
      name: 'inviteFriend',
      component: () => import('@/page/retail/inviteFriend.vue')
    },
    // 商品分析列表
    {
      path: '/retail/share',
      name: 'goodShare',
      component: () => import('@/page/retail/goodShare.vue')
    },
    // 提现第一步，输入银行信息
    {
      path: '/retail/withdraw/authentication',
      name: 'withdraw_authentication',
      component: () => import('@/page/retail/withdraw/authentication.vue')
    },
    // 提现第二步，输入提取金额
    {
      path: '/retail/withdraw/cashwithdrawal',
      name: 'withdraw_cashwithdrawal',
      component: () => import('@/page/retail/withdraw/cashwithdrawal.vue')
    },
    // 提现第三步，验证短信，提交表单
    {
      path: '/retail/withdraw/verification',
      name: 'withdraw_verification',
      component: () => import('@/page/retail/withdraw/verification.vue')
    },
    // 分销中心 --- 我的用户
    {
      path: '/customer',
      name: 'customer',
      component: customer
    },
    // 分销中心 --- 我的投资
    {
      path: '/invest',
      name: 'invest',
      component: invest
    },
    // 分销中心 --- 推广业绩
    {
      path: '/expand',
      name: 'expand',
      component: expand
    },
    // 分销中心 --- 收益明细
    {
      path: '/earnings',
      name: 'earnings',
      component: earnings
    },
    // 分销中心 --- 提现记录
    {
      path: '/record',
      name: 'record',
      component: record
    }
  ]
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // console.log('全局前置守卫to' + to)
  // console.log('全局前置守卫from' + from)
  // console.log('全局前置守卫next' + next)
  // to和from都是路由实例
  // to：即将跳转到的路由
  // from：现在的要离开的路由
  // next：函数
  // 登录状态
  if (sessionStorage.getItem('userId') && sessionStorage.getItem('token')) {
    // 状态中没有用户信息
    let s = store()
    let user = s.getters.getUser
    if (!user || !user.userId) {
      simpleDetail().then(res => {
        s.dispatch('setUser', res.data)
      })
    }
  }
  next()
})

// 全局解析守卫
// router.beforeResolve((to, from, next) => {
//   console.log('全局解析守卫to' + to)
//   console.log('全局解析守卫from' + from)
//   console.log('全局解析守卫next' + next)
//   next()
// })

// 全局后置钩子
// router.afterEach((to, form) => {
//   console.log('全局后置钩子to' + to)
//   console.log('全局后置钩子from' + form)
// })

export default router
