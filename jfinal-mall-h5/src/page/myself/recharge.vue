<template>
  <div>
    <!-- 头部导航 -->
    <Navbar :navbar="navbar"/>
    <!-- 充值 -->
    <div class="withdrawal">
      <div class="title">充值金额</div>
      <div class="input">
        <span>￥</span>
        <yd-input
          type="number"
          :show-clear-icon="true"
          :show-error-icon="false"
          :show-success-icon="false"
          :show-required-icon="false"
          v-model="money"
          max="10"
          placeholder="请输入金额"
        ></yd-input>
      </div>
    </div>
    <!-- 充值方式 -->
    <div class="recharge">
      <div class="title">请选择充值方式</div>
      <van-radio-group v-model="radio">
        <van-cell-group>
          <van-cell
            title="微信充值"
            :icon="require('../../assets/wx.png')"
            clickable
            @click="radio = 1"
          >
            <van-radio name="1" checked-color="#D7000F"/>
          </van-cell>

          <van-cell
            v-if="iswx"
            title="支付宝充值"
            :icon="require('../../assets/zhi.png')"
            clickable
            @click="radio = 2"
          >
            <van-radio name="2" checked-color="#D7000F"/>
          </van-cell>
        </van-cell-group>
      </van-radio-group>
    </div>
    <!-- 确认提现 -->
    <div class="bottom">
      <yd-button
        size="large"
        type="danger"
        style="background: #D7000F;"
        @click.native="moneyFun"
      >确认充值</yd-button>
    </div>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import { setTimeout } from "timers";
import { Dialog } from "vant";
import comm_js from "../../comm/";
import {
  // 充值
  Charge
} from "../../../api/";
export default {
  components: {
    Navbar
  },
  data() {
    return {
      // 钱
      money: "",
      radio: "1",
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/wallet",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "充值",
          //颜色
          color: "#333333"
        }
      },
      // 判断是否在微信浏览器中
      iswx: !comm_js.isWeiXin()
    };
  },
  methods: {
    // 提现
    moneyFun() {
      if (!this.money || this.money == 0) {
        comm_js.Dialog2("请输入充值金额", "error", 1500, this);
        return;
      }
      console.log(this.money >= 0.01);
      if (this.money < 0.01) {
        comm_js.Dialog2("充值金额最低为0.01元", "error", 1500, this);
        return;
      }
      this.ChargeFun();

      // Dialog.alert({
      //     title: '充值成功！',
      //   }).then(() => {
      //     // on close
      //   });
      // setTimeout(()=>{
      //     this.$router.push("/wallet");
      // },2000)
    },
    // 请求
    ChargeFun() {
      let that = this;
      let params = {
        userId: sessionStorage.getItem("user_id")
          ? sessionStorage.getItem("user_id")
          : "",
        amount: this.money
      };
      Charge(params).then(res => {
        if (res.status == 0) {
          // 跳转地址
          let jump_url = comm_js.host.jump_domain + "/#/recharge?active=1";
          if (parseInt(this.radio) == 1) {
            // 在微信环境中
            if (comm_js.isWeiXin()) {
              let jump_url = "/wallet";
              comm_js.WxGzLanuch(
                res.data.user_id,
                res.data.account,
                res.data.order_sn,
                jump_url,
                this
              );
              console.log(res);
              return;
            }
            // h5 环境中
            comm_js.WxLanuch(
              res.data.user_id,
              res.data.account,
              res.data.order_sn,
              jump_url,
              that
            );
          } else {
            // 支付宝
            comm_js.AliAppLanuch(
              res.data.user_id,
              res.data.account,
              res.data.order_sn,
              jump_url,
              that
            );
          }
          console.log(res);
        }
      });
    }
  },
  mounted() {
    if (this.$route.params.active) {
      comm_js.Dialog("是否已支付？", "", Dialog).then(res => {
        this.$router.push("/wallet");
      });
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
// 提现
.withdrawal {
  height: 4.5625rem;
  background: #ffffff;

  padding: 0 0.9375rem;
  .title {
    color: #333333;
    font-size: 0.6875rem;
    font-family: PingFang SC;
    padding-top: 0.9375rem;
  }
  .input {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 0.2rem;
    margin-bottom: 0.2rem;
    span {
      width: 0.6875rem;
      margin-right: 0.2rem;
      margin-top: 0.2rem;
    }
    height: 2rem;
    /deep/ input {
      height: 2rem;
      font-size: 1rem;
      font-family: DIN;
    }
  }
}
// 充值
.recharge {
  margin-top: 0.625rem;
  background: #ffffff;
  .title {
    margin-left: 0.9375rem;
    color: #999999;
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 2rem;
  }
}
// 提交
.bottom {
  padding: 0 0.9375rem;
  font-family: PingFang SC;
  font-size: 1rem;
}
/deep/ .van-dialog__confirm .van-button__text {
  color: #d7000f !important;
}
</style>

