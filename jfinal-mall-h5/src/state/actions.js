/* jshint esversion: 6 */
// 更改状态

// 修改商品信息
export function goodsDataFun({
  commit
}, newData) {
  return commit('GOODSDATAFUN', newData)
}
// 用户信息
export function setUser({
  commit
}, user) {
  return commit('MUTATIONS_USER', user)
}
// 改变当前选中classId
export function slelctClassFun({
  commit
}, newData) {
  return commit('SELECTCLASSFUN', newData)
}

// 改变首页中选中的导航
export function slelctPageClassFun({
  commit
}, newData) {
  return commit('NAVINDEXFUN', newData)
}
// 我要申请开店
export function stepFun({
  commit
}, newData) {
  return commit('STEPFUN', newData)
}

// 我要申请开店
export function addStore({
  commit
}, newData) {
  return commit('ADDSTORE', newData)
}

// 提交申请开店认证
export function AttestationStore({
  commit
}, newData) {
  return commit('ATTESTATIONSTORE', newData)
}

// 提交身份认证
export function StoreStep4({
  commit
}, newData) {
  return commit('STORESTEP4', newData)
}

// 营业执照
export function StoreStep5({
  commit
}, newData) {
  return commit('STORESTEP5', newData)
}

// 提交银行卡
export function StoreStep6({
  commit
}, newData) {
  return commit('STORESTEP6', newData)
}
