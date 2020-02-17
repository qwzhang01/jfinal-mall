/* jshint esversion: 6 */
const userinfo = {
  state: {
    // 用户详情
    user: {
      mobile: '',
      usableRemain: 0,
      userId: ''
    }
  },
  mutations: {
    // 更改商品详情
    MUTATIONS_USER(state, data) {
      // console.log(user)
      state.user = data
    }
  },
  actions: {
  },
  getters: {}
}

export default userinfo
