const cart = {
  state: {
    // 首页导航标签
    navIndex: 0
  },
  mutations: {
    // 更改商品详情
    NAVINDEXFUN(state, newData) {
      state.navIndex = newData.key
    }
  },
  actions: {},
  getters: {}
}

export default cart
