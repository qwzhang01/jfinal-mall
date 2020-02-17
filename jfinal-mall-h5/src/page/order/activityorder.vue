<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar" />
    <!-- 地址 -->
    <router-link :to="'/addresslist?u=activityorder?goodId='+goodId+'&type=ordinaryorder'">
      <Address :address="ordinaryorder.consignee" />
    </router-link>
    <!-- 一元抢购 -->
    <div v-if="activityType == 3" class="rob_shop">
      <div class="title">
        <img src="../../assets/shop_1.png" alt="iocn" /> 正在参加抽奖活动，支付即可成功参与
      </div>
      <div class="userList">
        <div class="userTitle userTitle2">参与人员：</div>
        <div class="users">
          <div class="headPic">
            <img
              v-lazy="item.headPic"
              alt="参与人员"
              v-for="(item,key) in ordinaryorder.userList"
              :key="key"
              :class="[key > 0?'headPic_1':'']"
              :style="{'left': '-'+key/2+'rem' }"
            />
            <img
              src="../../assets/haeder_icon.png"
              alt="参与人员"
              class="headPic_1"
              :style="{'left': '-'+ (ordinaryorder.userList? ordinaryorder.userList.length : 0) / 2+'rem' }"
            />
          </div>
          <div class="userTitle">还剩{{absentNum}}人开奖</div>
        </div>
      </div>
    </div>
    <!-- 商品列表 -->
    <div
      class="shopBox"
      :style="{'padding-bottom':coupon.length > 0 ?shopBoxStyle+'rem' : '4.5rem'}"
    >
      <div class="shopList" v-for="(item,key) in ordinaryorder.orderStoreList" :key="key">
        <!-- 商品信息 -->
        <ShopCard :order="item" />
        <!-- 小计 -->
        <div class="subtotal">
          <!-- 列表 orderType：1、普通订单 2、抢购 3、秒杀-->
          <From :orderType="activityType" @see="seeFun" :goodData="{key,item}" />
          <div class="godsNumber">共计 {{item.sumNum}} 件商品</div>
          <div class="min_sum">
            小计：
            <span>
              ￥{{item.sumPrice}}
              <i
                style="font-size: 0.575rem"
                v-if="ordinaryorder.totalPointAsMoney > 0"
              >+ {{ordinaryorder.totalPointAsMoney}}积分</i>
            </span>
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
    <!-- 优惠券 -->
    <Coupon :coupon="coupon" type="2" v-if="showList" />
  </div>
</template>

<script>
import { Toast } from "vant";
import Navbar from "../../components/navbar";
import Coupon from "../../components/coupon";
import From from "./from";
import ShopCard from "../../components/shopCard";
import Address from "../../components/address";
import commJs from "../../comm";
import { submitOrder } from "../../../api/goods/";
import { SubFlashOrder } from "../../../api/order.js";
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
      // 商品类型 2秒杀 3拼团 4优惠券
      activityType: "",
      // 还差多少人开奖
      absentNum: 0,

      // 订单数据
      ordinaryorder: {
        // 收货信息
        consignee: {},
        // 商品信息，以一个店铺为准
        orderStoreList: [],
        totalNum: 0,
        totalPointAsMoney: 0,
        totalPrice: 0,
        // 这个要去掉
        comfirmOrderList: []
      },
      // 提交加载
      loadingActive: false,
      // 订单box样式
      shopBoxStyle: 8,
      // 品台优惠券
      coupon: [
        // {
        //   money:5,
        //   width:3.25,
        //   height:2.125
        // }
      ],
      showList: false,
      // 配送方式
      devierBox: false,
      // 配送方式对应的表单对象
      formItem: {},
      delivery: 0,
      userNote: "",
      deliveryArr: [],
      // 商品id
      goodId: "",
      flashGoodId: "",
      lotteryGoodId: ""
    };
  },
  methods: {
    // 初始化订单商品信息
    initGood() {
      if (sessionStorage.getItem("ordinaryorder")) {
        this.ordinaryorder = Object.assign(
          this.ordinaryorder,
          JSON.parse(sessionStorage.getItem("ordinaryorder"))
        );
      }
      // 合计价格
      this.ordinaryorder.totalPrice = this.ordinaryorder.totalPrice * 100;
    },
    // 查看
    seeFun(data) {
      // 判断类型
      // console.log(data);
      // 优惠券
      if (data.item.type == "coupon") {
        this.platformCoupon();
      } else if (data.item.type == "platformCoupon") {
        this.platformCoupon();
      }
      // 配送方式
      else if (data.item.type == "delivery") {
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
    onSubmit() {
      Toast.loading({ mask: true, message: "请求中..." });
      let that = this;
      // 商品参数
      let data = {
        flashGoodId: this.flashGoodId,
        addressId: this.ordinaryorder.consignee.addressId,
        delivery: this.delivery,
        userNote: this.userNote
      };
      SubFlashOrder(data).then(res => {
        Toast.clear();
        if (res.status == 0) {
          commJs.Dialog2(res.msg, "success", 1000, that);
          setTimeout(() => {
            // 现在的剩余支付时间，精确到秒, 秒杀30秒以内不支付，自动取消订单
            let time = Date.parse(new Date()) / 1000 + 30;
            let dd = commJs.dateHandle(time, "/");
            sessionStorage.setItem("paytime", dd);
            that.$router.push({
              path: "/payment",
              query: {
                orderInfo: JSON.stringify(res.data),
                activityType: that.activityType
              }
            });
          }, 500);
        } else {
          // commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
    }
  },
  mounted() {
    if (this.$route.query.goodId && this.$route.query.flashGoodId) {
      this.activityType = 2;
      this.goodId = this.$route.query.goodId;
      this.flashGoodId = this.$route.query.flashGoodId;
      this.navbar.left[0].to =
        "/goodsinfo?goodId=" +
        this.goodId +
        "&flashGoodId=" +
        this.$route.query.flashGoodId;
    } else if (this.$route.query.goodId && this.$route.query.lotteryGoodId) {
      this.activityType = 2;
      this.goodId = this.$route.query.goodId;
      this.lotteryGoodId = this.$route.query.lotteryGoodId;
      this.navbar.left[0].to =
        "/goodsinfo?goodId=" +
        this.goodId +
        "&lotteryGoodId=" +
        this.$route.query.lotteryGoodId;
    } else if (this.$route.query.goodId) {
      // 商品详情
      this.activityType = 1;
      this.goodId = this.$route.query.goodId;
      this.navbar.left[0].to = "/goodsinfo?goodId=" + this.goodId;
    } else {
      // 购物车
      this.activityType = 1;
      this.navbar.left[0].to = "/cart";
    }
    //  抢购拼团
    if (sessionStorage.getItem("lotteryGoodId")) {
      this.activityType = 3;
      this.absentNum = sessionStorage.getItem("absentNum")
        ? sessionStorage.getItem("absentNum")
        : 0;
    }
    this.initGood();
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

