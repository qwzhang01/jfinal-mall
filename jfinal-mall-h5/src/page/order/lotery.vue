<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar" />
    <!-- 地址 -->
    <router-link :to="'/addresslist?u=loteryorder?goodId='+goodId+'&type=ordinaryorder'">
      <Address :address="ordinaryorder.consignee" />
    </router-link>
    <div class="rob_shop">
      <div class="title">
        <img src="../../assets/shop_1.png" alt="iocn" /> 正在参加抽奖活动，支付即可成功参与
      </div>
      <div class="userList">
        <div class="userTitle userTitle2">参与人员：</div>
        <div class="users">
          <div class="headPic">
            <img
              v-lazy="item"
              alt="参与人员"
              v-for="(item,key) in ordinaryorder.lotteryUser.headPic"
              :key="key"
              :class="[key > 0?'headPic_1':'']"
              :style="{'left': '-'+key/2+'rem' }"
            />
            <img
              src="../../assets/haeder_icon.png"
              alt="参与人员"
              class="headPic_1"
              :style="{'left': '-'+ (ordinaryorder.lotteryUser.headPic? ordinaryorder.lotteryUser.headPic.length : 0) / 2+'rem' }"
            />
          </div>
          <div class="userTitle">还剩{{ordinaryorder.lotteryUser.absentNum}}人开奖</div>
        </div>
      </div>
    </div>
    <!-- 商品列表 -->
    <div class="shopBox" :style="{'padding-bottom':'4.5rem'}">
      <div v-for="(item,key) in ordinaryorder.orderStoreList" :key="key">
        <!-- 商品信息 -->
        <ShopCard :order="item" :seller="false" />

        <!-- 小计 -->
        <div class="subtotal">
          <!-- 列表 orderType：1、普通订单 2、抢购 3、秒杀-->
          <From orderType="1" @see="seeFun" :goodData="{key,item}" :index="delivery" />
          <div class="godsNumber">共计 {{item.sumNum}} 件商品</div>
          <div class="min_sum">
            小计：
            <span>￥{{item.sumPrice}}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 提交订单 -->
    <van-submit-bar
      :loading="loadingActive"
      :price="ordinaryorder.totalPrice"
      button-text="提交订单"
      @submit="onSubmit"
    ></van-submit-bar>

    <!-- 弹窗 -->
    <yd-popup v-model="devierBox" position="bottom" height="60%" class="popup">
      <!-- 标题 -->
      <div class="title">
        <span>配送方式</span>
        <van-icon name="cross" @click.native="clart" />
      </div>
      <!-- 内容 -->
      <div class="coment">
        <van-radio-group v-model="delivery">
          <van-radio
            v-for="(item,key) in deliveryArr"
            :name="item.id"
            :key="key"
            checked-color="#D7000F"
          >{{item.name}}</van-radio>
        </van-radio-group>
      </div>
    </yd-popup>
  </div>
</template>

<script>
// 普通下单
import { Toast } from "vant";
import Navbar from "../../components/navbar";
import From from "./from";
import ShopCard from "../../components/shopCard";
import Address from "../../components/address";
import commJs from "../../comm";
import { mapActions, mapGetters } from "vuex";
import { SubLotteryOrder } from "../../../api/order.js";
import { setTimeout } from "timers";

export default {
  components: {
    Navbar,
    Address,
    ShopCard,
    From
  },
  data() {
    return {
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/goodsinfo",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "确认订单",
          //颜色
          color: "#333333"
        },
        right: []
      },
      // 订单数据
      ordinaryorder: {
        consignee: {},
        orderStoreList: [],
        // 参与人员信息
        lotteryUser: {},
        totalNum: 0,
        totalPointAsMoney: 0,
        totalPrice: 0
      },
      // 提交加载
      loadingActive: false,
      devierBox: false,
      // 配送方式
      delivery: 0,
      userNote: "",
      formItem: {},
      deliveryArr: [],
      // 商品id
      goodId: "",
      lotteryGoodId: ""
    };
  },
  methods: {
    // 提交
    onSubmit() {
      Toast.loading({ mask: true, message: "请求中..." });
      let that = this;
      let data = {
        delivery: that.delivery,
        userNote: that.userNote,
        addressId: that.ordinaryorder.consignee.addressId,
        lotteryGoodId: that.lotteryGoodId
      };
      SubLotteryOrder(data).then(res => {
        Toast.clear();
        if (res.status == 0) {
          commJs.Dialog2(res.msg, "success", 1000, that);
          setTimeout(() => {
            // 现在的剩余支付时间，精确到秒
            let time = Date.parse(new Date()) / 1000 + 3600 * 3;
            let dd = commJs.dateHandle(time, "/");
            sessionStorage.setItem("paytime", dd);
            this.$router.push({
              path: "/payment",
              query: {
                orderInfo: JSON.stringify(res.data)
              }
            });
          }, 500);
        }
      });
    },
    seeFun(data) {
      // 判断类型
      // 配送方式
      if (data.item.type == "delivery") {
        this.devierBox = !this.devierBox;
        this.formItem = data.item;
        this.delivery = data.item.vModel;
        this.deliveryArr = data.item.selectedData;
      }
      // 留言
      else if (data.item.type == "input") {
        this.userNote = data.item.value;
      }
    },
    // 关闭弹窗
    clart() {
      this.devierBox = false;
      this.formItem.vModel = this.delivery;
      this.formItem.value = this.deliveryArr.filter(
        s => s.id === this.formItem.vModel
      )[0].name;
    }
  },
  computed: {
    ...mapGetters(["getUser"]),
    pointUse: function() {
      let pointUse = {
        // 可用积分
        pointRemain: 0,
        // 抵扣金额
        userPointRemain: 0
      };
      pointUse.pointRemain = this.getUser.usableRemain;
      return pointUse;
    }
  },
  mounted() {
    // 商品详情
    this.goodId = this.$route.query.goodId;
    this.lotteryGoodId = this.$route.query.lotteryGoodId;
    this.navbar.left[0].to =
      "/goodsinfo?goodId=" +
      this.goodId +
      "&lotteryGoodId=" +
      this.lotteryGoodId;

    if (sessionStorage.getItem("ordinaryorder")) {
      this.ordinaryorder = Object.assign(
        this.ordinaryorder,
        JSON.parse(sessionStorage.getItem("ordinaryorder"))
      );
      this.ordinaryorder.totalPrice = this.ordinaryorder.totalPrice * 100;
    }
  }
};
</script>

