import Vue from 'vue'
import Vuex from 'vuex'
// 全局
import * as actions from './actions'
import * as getters from './getter'
// 模块
import goodsInfo from './modules/goodsInfo'
import userinfo from './modules/userinfo'
import Class from './modules/class'
import Index from './modules/'
import Step from './modules/step'

Vue.use(Vuex)

// 开启严格模式
// const debug = process.env.NODE_ENV !== 'production'

const store = new Vuex.Store({
  getters,
  actions,
  modules: {
    userinfo,
    goodsInfo,
    Class,
    Index,
    Step
  }
  // strict: debug
})
export default () => store
