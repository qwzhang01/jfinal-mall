<template>
  <div>
    <div class="layout">
      <div v-for="(item,key) in config[orderType].from" :key="key" class="view" @click="see(item)">
        <!-- 标题 -->
        <div class="left">{{item.lable}}</div>

        <!-- 判断类型 -->
        <div class="right">
          <!-- 数字 -->
          <van-stepper
            v-if="item.type == 'spinner'"
            v-model="item.value"
            integer
            :min="1"
            :max="storeCount"
            :step="1"
          />
          <!-- 输入框 -->
          <yd-input
            v-model="item.value"
            :show-error-icon="false"
            :show-success-icon="false"
            :show-required-icon="false"
            show-clear-icon
            max="40"
            :placeholder="'请输入'+ item.lable+'(最多40字)'"
            v-else-if="item.type == 'input'"
            @input="see(item)"
          ></yd-input>
          <!-- 滚动选择 -->
          <!-- v-else-if="item.type == 'picker'" -->

          <span v-else>{{item.value}}</span>
          <!-- 标签 -->
          <van-icon :name="item.icon" v-if="item.icon" color="#999999" class="icon" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// 订单列表
import commJs from "../../comm";
import config from "./config.js";
export default {
  props: ["orderType", "goodData", "index", "installationTime"],
  data() {
    return {
      config,
      // 店铺索引
      key: 0,
      // 库存数量
      storeCount: 0
    };
  },
  methods: {
    see(item) {
      this.$emit("see", {
        key: this.key,
        item
      });
    },
    dataFun() {
      let that = this;
      if (that.goodData) {
        that.config[that.orderType].from = that.config[that.orderType].from.map(
          item => {
            // 店铺索引
            that.key = that.goodData.item.storeId;
            // 配送方式
            if (item.type == "delivery") {
              // 送锁
              if (that.orderType == 4) {
                item.value = that.goodData.sendRecord.sendWay[that.index];
              } else {
                item.selectedData = that.goodData.item.expressList;
                item.vModel = that.goodData.item.expressList[0].id;
                item.value = that.goodData.item.expressList[0].name;
              }
            }
            return item;
          }
        );
      }
    }
  },
  mounted() {
    // console.log(this.goodData);
    setTimeout(() => {
      this.dataFun(this.index);
    }, 450);
  },
  watch: {
    index(newV, oldV) {
      this.dataFun();
    },
    installationTime(newV, oldV) {
      this.dataFun();
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.layout {
  font-size: 0.875rem;
  background: #ffffff;
  .view {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.8rem 0rem 0.8rem 0;
    margin-left: 0.9375rem;
    border-bottom: 1px solid #e6e6e6;
    .left {
      color: #333333;
      font-family: PingFang-SC-Medium;
    }
    .right {
      display: flex;
      align-items: center;
      width: 60%;
      justify-content: flex-end;
      /deep/ .yd-input > input {
        text-align: right;
      }
    }
    .icon {
      margin-left: 0.2rem;
    }
  }
}
/deep/ .yd-spinner-square {
  width: 5rem !important;
  height: 1rem !important;
}
// .view:nth-last-child(1){
//   border: none;
// }
/deep/ .yd-spinner-square:after,
/deep/ .yd-spinner-square:before {
  content: none;
}
</style>
