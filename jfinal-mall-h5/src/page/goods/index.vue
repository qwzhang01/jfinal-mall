<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar" />
    <!-- 地址 -->
    <router-link :to="'/addresslist?u=ordinaryorder?goodId='+goodId+'&type=ordinaryorder'">
      <Address :address="address" />
    </router-link>
    <!-- 商品列表 -->
    <div
      class="shopBox"
      :style="{'margin-bottom':coupon.length > 0 ?shopBoxStyle+'rem' : '3.5rem'}"
    >
      <div class="shopList" v-for="(item,key) in ordinaryorder.comfirmOrderList" :key="key">
        <!-- 商品信息 -->
        <ShopCard :order="item" />
        <!-- 小计 -->
        <div class="subtotal">
          <!-- 列表 orderType：1、普通订单 2、抢购 3、秒杀-->
          <From orderType="1" @see="seeFun" :goodData="{key,item}" />
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
    <yd-popup v-model="show2" position="bottom" height="60%" class="popup">
      <!-- 标题 -->
      <div class="title">
        <span>{{showTitle}}</span>
        <van-icon name="cross" @click.native="clart" />
      </div>
      <!-- 内容 -->
      <div class="coment">
        <!-- 配送方式 -->
        <van-radio-group v-model="delivery" v-if="activeDelivery">
          <van-radio
            :name="item.value"
            v-for="(item,key) in deliveryArr"
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
import ShopCard from "../../components/shopCard";
import Address from "../../components/address";
import commJs from "../../comm";
import { submitOrder } from "../../../api/goods/";
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
      // 订单数据
      ordinaryorder: {
        address: ""
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
      // 地址
      address: { address: "" },
      order: {
        storeName: "",
        storeId: "",
        orderGood: [
          {
            goodNum: "",
            finalPrice: "",
            specName: "",
            goodName: "",
            goodPrice: "",
            imgPath: ""
          }
        ]
      },
      //弹窗
      showTitle: "配送方式",
      show2: false,
      showList: false,
      // 配送方式
      // 默认选中第一个
      delivery: 0,
      activeDelivery: false,
      deliveryArr: [],
      // 商品id
      goodId: "",
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
      }
    };
  },
  methods: {
    // 提交
    onSubmit() {
      Toast.loading({ mask: true, message: "请求中..." });
      let that = this;
      // console.log(this.ordinaryorder);
      // 商品参数
      this.ordinaryorder.comfirmOrderList.map((items, keys) => {
        items.submitGoodsList = items.orderGood;
        // delete items.orderGood;
        // delete items.list;
      });

      let data = {
        isPoint: 1, // TODO 这里根据客户选择进行赋值，是否使用积分支付 1是2否
        addressId: this.ordinaryorder.addressId,
        address: this.ordinaryorder.address,
        //(用户)余额支付金额
        userMoneyPay: 0,
        totalPrice: this.ordinaryorder.totalPrice / 100,
        //实付款金额
        realPayPrice: this.ordinaryorder.totalPrice / 100,
        //满5000送智能状态(1活动进行, 0活动关闭)
        activeStatus: 0,
        submitOrderList: this.ordinaryorder.comfirmOrderList
      };
      // console.log(data);
      submitOrder(data).then(res => {
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
        } else {
          commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
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
        this.show2 = !this.show2;
        this.activeDelivery = true;
        this.deliveryArr = this.ordinaryorder.comfirmOrderList[
          data.key
        ].sendWay.map((item, key) => {
          return { value: key, name: item };
        });
        this.showTitle = "配送方式";
      }
      // 留言
      else if (data.item.type == "input") {
        // console.log(data.item);
        this.ordinaryorder.comfirmOrderList[data.key].userNote =
          data.item.value;
      }

      let ordinaryorder = this.ordinaryorder;
      this.ordinaryorder = [];
      this.ordinaryorder = ordinaryorder;
      this.handleData();
    },
    // 关闭弹窗
    clart() {
      this.show2 = false;
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
    // 商品处理
    handleData() {
      this.order = this.ordinaryorder.comfirmOrderList.map(items => {
        items.orderGood = items.list.map(item => {
          item.goodName = item.goodsName;
          item.goodNum = item.goodsNum;
          item.finalPrice = item.goodsPrice;
          item.specName = item.specKeyName;
          item.imgPath = item.originalImg;

          items.userNote = items.userNote ? items.userNote : ""; // 留言
          items.sumPrice = item.goodsPrice * item.goodsNum;
          items.sumNum = item.goodsNum;
          (item.flashGoodId = 0),
            //是否需要上门安装(1需要, 0不需要)
            (item.doorServiceStatus = 0),
            //安装地址id
            (item.doorAddressId = 0),
            (item.doorStartTime = ""),
            (item.doorEndTime = "");
          //规格ID
          (item.specKey = 0),
            // 抢购
            (item.activityNum = 0),
            (item.cartId = 0);
          return item;
        });
      });
      // 合计价格
      this.moeny();
    },
    // 合计价格
    moeny() {
      let totalMoeny = 0;
      this.ordinaryorder.comfirmOrderList.map(items => {
        totalMoeny = totalMoeny + items.sumPrice;
      });
      this.ordinaryorder.totalPrice = totalMoeny * 100;
    }
  },
  mounted() {
    if (this.$route.query.goodId) {
      // 商品详情
      this.goodId = this.$route.query.goodId;
      this.navbar.left[0].to = "/goodsinfo?goodId=" + this.goodId;
    } else {
      // 购物车
    }
    if (sessionStorage.getItem("ordinaryorder")) {
      this.ordinaryorder = Object.assign(
        this.ordinaryorder,
        JSON.parse(sessionStorage.getItem("ordinaryorder"))
      );
      console.log(this.ordinaryorder);
      if (sessionStorage.getItem("consignee")) {
        let address = JSON.parse(sessionStorage.getItem("consignee"));
        this.ordinaryorder.address = address.address;
        this.ordinaryorder.addressId = address.address_id;
        this.ordinaryorder.mobile = address.mobile;
        this.ordinaryorder.consignee = address.nickname;
      }
      // 地址
      this.address = Object.assign(this.address, {
        address: this.ordinaryorder.address,
        address_id: this.ordinaryorder.addressId,
        mobile: this.ordinaryorder.mobile,
        nickname: this.ordinaryorder.consignee
      });
      this.handleData();
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
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
</style>

