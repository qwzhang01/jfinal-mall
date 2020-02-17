/* jshint esversion: 6 */
// 获取权限对象信息
export const getOperator = state => {
    return state.operator
}
// 全局参数
export const getParams = state => {
    return state.params
}

export const getUser = state => {
    return {
        name: state.operator.name,
        username: state.operator.username,
        phone: state.operator.phone
    }
}