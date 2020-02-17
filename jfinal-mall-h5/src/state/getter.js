import router from '../router'
/**
 *  获取参数
 */
export const getUser = state => {
  return state.userinfo.user
}
// 规格
export const getGood = state => {
  return state.goodsInfo.goodsData
}
// class选中的id
export const getClassId = state => {
  return state.Class.activeId
}
// class选中的key
export const getClassKey = state => {
  return state.Class.activeKey
}
// 首页中 选中的class
export const getpageClass = state => {
  return state.Index.navIndex
}
// 我要开店
export const getStep = state => {
  if (!state.Step.step1.mobile) {
    router.push({
      path: '/step1'
    })
  }
  return state.Step
}
