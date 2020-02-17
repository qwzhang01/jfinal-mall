/* jshint esversion: 6 */
import store from './../vuex/store'

// 判断按钮是否有权限
export function checkPermission(value) {

  if (!value) {
    return true
  }
  let operator = store.getters.getOperator.permission
  if (operator) {
    if (operator.indexOf(value) > -1) {
      return true
    }
  }
  return false
}