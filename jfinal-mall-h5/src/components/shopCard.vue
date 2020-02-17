<template>
  <div>
    <div class="shop_title">
      <div class="stroe">
        <!-- 选择 默认关闭-->
        <van-checkbox
          v-model="order.checked"
          checked-color="#D7000F"
          v-if="order.hasOwnProperty('checked')"
          @click="checkedFatherStroe(fatherIndex)"
        ></van-checkbox>
        <!-- 店铺 -->
        <router-link :to="'/storepage?store_id='+ order.storeId">
          <van-icon name="shop-o" size="1rem" />
          <span>{{order.storeName}}</span>
          <van-icon name="arrow" size=".9375rem" color="#999999" />
        </router-link>
      </div>
      <div v-if="seller== false?false:true">
        <van-icon name="chat-o" size=".9375rem" class="wact" color="red" />
        <span>联系卖家</span>
      </div>
    </div>

    <div v-for="(items,index) in order.orderGood" :key="index">
      <div class="card_box">
        <!-- 选择商品 -->
        <div class="checkbox" v-if="items.hasOwnProperty('checked')">
          <van-checkbox
            v-model="items.checked"
            checked-color="#D7000F"
            @click.stop="checkedStroe(fatherIndex,index)"
          ></van-checkbox>
        </div>
        <div class="card">
          <div class="card_lfet" @click.stop="introduce(items)">
            <img :src="items.imgPath" />
          </div>
          <div class="card_fight">
            <div class="card_fight_top">
              <div
                class="goodname"
                style="
              display: -webkit-box;
              -webkit-box-orient: vertical;
              overflow : hidden;
              display: -webkit-box;
              -webkit-line-clamp: 2;"
              >{{items.goodName}}</div>
              <div class="goodPrice">
                <!-- 老版本先用这个 -->
                <span v-if="items.finalPrice">￥{{items.finalPrice}}</span>
                <!-- 以后只用这个 -->
                <span v-else>￥{{items.goodPrice}}</span>
              </div>
            </div>
            <div class="card_fight_bottom">
              <div
                class="specName"
                style="
              display: -webkit-box;
              -webkit-box-orient: vertical;
              overflow : hidden;
              display: -webkit-box;
              -webkit-line-clamp: 2;"
                :style="{
                'width': items.hasOwnProperty('checked')?'53%':'80%',
              }"
              >{{items.specName}}</div>
              <div v-if="!items.hasOwnProperty('checked')">x{{items.goodNum}}</div>
              <!-- 计算 -->
              <div v-else>
                <van-stepper
                  v-model="items.goodNum"
                  integer
                  :min="1"
                  :max="items.storeCount"
                  :step="1"
                  @plus="plus(items.id,1)"
                  @minus="minus(items.id,0)"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 订单详情 -->
      <div class="operationOder" v-if="order.hasOwnProperty('slot')">
        <!-- 操作类型 1：取消订单 2：去支付 3：提醒发货 4：申请退款 5：查看物流 6：确定收货 7：去评价   -->
        <!-- <van-button size="mini" @click.stop="operation(item,4)" v-if="order.status == 4">退款</van-button> -->
      </div>
      <!-- <slot :item="{key:index, data:items}"></slot> -->
    </div>
  </div>
</template>

<script>
/**
 * 商品卡片
 */
export default {
  props: ["order", "fatherIndex", "seller"],
  data() {
    return {};
  },
  methods: {
    jump(mun) {
      this.$emit("jump", "store");
    },
    /**
     * 点击跳转
     */
    introduce(items) {
      this.$emit("introduceSelf", items);
    },

    // 选择店铺
    checkedFatherStroe(fatherIndex) {
      this.$emit("checkedFatherStroe", { index: fatherIndex });
    },

    // 选择商品
    checkedStroe(fatherIndex, index) {
      this.$emit("checkedStroe", { fatherIndex, index });
    },
    //点击增加
    plus(id, type) {
      this.$emit("calculation", { id, type });
    },
    //点击减少
    minus(id, type) {
      this.$emit("calculation", { id, type });
    }
  },
  watch: {},
  mounted() {
    console.log(this.order);
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.shop_title {
  display: flex;
  justify-content: space-between;
  padding: 0 0.9375rem;
  height: 2.5rem;
  align-items: center;
  background: #ffffff;
  font-size: 0.8rem;
  i {
    position: relative;
    top: 0.18rem;
  }
  .wact {
    top: 0.1rem;
  }
  .stroe {
    display: flex;
    /deep/ .van-checkbox {
      margin-right: 0.8125rem;
    }
  }
}
/deep/ .van-cell:not(:last-child)::after {
  content: none;
}
/deep/ .van-card {
  background-color: #ffffff;
  padding: 0;
  margin-top: 0;
}
/deep/ .van-card__header {
  background-color: #fafafa;
  height: 6.25rem;
  align-items: center;
  padding: 0 0.9375rem;
}

/deep/ .van-card__thumb {
  width: 5rem;
  height: 5rem;
}
/deep/ .van-cell {
  padding: 0 0.9375rem;
  height: 2.5rem;
  line-height: 2.6rem;
}
/deep/ .van-cell__left-icon {
  margin-top: 0.45rem;
}

/deep/ .van-card__content {
  display: flex;
  justify-content: center;
}

.style_div {
  display: flex;
}
</style>

