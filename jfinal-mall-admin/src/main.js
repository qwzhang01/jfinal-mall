/* jshint esversion: 6 */

import Vue from 'vue'
import App from './App'
import 'element-ui/lib/theme-chalk/index.css'
import '@/styles/index.scss'
import ElementUI from 'element-ui'

import VueRouter from 'vue-router'
import store from './vuex/store'
import Vuex from 'vuex'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import routes from './routes'
/* import Mock from './mock'
Mock.bootstrap(); */
import 'font-awesome/css/font-awesome.min.css'

import { getToken, getUserId, removeToken } from '@/util/cookie'

import { checkPermission } from '@/util/operator'

Vue.use(ElementUI)
Vue.use(VueRouter)
Vue.use(Vuex)

NProgress.configure({ showSpinner: false });

const router = new VueRouter({
  mode: 'history',
  scrollBehavior: () => ({ y: 0 }),
  routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
  NProgress.start();
  let token = getToken()
  let userId = getUserId()
  if (token && userId && to.path === '/login') {
    next("/")
  }
  if (!token && !userId && to.path !== '/login') {
    next({ path: '/login' })
  } else if (to.path === '/login') {
    next()
  } else {
    let authInfo = to.meta.authInfo
    let operator = store.getters.getOperator.permission
    if (operator.length <= 0) {
      store.dispatch('loadOperator').then(() => {
        if (authInfo && !checkPermission(authInfo)) {
          next({ path: '/403' })
        } else {
          next()
        }
      })
    } else {
      if (authInfo && !checkPermission(authInfo)) {
        next({ path: '/403' })
      } else {
        next()
      }
    }
  }
})

router.afterEach(transition => {
  NProgress.done();
});

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')