/* jshint esversion: 6 */
import Login from './views/login/index.vue'
import NotFound from './views/404.vue'
import UnAuth from './views/403.vue'
import Layout from './views/layout/index.vue'

import Main from './views/cms/index/index.vue'
// 会员管理
import userInfo from "./views/member/info/index.vue"
import userInfo2 from "./views/member/info2/index.vue"
import invest from "./views/member/invest/index.vue"
import point from "./views/member/point/index.vue"
// 订单管理
import orderInfo from "./views/order/info/index.vue"
import orderDeliver from "./views/order/deliver/index.vue"
import lotteryOrder from "./views/order/lottery/index.vue"
import doorService from "./views/order/doorService/index.vue"
import orderRefund from "./views/order/refund/index.vue"
import orderReturn from "./views/order/return/index.vue"
// 提现管理
import withdrawlAuth from "./views/withdrawl/auth/index.vue"
import withdrawlPay from "./views/withdrawl/pay/index.vue"
import withdrawlSuccess from "./views/withdrawl/success/index.vue"
import withdrawlFail from "./views/withdrawl/fail/index.vue"
// 商品管理
import goodInfo from "./views/good/info/"
import goodForm from "./views/good/info/goodForm.vue"
import brand from "./views/good/brand/"
import category from './views/good/category/'
import spec from './views/good/spec/'
// 促销管理
import seckill from "@/views/prom/seckill/"
import seckillGood from "@/views/prom/seckill/list"
import crowdRob from "@/views/prom/crowdRob/"
import crowdRobGood from "@/views/prom/crowdRob/list"
// 内容管理
import article from "@/views/cms/article/";
import articleForm from "@/views/cms/article/form.vue";
import appIndex from "@/views/cms/app-home/"
import specShowGood from "@/views/cms/app-home/good.vue"
// 店铺管理
import storeAuth from "./views/store/auth/"
import storeInfo from "./views/store/info/"
// 系统管理
import staff from "@/views/sys/staff/"
import role from "@/views/sys/role/"
import param from "@/views/sys/param/"
import smsLog from "@/views/sys/sms/"

