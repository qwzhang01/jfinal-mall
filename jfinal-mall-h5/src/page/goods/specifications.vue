<template>
  <div class="boxNorms" id="boxNorms">
    <van-sku
      ref="sku"
      v-model="showBase"
      :sku="getGood.sku"
      :goods="getGood.goods_info"
      :goods-id="getGood.goods_id"
      :hide-stock="false"
      :hide-quota-text="false"
      :close-on-click-overlay="true"
      :show-add-cart-btn="false"
      :stock-threshold="50"
      :quota="getGood.quota"
      :quota-used="getGood.quota_used"
      :custom-stepper-config="customStepperConfig"
      buy-text="确定"
      reset-stepper-on-hide
      reset-selected-sku-on-hide
      :message-config="messageConfig"
      :custom-sku-validator="customSkuValidator"
      @buy-clicked="onBuyClicked"
    />
  </div>
</template>

<script>
/**
 * 商品规格选择
 * 注意：
 */
import { mapActions, mapGetters } from "vuex";
export default {
  props: ["actionButon"],
  data() {
    return {
      showBase: true,
      // initialSku: {
      //   s1: "30349",
      //   s2: "1193",
      //   selectedNum: 3
      // },
      customSkuValidator: () => "请选择规格后购买",
      customStepperConfig: {
        // quotaText: "单次限购100000件",
        stockFormatter: stock => `剩余${stock}件`,
        handleOverLimit: data => {
          const { action, limitType, quota, quotaUsed } = data;
          if (action === "minus") {
            this.$toast("至少选择一件商品");
          } else if (action === "plus") {
            if (limitType === LIMIT_TYPE.QUOTA_LIMIT) {
              this.$toast(`限购${quota}件`);
            } else {
              this.$toast("库存不够了");
            }
          }
        },
        handleStepperChange: currentValue => {}
      },

      messageConfig: {
        uploadImg: (file, img) =>
          new Promise(resolve => {
            setTimeout(() => resolve(img), 1000);
          }),
        uploadMaxSize: 3
      }
    };
  },
  computed: {
    ...mapGetters(["getGood"])
  },
  methods: {
    onBuyClicked(data) {
      console.log(this.actionButon);
      if ("buy" === this.actionButon) {
        // 立即购买
        this.$emit("BuyClicked", data);
      }
      if ("cart" === this.actionButon) {
        // 加入购物车
        this.$emit("AddCartClicked", data);
      }
    }
  },
  mounted() {
    // console.log(this.getGood);
  },
  watch: {}
};
</script>

<style lang="less" scoped>
/deep/ .van-sku-actions {
  height: 50px;
  button {
    line-height: 34px;
    height: 36px;
  }
  :first-child {
    margin-left: 5px;
    margin-right: 5px;
    border-top-left-radius: 18px;
    border-bottom-left-radius: 18px;
    border-top-right-radius: 18px;
    border-bottom-right-radius: 18px;
  }
}
/deep/ .van-stepper__input {
  color: #333333 !important;
}
/deep/ .van-hairline--bottom::after {
  border: none;
}
</style>