<style lang="less" scoped>
.shopList {
  margin-bottom: 0.9375rem;
}

// 小计
.subtotal {
  text-align: right;
  background: #ffffff;
  padding-right: 0.9375rem;
  .godsNumber {
    padding-top: 0.8rem;
    color: #666666;
    font-size: 0.8125rem;
  }
  .min_sum {
    color: #333333;
    font-size: 0.875rem;
    padding: 0.6rem 0 0.8rem 0;
    span {
      color: #d7000f !important;
    }
  }
}
.order-switch {
  height: 4em;
  background-color: white;
  hr {
    border: none;
    margin-left: 1em;
    margin-right: 1em;
    height: 1px;
    background-color: #e5e5e5;
  }
  .yd-flexbox {
    margin-top: 1em;
    .yd-flexbox-item {
      text-align: right;
      input {
        margin-right: 10%;
        float: right;
      }
      span {
        color: #d7000f;
      }
    }
  }
}
.address {
  margin-top: 0 !important;
}
// 提交
.coupon {
  background: #ffffff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0.5rem 0.9375rem;
  display: flex;
  .left {
    width: 40%;
  }
  .right {
    width: 60%;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    font-size: 0.875rem;
    color: #333333;
    font-family: PingFang-SC-Medium;
    span {
      margin-right: 0.2rem;
    }
  }
}
// 弹窗
.popup {
  .title {
    position: relative;
    font-size: 1rem;
    color: #333333;
    border-bottom: 1px solid #e6e6e6;
    font-family: PingFang-SC-Bold;
    text-align: center;
    padding: 0.8rem;
    /deep/.van-icon-cross {
      position: absolute;
      right: 0.8rem;
      bottom: 1rem;
    }
  }
  .coment {
    padding: 1rem 0.875rem;
    /deep/ .van-radio__label {
      font-size: 0.875rem;
    }
    .van-radio {
      margin-bottom: 0.8rem;
    }
  }
}
.back_picker {
  position: fixed;
  bottom: 0;
  width: 100%;
  background: rgba(51, 51, 51, 0.5);
  z-index: 100;
  height: 100%;
  /deep/ .van-picker {
    position: absolute;
    bottom: 0rem;
    width: 100%;
  }
}
.shopLock {
  /deep/ .layout {
    .view:nth-last-child(1) {
      border: none;
    }
  }
}
/deep/ .van-picker__cancel {
  color: #999999;
}
/deep/ .van-picker__confirm {
  color: #d7000f;
}
// 一元抢购
.rob_shop {
  background: #ffffff;
  margin: 0.625rem 0;
  padding: 0.9375rem;
  .title {
    display: flex;
    align-items: center;
    font-size: 0.875rem;
    color: #333333;
    img {
      width: 0.875rem;
      height: 0.875rem;
      margin-right: 0.3rem;
    }
  }
  .userTitle {
    color: #666666;
    font-size: 0.75rem;
  }
  .userTitle2 {
    padding-top: 0.625rem;
    margin-right: 0.625rem;
  }
  .userList {
    display: flex;
    margin-top: 0.9375rem;
    justify-content: center;
    // align-items:center;
  }
  .users {
    .headPic {
      display: flex;
      margin-bottom: 0.9375rem;
    }
    img {
      display: block;
      width: 2rem;
      height: 2rem;
      border-radius: 45%;
      border: 1px solid #ffffff;
    }
    .headPic_1 {
      position: relative;
      left: -0.625rem;
    }
  }
}
</style>
