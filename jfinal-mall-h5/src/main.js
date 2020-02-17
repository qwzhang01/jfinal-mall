/* jshint esversion: 6 */
import Vue from 'vue'
import App from './App'
import router from './router'
import state from './state/index'
import './comm/index.less'
import comm from './comm/index'
import {
  signInWechatMp
} from '../api/index'

// vant
import Vant from 'vant'
import 'vant/lib/index.css'

// ydui
import YDUI from 'vue-ydui'
import 'vue-ydui/dist/ydui.px.css'
import VueLazyLoad from 'vue-lazyload'

// 兼容低版本
import 'babel-polyfill'
import Es6Promise from 'es6-promise'
require('es6-promise').polyfill()
Es6Promise.polyfill()

// 图片懒加载 v-lazy
Vue.use(VueLazyLoad, {
  error: require('./assets/logo.png'),
  loading: require('./assets/logo_1.png')
})

Vue.use(require('vue-cookies'))
Vue.use(Vant)
Vue.use(YDUI)

// state
const store = state()
Vue.config.productionTip = true

// Vue
var vueFun = {
  vueActive: (active = false) => {
    if (active) {
      /* eslint-disable no-new */
      new Vue({
        el: '#app',
        router,
        store,
        components: {
          App
        },
        template: '<App/>',
        beforeCreate () {
          // 组建间通信，使用bus
          Vue.prototype.bus = this
        }
      })
    }
  }
}

/**
 * 浏览器登录判断
 */

// 微信登录
if (comm.isWeiXin()) {
  // 如果有code，进入使用code获取openID或登录的逻辑
  if (comm.getQueryString('code')) {
    // 判断是否存在tonken、openid、userid
    if (!sessionStorage.getItem('token') || !sessionStorage.getItem('userId') || !sessionStorage.getItem('openId') || !sessionStorage.getItem('wx_auth_code')) {
      signInWechatMp({
        code: comm.getQueryString('code')
      }).then(res => {
        if (res.status === 0) {
          sessionStorage.setItem('userId', res.data.userId)
          sessionStorage.setItem('token', res.data.accessToken)
          sessionStorage.setItem('wx_auth_code', res.data.wxAccessToken)
          sessionStorage.setItem('openId', res.data.openId)
          vueFun.vueActive(true)
        } else {
          console.log(res.msg)
        }
      })
    } else {
      vueFun.vueActive(true)
    }
  } else {
    // 如果不存在code，跳转到微信的redirect接口，获取code
    comm.wxcode()
  }
} else {
  // 不是微信浏览器
  vueFun.vueActive(true)
}
