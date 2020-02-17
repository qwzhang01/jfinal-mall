const cart = {
  state: {
    // 商品详情
    activeId: 0,
    activeKey: 0
  },
  mutations: {
    // 更改商品详情
    SELECTCLASSFUN (state, newData) {
      state.activeId = newData.activeId
      state.activeKey = newData.key
    }
  },
  actions: {},
  getters: {}
}

export default cart
