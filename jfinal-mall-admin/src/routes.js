/* jshint esversion: 6 */
import Login from './views/login/index.vue'
import NotFound from './views/404.vue'
import UnAuth from './views/403.vue'
import Layout from './views/layout/index.vue'

import Main from './views/cms/index/index.vue'

// 内容管理
import article from "@/views/cms/article/";
import articleForm from "@/views/cms/article/form.vue";
// 系统管理
import staff from "@/views/sys/staff/"
import role from "@/views/sys/role/"
import param from "@/views/sys/param/"

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
        name: '内容管理',
        meta: { authInfo: '内容管理' },
        iconCls: 'fa fa-newspaper-o',
        children: [
            { path: '/cms/article', component: article, name: '资讯管理', meta: { authInfo: '资讯管理-查看' } },
            { path: '/cms/article/form', component: articleForm, name: '资讯编辑', hidden: true, meta: { authInfo: '资讯管理-查看' } },
        ]
    },

    {
        path: '/',
        component: Layout,
        name: '系统管理',
        meta: { authInfo: '系统管理' },
        iconCls: 'fa fa-address-card',
        children: [
            { path: '/sys/user', component: staff, name: '系统用户', meta: { authInfo: '系统用户-查看' } },
            { path: '/sys/role', component: role, name: '系统角色', meta: { authInfo: '系统角色-查看' } },
            { path: '/sys/config', component: param, name: '参数查看', meta: { authInfo: '参数查看-查看' } },

        ]
    },
    {
        path: '*',
        hidden: true,
        redirect: { path: '/404' }
    }
];

export default routes;