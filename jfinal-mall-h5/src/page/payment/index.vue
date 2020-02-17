<template>
  <div class="payment">
    <!-- 导航 -->
    <Navbar :navbar="navbar" />
    <van-row v-if="activityType === 2">
      <van-col :span="24" class="tips">
        <span>此订单为秒杀订单，未支付则不会进入您的订单记录</span>
      </van-col>
    </van-row>
    <!-- 支付金额信息 -->
    <div class="moneyInfo">
      <div class="title">支付金额</div>
      <div class="money">
        <span v-if="orderInfo.realPayPrice && orderInfo.realPayPrice > 0">￥</span>
        <span v-if="orderInfo.realPayPrice && orderInfo.realPayPrice > 0">{{orderInfo.realPayPrice}}</span>
        <span v-if="orderInfo.realPayPrice && orderInfo.pointAsMoney > 0">+</span>
        <span
          v-if="orderInfo.realPayPrice && orderInfo.pointAsMoney > 0"
        >{{orderInfo.pointAsMoney}}积分</span>
      </div>
      <div class="time">
        <div class="masterOrderSn">订单号：{{orderInfo.masterOrderSn}}</div>
        <div>
          剩余支付时间：
          <yd-countdown :time="payTime" format="{%h}：{%m}：{%s}" :callback="oerver"></yd-countdown>
        </div>
      </div>
    </div>
    <!-- 支付方式 -->
    <div class="pay_box">
      <div class="title">请选择支付方式</div>
      <div class="pay" v-for="(item,key) in pay" :key="key" @click="payType = key">
        <div class="left">
          <div class="icon">
            <img :src="require('../../assets/'+item.iocn+'.png')" alt="支付图片" />
          </div>
          <div class="pay_coment">
            <div>{{item.name}}</div>
            <div>{{item.explain}}</div>
          </div>
        </div>
        <div class="right">
          <img
            :src="key == payType ? require('../../assets/select.png') : require('../../assets/no_select.png')"
            alt="支付按钮"
          />
        </div>
      </div>
    </div>
    <!-- 支付 -->
    <div class="payBtn">
      <yd-button
        size="large"
        type="hollow"
        :bgcolor="bgcolor"
        :disabled="disabled"
        @click.native="payBtn"
      >{{paycoment}}</yd-button>
    </div>
  </div>
</template>

