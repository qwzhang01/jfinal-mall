/* jshint esversion: 6 */
import Vue from 'vue'
import Vuex from 'vuex'
import * as actions from './actions'
import * as getters from './getters'
import * as mutations from './mutations'

Vue.use(Vuex)

const state = {
    // 权限相关信息
    operator: {
        isLoad: false,
        permission: [],
        name: '',
        username: '',
        phone: ''
    },
    // 全局参数
    params: {
        fileHost: ''
    }
}

export default new Vuex.Store({
    actions,
    getters,
    state,
    mutations
})