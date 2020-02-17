/* jshint esversion: 6 */
//  初始化
export const INIT_VUE = (status, params) => {
    status.params = params
}
// 将权限、路由对象存入VUEX
export const LOAD_OPERATOR = (status, operator) => {
    status.operator = operator
}