<script>
// 支付
import commJs from "../../comm";
import Navbar from "../../components/navbar";
import { Notify } from "vant";
export default {
  components: {
    Navbar
  },
  data() {
    return {
      activityType: 1,
      // 支付标题
      paycoment: "支付",
      // 是否禁用
      disabled: false,
      // 支付按钮背景颜色
      bgcolor: "#D7000F",
      // 选择支付类型
      paymentType: "1",
      // 选择的类型
      payType: 0,
      wx_science: commJs.isWeiXin(),
      // 剩余支付时间
      payTime: "",
      // 支付信息
      orderInfo: {
        masterOrderSn: ""
      },
      // 支付方式
      pay: [
        {
          type: "wx",
          name: "微信支付",
          explain: "推荐已在微信中绑定银行卡的用户使用",
          iocn: "wx"
        },
        {
          type: "zfb",
          name: "支付宝支付",
          explain: "快捷安全，可支持银行卡支付",
          iocn: "zhi"
        }
      ],
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/orderlist?tabl=0",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "支付中心",
          //颜色
          color: "#333333"
        },
        right: []
      }
    };
  },
  methods: {
    // 支付
    payBtn() {
      console.log(this.payType);
      let jump_url = "/orderlist?tabl=0";
      if (this.payType == 0) {
        console.log("微信");
        // 判断是否在微信环境中
        if (this.wx_science) {
          // 公众号
          commJs.WxGzLanuch(
            sessionStorage.getItem("userId"),
            this.orderInfo.realPayPrice,
            this.orderInfo.masterOrderSn,
            jump_url,
            this
          );
        } else {
          // h5
          commJs.WxLanuch(
            sessionStorage.getItem("userId"),
            this.orderInfo.realPayPrice,
            this.orderInfo.masterOrderSn,
            jump_url,
            this
          );
        }
      } else if (this.payType == 1) {
        commJs.AliAppLanuch(
          sessionStorage.getItem("userId"),
          this.orderInfo.realPayPrice,
          this.orderInfo.masterOrderSn,
          jump_url,
          this
        );
      }
    },
    // 时间到，未支付
    oerver() {
      this.paycoment = "订单已失效";
      this.disabled = true;
      this.bgcolor = "#cccccc";
    }
  },
  mounted() {
    // 判断是否是微信环境
    if (this.wx_science) {
      // 删除支付宝
      this.pay.splice(1, 1);
    }
    // 判断是否是有订单信息
    if (this.$route.query.orderInfo) {
      this.orderInfo = Object.assign(
        this.orderInfo,
        JSON.parse(this.$route.query.orderInfo)
      );
      // console.log(this.orderInfo);
      if (this.orderInfo) {
        if (this.orderInfo.realPayPrice === 0) {
          commJs.Dialog2("秒杀成功", "success", 1000, this);
          this.$router.push({
            path: "/orderlist?tabl=2"
          });
        } else {
          this.payTime = sessionStorage.getItem("paytime");
        }
      }
    }
    if (this.$route.query.activityType) {
      this.activityType = parseInt(this.$route.query.activityType);
      // Notify({
      //   message: "此订单为秒杀订单，未支付则不会进入您的订单记录",
      //   duration: 3000,
      //   background: "#D7000F"
      // });
    }
  }
};
</script>

<style lang="less" scoped>
.tips {
  box-sizing: border-box;
  padding: 6px 15px;
  font-size: 14px;
  line-height: 20px;
  white-space: pre-wrap;
  text-align: center;
  color: rgb(255, 255, 255);
  background: rgb(215, 0, 15);
}
.moneyInfo {
  background: #ffffff;
  text-align: center;
  color: #333333;
  padding: 1.25rem 0;
  font-family: PingFang-SC-Medium;
  .title {
    font-size: 0.9375rem;
    padding-bottom: 1.5625rem;
  }
  .money {
    color: #d7000f;
    span:nth-child(1) {
      font-size: 0.7rem;
    }
    span:nth-child(2) {
      font-size: 1.2rem;
    }
  }
  .time {
    font-size: 0.9375rem;
    margin-top: 1.25rem;
  }
}
.pay_box {
  margin-top: 0.9375rem;
  background: #ffffff;
  padding: 0 0.9375rem;
  .title {
    color: #999999;
    font-family: PingFang-SC-Medium;
    font-size: 0.8125rem;
    padding: 0.9375rem 0;
  }
  .pay {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0.9375rem 0;
    border-bottom: 1px solid #e6e6e6;
    .left {
      display: flex;
      align-items: center;
      .icon {
        img {
          width: 1.8125rem;
          height: 1.8125rem;
          margin-top: 0.2rem;
        }
      }
      .pay_coment {
        margin-left: 0.5rem;
        div:nth-child(1) {
          padding-bottom: 0.2rem;
        }
        div:nth-child(2) {
          color: #999999;
        }
      }
    }
    .right {
      img {
        width: 1.375rem;
        height: 1.375rem;
      }
    }
  }
}

.pay:nth-last-child(1) {
  border: none;
}
.payBtn {
  position: fixed;
  width: 100%;
  bottom: 0;
  button {
    background: #d7000f;
    margin-top: 0;
    font-family: PingFang-SC-Medium;
    font-size: 1rem;
    height: 2.8125rem;
    color: #ffffff;
  }
}
.masterOrderSn {
  margin-bottom: 0.3rem;
}
</style>

