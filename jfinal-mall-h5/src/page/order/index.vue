<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar" />
    <!-- 地址 -->
    <router-link :to="'/addresslist?u=ordinaryorder?goodId='+goodId+'&type=ordinaryorder'">
      <Address :address="ordinaryorder.consignee" />
    </router-link>
    <!-- 商品列表 -->
    <div class="shopBox" :style="{'padding-bottom':coupon.length > 0 ?'8rem' : '4.5rem'}">
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
      <!-- 是否使用积分购买的按钮 -->
      <div
        class="shopList order-switch"
        v-if="pointUse.pointRemain > 0 && ordinaryorder.totalPointAsMoney > 0"
      >
        <hr />
        <van-row>
          <van-col span="24" class="switch-box">
            <span v-if="!pointSwitch">
              可用积分
              <i>{{pointUse.pointRemain}}</i>
            </span>
            <span v-else>
              抵扣金额
              <i>{{pointUse.userPointRemain}}</i>
            </span>
          </van-col>
          <van-col span="24" class="switch-box">
            <van-switch
              v-model="pointSwitch"
              size="24px"
              active-color="#07c160"
              inactive-color="#ee0a24"
              @change="pointSwitchFn"
            />
          </van-col>
        </van-row>
      </div>
    </div>

    <!-- 提交订单 -->
    <van-submit-bar
      :loading="loadingActive"
      :price="ordinaryorder.totalPrice"
      button-text="提交订单"
      @submit="onSubmit"
    >
      <!-- 平台优惠券 -->
      <div class="coupon" slot="top" v-if="coupon.length > 0">
        <!-- 优惠券 -->
        <div class="left">
          <Coupon :coupon="coupon" type="1" />
        </div>
        <!-- 显示优惠券信息 -->
        <div class="right" @click="seeFun({type:'platformCoupon'})">
          <span>满500减5元</span>
          <van-icon name="arrow" color="#999999" class="arrow" />
        </div>
      </div>
    </van-submit-bar>

    <!-- 弹窗 -->
    <yd-popup v-model="devierBox" position="bottom" height="60%" class="popup">
      <!-- 标题 -->
      <div class="title">
        <span>配送方式</span>
        <van-icon name="cross" @click.native="clart" />
      </div>
      <!-- 内容 -->
      <div class="coment">
        <!-- 配送方式 -->
        <van-radio-group v-model="delivery" v-if="activeDelivery">
          <van-radio
            v-for="(item,key) in deliveryArr"
            :name="item.id"
            :key="key"
            checked-color="#D7000F"
          >{{item.name}}</van-radio>
        </van-radio-group>
      </div>
    </yd-popup>
    <!-- 优惠券 -->
    <Coupon :coupon="coupon" type="2" v-if="showList" />
  </div>
</template>

<script>
// 普通下单
import { Toast } from "vant";
import Navbar from "../../components/navbar";
import Coupon from "../../components/coupon";
import From from "./from";
import { mapActions, mapGetters } from "vuex";
import ShopCard from "../../components/shopCard";
import Address from "../../components/address";
import commJs from "../../comm";
import {
  submitOrder,
  appointTimeParam,
  hasPromLockFun,
  goodsDetail
} from "../../../api/goods/";
import { SubOrder as SubOrderApi } from "../../../api/order.js";
import { setTimeout } from "timers";

