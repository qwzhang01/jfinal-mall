<template>
  <div class="wallet">
    <!-- 头部 -->
    <Navbar :navbar="navbar"/>
    <!-- 内容 -->
    <div
      class="coment"
      :style="{background:'url('+require('../../assets/back_1.png')+') no-repeat center'}"
    >
      <!-- left -->
      <div class="left">
        <div>可用余额（元）</div>
        <div>{{balance}}</div>
        <div>
          冻结金额（元）
          <span>{{frozenRMB}}</span>
        </div>
      </div>
      <!-- right -->
      <router-link to="/walletlist?type=3&title=明细">
        <div class="right">
          <span>查看明细</span>
          <yd-navbar-next-icon></yd-navbar-next-icon>
        </div>
      </router-link>
    </div>
    <!-- 操作按钮 -->
    <div class="operation">
      <van-button type="default" @click="jump()">提现</van-button>
      <router-link to="/recharge">
        <van-button type="danger">充值</van-button>
      </router-link>
    </div>
    <!-- 列表 -->
    <yd-cell-group class="record">
      <router-link to="/walletlist?type=1&title=充值记录">
        <yd-cell-item arrow>
          <span slot="left">
            <img src="../../assets/recharge.png" alt="充值记录">
            <span>充值记录</span>
          </span>
          <span slot="right">去查看</span>
        </yd-cell-item>
      </router-link>
      <router-link to="/walletlist?type=2&title=提现记录">
        <yd-cell-item arrow>
          <span slot="left">
            <img src="../../assets/withdraw _cash.png" alt="提现记录">
            <span>提现记录</span>
          </span>
          <span slot="right">去查看</span>
        </yd-cell-item>
      </router-link>
    </yd-cell-group>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import comm_js from "../../comm/";
import { BalanceDetail } from "../../../api/";
export default {
  components: {
    Navbar
  },
  data() {
    return {
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        background: "#D7000F",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/myself",
            color: "#ffffff"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "我的钱包",
          //颜色
          color: "#ffffff"
        }
      },
      // 余额
      balance: "",
      // 冻结金额
      frozenRMB: ""
    };
  },
  methods: {
    // 跳转
    jump() {
      // 是否认证
      this.$router.push("/authentication");
    },
    // 获取钱包详情
    BalanceDetailFun() {
      let that = this;
      let params = {
        userId: sessionStorage.getItem("user_id")
      };
      BalanceDetail(params).then(res => {
        if (res.status == 0) {
          // 余额
          that.balance = comm_js.abs(res.data.availableAmount, 2);
          // 冻结金额
          that.frozenRMB = comm_js.abs(res.data.frozenAmount, 2);
        }
      });
    }
  },
  mounted() {
    this.BalanceDetailFun();
    console.log(window.location.href);
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.wallet {
  background: #ffffff;
}
// 内容

.coment {
  display: flex;
  justify-content: space-between;
  color: #ffffff;
  background-size: 100% 100%;
  height: 13.375rem;
  .left {
    margin-left: 0.9375rem;
    div:nth-child(1) {
      margin-top: 1.5625rem;
      font-size: 0.75rem;
    }
    div:nth-child(2) {
      font-size: 1.875rem;
      margin-top: 0.625rem;
      margin-bottom: 0.875rem;
      font-family: DIN;
      font-weight: bold;
    }
    div:nth-child(3) {
      font-size: 0.75rem;
      span {
        margin-left: 0.5rem;
      }
    }
  }
  .right {
    width: 5.25rem;
    height: 1.875rem;
    border-top-left-radius: 2rem;
    border-bottom-left-radius: 2rem;
    background: #f4242a;
    display: flex;
    align-items: center;
    margin-top: 3.5rem;
    padding-left: 0.8rem;
    /deep/ i {
      color: #ffffff;
      font-size: 1.5625rem;
      margin-left: 0.2rem;
    }
  }
}

// 操作
.operation {
  position: relative;
  bottom: 1.5rem;
  display: flex;
  justify-content: space-around;
  button {
    width: 8.125rem;
    height: 2.75rem;
    font-size: 1rem;
    border-radius: 1.375rem;
    button:nth-child(1) {
      color: #999999;
    }
    button:nth-child(2) {
      background: #fb1d2c;
      color: #ffffff;
    }
  }
}
// 列表
/deep/ .yd-cell {
  align-items: center;
  font-size: 1rem;
  img {
    width: 1.125rem;
    height: 1.125rem;
    position: relative;
    top: 0.2rem;
    margin-right: 0.3rem;
  }
  /deep/ .yd-cell-right {
    color: #999999;
  }
}
/deep/ .yd-navbar:after {
  display: none;
}
/deep/ .yd-cell-item {
  border-bottom: 1px solid #ebedf0;
}
.record {
  margin-top: 2rem;
}
</style>