let routes = [
    {
        path: '/login',
        component: Login,
        name: '',
        hidden: true
    },
    {
        path: '/404',
        component: NotFound,
        name: '',
        hidden: true
    },
    {
        path: '/403',
        component: UnAuth,
        name: '',
        hidden: true
    },
    {
        path: '/',
        component: Layout,
        name: '',
        leaf: true,
        redirect: '/main',
        iconCls: 'fa fa-home',
        children: [
            { path: '/main', component: Main, name: '首页' }
        ]
    },
    {
        path: '/',
        component: Layout,
        name: '会员管理',
        iconCls: 'fa fa-users',
        meta: { authInfo: '会员管理' },
        children: [
            { path: '/member/info', component: userInfo, name: '会员审核', meta: { authInfo: '会员信息-查看' } },
            { path: '/member/info2', component: userInfo2, name: '特约商审核', meta: { authInfo: '会员信息-查看' } },
            // { path: '/member/invest', component: invest, name: '有奖购买', meta: { authInfo: '有奖购买-查看' } },
            // { path: '/member/point', component: point, name: '积分明细', meta: { authInfo: '积分明细-查看' } },
            // { path: '/member/charge', component: charge, name: '充值信息', meta: { authInfo: '充值管理-查看' } },
            // { path: '/member/balance', component: balance, name: '钱包明细', meta: { authInfo: '钱包明细-查看' } },
        ]
    },
    // {
    //     path: '/',
    //     component: Layout,
    //     name: '订单管理',
    //     meta: { authInfo: '订单管理' },
    //     iconCls: 'fa fa-first-order',
    //     children: [
    //         { path: '/order/info', component: orderInfo, name: '订单信息', meta: { authInfo: '订单信息-查看' } },
    //         { path: '/order/deliver', component: orderDeliver, name: '订单发货', meta: { authInfo: '订单发货-查看' } },
    //         { path: '/order/lottery', component: lotteryOrder, name: '拼团抽奖', meta: { authInfo: '拼团抽奖订单-查看' } },
    //         // { path: '/order/refund', component: orderRefund, name: '退款管理', meta: { authInfo: '退款管理-查看' } },
    //         // { path: '/order/return', component: orderReturn, name: '退货管理', meta: { authInfo: '退货管理-查看' } }
    //     ]
    // },
    // {
    //     path: '/',
    //     component: Layout,
    //     name: '提现管理',
    //     meta: { authInfo: '提现管理' },
    //     iconCls: 'fa fa-address-card',
    //     children: [
    //         { path: '/withdrawl/auth', component: withdrawlAuth, name: '待审核', meta: { authInfo: '审核-查看' } },
    //         { path: '/withdrawl/pay', component: withdrawlPay, name: '待打款', meta: { authInfo: '打款-查看' } },
    //         { path: '/withdrawl/success', component: withdrawlSuccess, name: '成功记录', meta: { authInfo: '成功记录-查看' } },
    //         { path: '/withdrawl/fail', component: withdrawlFail, name: '拒绝记录', meta: { authInfo: '拒绝记录-查看' } }
    //     ]
    // },
    // {
    //     path: '/',
    //     component: Layout,
    //     name: '商品管理',
    //     meta: { authInfo: '商品管理' },
    //     iconCls: 'fa fa-address-card',
    //     children: [
    //         { path: '/good/info', component: goodInfo, name: '商品信息', meta: { authInfo: '商品信息-查看' } },
    //         { path: '/good/form', component: goodForm, name: '商品编辑', hidden: true, meta: { authInfo: '商品信息-查看' } },
    //         { path: '/good/category', component: category, name: '商品分类', meta: { authInfo: '商品分类-查看' } },
    //         { path: '/good/spec', component: spec, name: '商品规格', meta: { authInfo: '商品规格-查看' } },
    //         { path: '/good/brand', component: brand, name: '品牌管理', meta: { authInfo: '品牌管理-查看' } }
    //     ]
    // },
    // {
    //     path: '/',
    //     component: Layout,
    //     name: '售后管理',
    //     meta: { authInfo: '上门安装' },
    //     iconCls: 'fa fa-address-card',
    //     children: [
    //         { path: '/door/service', component: doorService, name: '上门安装', meta: { authInfo: '上门安装-查看' } }
    //     ]
    // },
    // {
    //     path: '/',
    //     component: Layout,
    //     name: '促销管理',
    //     meta: { authInfo: '促销管理' },
    //     iconCls: 'fa fa-address-card',
    //     children: [
    //         { path: '/seckill', component: seckill, name: '秒杀设置', meta: { authInfo: '秒杀设置-查看' } },
    //         { path: '/seckill/good', component: seckillGood, name: '秒杀商品', hidden: true, meta: { authInfo: '秒杀设置-商品查看' } },

    //         { path: '/crowdRob', component: crowdRob, name: '抢购抽奖', meta: { authInfo: '抢购设置-查看' } },
    //         { path: '/crowdRob/good', component: crowdRobGood, name: '抢购商品', hidden: true, meta: { authInfo: '抢购设置-商品查看' } },
    //     ]
    // },

    {
        path: '/',
        component: Layout,
        name: '客户管理',
        meta: { authInfo: '店铺管理' },
        iconCls: 'fa fa-id-card-o',
        children: [
            // { path: '/store/auth', component: storeAuth, name: '开店审核', meta: { authInfo: '开店审核-查看' } },
            { path: '/store/info', component: storeInfo, name: '客户信息', meta: { authInfo: '店铺信息-查看' } },
        ]
    },
    {
        path: '/',
        component: Layout,
        name: '提现管理',
        meta: { authInfo: '提现管理' },
        iconCls: 'fa fa-address-card',
        children: [
            { path: '/withdrawl/auth', component: withdrawlAuth, name: '提现审核', meta: { authInfo: '审核-查看' } },
            // { path: '/withdrawl/auth', component: withdrawlAuth, name: '待审核', meta: { authInfo: '审核-查看' } },
            // { path: '/withdrawl/pay', component: withdrawlPay, name: '待打款', meta: { authInfo: '打款-查看' } },
            // { path: '/withdrawl/success', component: withdrawlSuccess, name: '成功记录', meta: { authInfo: '成功记录-查看' } },
            // { path: '/withdrawl/fail', component: withdrawlFail, name: '拒绝记录', meta: { authInfo: '拒绝记录-查看' } }
        ]
    },
    {
        path: '/',
        component: Layout,
        name: '内容管理',
        meta: { authInfo: '内容管理' },
        iconCls: 'fa fa-newspaper-o',
        children: [
            { path: '/cms/article', component: article, name: '资讯管理', meta: { authInfo: '资讯管理-查看' } },
            { path: '/cms/article/form', component: articleForm, name: '资讯编辑', hidden: true, meta: { authInfo: '资讯管理-查看' } },
            { path: '/app/index', component: appIndex, name: 'APP首页', hidden: true, meta: { authInfo: 'APP首页-查看' } },
            { path: '/app/specShow/good', component: specShowGood, name: 'APP首页专场商品', hidden: true, meta: { authInfo: 'APP首页-查看' } },
        ]
    }, {
        path: '/',
        component: Layout,
        name: '系统管理',
        meta: { authInfo: '系统管理' },
        iconCls: 'fa fa-address-card',
        children: [
            { path: '/sys/user', component: staff, name: '系统用户', meta: { authInfo: '系统用户-查看' } },
            { path: '/sys/role', component: role, name: '系统角色', meta: { authInfo: '系统角色-查看' } },
            // { path: '/sys/config', component: param, name: '参数查看', meta: { authInfo: '参数查看-查看' } },
            { path: '/sys/smsLog', component: smsLog, name: '短息记录', meta: { authInfo: '短信记录-查看' } }

        ]
    },
    {
        path: '*',
        hidden: true,
        redirect: { path: '/404' }
    }
];

export default routes;