export default {
  components: {
    Navbar,
    Address,
    ShopCard,
    From,
    Coupon
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
        totalNum: 0,
        totalPointAsMoney: 0,
        totalPrice: 0
      },
      // 提交加载
      loadingActive: false,
      // 品台优惠券
      coupon: [
        // {
        //   money: 5,
        //   width: 3.25,
        //   height: 2.125
        // }
      ],
      // 快递方式弹窗
      devierBox: false,
      // 是否显示优惠券
      showList: false,
      // 配送方式
      // 默认选中第一个
      delivery: 0,
      activeDelivery: false,
      deliveryArr: [],
      // 商品id
      goodId: "",
      pointSwitch: false,
      subOrder: {
        addressId: 0,
        isPoint: 2,
        isCart: 2,
        orderStoreList: []
      }
    };
  },
  methods: {
    // 开关是否使用积分支付的开关
    pointSwitchFn(val) {
      let usePoint = 0;
      if (this.pointUse.pointRemain > this.ordinaryorder.totalPointAsMoney) {
        usePoint = this.ordinaryorder.totalPointAsMoney;
      } else {
        usePoint = this.pointUse.pointRemain;
      }
      this.pointUse.userPointRemain = usePoint;
      // 计算最终抵扣的积分
      usePoint = usePoint * 100;
      // 计算提交表单显示的数据
      if (val) {
        this.ordinaryorder.totalPrice =
          this.ordinaryorder.totalPrice - usePoint;
      } else {
        this.ordinaryorder.totalPrice =
          this.ordinaryorder.totalPrice + usePoint;
      }
      // 提交的表单的赋值
      this.subOrder.isPoint = this.pointSwitch ? 1 : 2;
    },
    // 提交
    onSubmit() {
      Toast.loading({ mask: true, message: "请求中..." });
      let that = this;
      // console.log(data);
      SubOrderApi(this.subOrder).then(res => {
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
                orderInfo: JSON.stringify(res.data),
                activityType: 1
              }
            });
          }, 500);
        } else {
          commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
    },
    // 查看
    seeFun(data) {
      // console.log(data);
      let storeId = data.key;
      let item = data.item;
      let storeGood = this.subOrder.orderStoreList.filter(
        os => os.storeId === storeId
      );
      if (storeGood.length !== 1) {
        throw Error("参数异常");
      }
      let sg = storeGood[0];
      if (item.type == "delivery") {
        // 配送方式
        this.devierBox = !this.devierBox;
        this.activeDelivery = true;
        sg.delivery = item.vModel;
        this.deliveryArr = item.selectedData;
      } else if (item.type == "input") {
        // 留言
        sg.userNote = item.value;
      }
    },
    // 关闭弹窗
    clart() {
      this.devierBox = false;
      this.activeDelivery = false;
    },
    // 开启优惠券弹窗
    platformCoupon() {
      let objs = document.getElementById("van-coupon-list");
      if (objs) {
        // 先清除
        this.showList = false;
        // 再开启
        setTimeout(() => {
          this.showList = true;
        }, 10);
      } else {
        this.showList = !this.showList;
      }
    },
    initSubOrder() {
      // 将session中的订单信息转换为对应的对象
      if (sessionStorage.getItem("ordinaryorder")) {
        this.ordinaryorder = Object.assign(
          this.ordinaryorder,
          JSON.parse(sessionStorage.getItem("ordinaryorder"))
        );
        this.ordinaryorder.totalPrice = this.ordinaryorder.totalPrice * 100;
        // 生成subOrder
        this.subOrder.addressId = this.ordinaryorder.consignee.addressId;
        this.subOrder.isCart = parseInt(sessionStorage.getItem("isCart"));
        this.subOrder.orderStoreList = this.ordinaryorder.orderStoreList.map(
          storeGood => {
            return {
              storeId: storeGood.storeId,
              delivery: 0,
              userNote: "",
              orderGoodList: storeGood.orderGood.map(g => {
                return {
                  buyNum: g.goodNum,
                  skuId: g.skuId
                };
              })
            };
          }
        );
      }
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
    if (this.$route.query.goodId) {
      // 商品详情
      this.goodId = this.$route.query.goodId;
      this.navbar.left[0].to = "/goodsinfo?goodId=" + this.goodId;
    } else {
      // 购物车
      this.navbar.left[0].to = "/cart";
    }
    this.initSubOrder();
  }
};
</script>
<style lang="less" scoped>
.shopList {
  margin-bottom: 0.9375rem;
}
/deep/.van-submit-bar__bar {
  /deep/ .van-submit-bar__button {
    line-height: 34px;
    height: 34px;
    background: #d7000f;
    border-radius: 18px;
  }
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
  height: 4.5rem;
  background-color: white;
  hr {
    border: none;
    margin-left: 1em;
    margin-right: 1em;
    height: 1px;
    background-color: #e5e5e5;
  }
  .switch-box {
    padding-top: 0.8rem;
    color: #666666;
    font-size: 0.8125rem;
    text-align: right;
    padding-right: 0.9375rem;
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
  /deep/ .yd-popup-content {
    // padding:.9375rem;
  }
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
</style>
