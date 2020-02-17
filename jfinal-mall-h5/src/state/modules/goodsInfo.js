/* jshint esversion: 6 */
const cart = {
  state: {
    // 商品详情
    goodsData: {}
  },
  mutations: {
    // 更改商品详情
    GOODSDATAFUN(state, newData) {


      let goods = {}
      // 商品Id
      goods.goods_id = newData.goods_id
      // 限购件数(库存)
      goods.quota = newData.store_count
      goods.goods_info = {
        // 商品标题
        title: newData.goods_name,
        // 商品图片
        picture: newData.original_img,
        // 商品价格
        price: newData.shop_price,
        origin: ''
      }
      goods.sku = {
        // 所有sku规格类目与其值的从属关系，比如商品有颜色和尺码两大类规格，颜色下面又有红色和蓝色两个规格值。
        // 可以理解为一个商品可以有多个规格类目，一个规格类目下可以有多个规格值。
        tree: newData.goods_spec_list.map(r => {
          let item = {}
          item.k = r.specName
          item.k_id = r.specList[0].specId
          item.k_s = 's' + item.k_id
          item.count = r.specList.length
          item.v = r.specList.map(i => {
            let val = {}
            val.id = i.itemId
            val.name = i.itemName
            return val
          })
          return item
        }),
        // 所有 sku 的组合列表，比如红色、M 码为一个 sku 组合，红色、S 码为另一个组合
        list: newData.spec_goods_price.filter(sku => sku.disAbled).map(sku => {
          let r = {
            id: sku.item_id,
            price: sku.price * 100,
            stock_num: sku.store_count,
            spec_img: 'http://file.sdbjgj.com/' + sku.spec_img,
            goods_id: sku.goods_id
          }
          if (sku.key && newData.goods_spec_list.length > 0) {
            sku.key.split("_").forEach(i => {
              let specItem = []
              newData.goods_spec_list.forEach(element => {
                specItem = specItem.concat(element.specList)
              })
              let item = specItem.filter(s => s.itemId === parseInt(i))[0]
              r["s" + item.specId] = item.itemId
            })
          }
          return r
        }),

        // 默认价格（单位元）
        price: newData.shop_price,
        // 商品总库存
        stock_num: newData.spec_goods_price[0].store_count,
        collection_id: 2261,
        collection_price: 0,
        none_sku: false,
        sold_num: 0,
        min_price: '1.00',
        max_price: '1.00',
        // 留言
        messages: [],
        // 是否隐藏剩余库存
        hide_stock: newData.activityType > 1 && newData.activityActive === 1 ? true : false
      }
      // 已购买过的数量
      goods.quota_used = 0
      state.goodsData = goods
    }
  },
  actions: {},
  getters: {}
}

export default cart
