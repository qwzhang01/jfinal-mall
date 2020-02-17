/* jshint esversion: 6 */
import { staffOperator } from './../api/system/staff'
import { basic } from "@/api/system/param.js";

// 获取参数
export const initVue = ({ commit }) => {
    return new Promise((resolve, reject) => {
        basic("fileHost").then(res => {
            if (res.status === 0) {
                let params = { fileHost: res.data };
                commit('INIT_VUE', params)
            }
            resolve()
        }).catch(error => {
            reject(error)
        })
    })
}
// 将权限、路由对象存入VUEX
export const loadOperator = ({ commit }) => {

    return new Promise((resolve, reject) => {
        staffOperator().then(res => {
            if (res.status === 0) {
                let operator = {
                    isLoad: true,
                    permission: res.data.permission,
                    name: res.data.name,
                    phone: res.data.phone,
                    username: res.data.username
                }
                commit('LOAD_OPERATOR', operator)
            }
            resolve()
        }).catch(error => {
            reject(error)
        })
    })
}