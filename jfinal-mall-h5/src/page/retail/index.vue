<template>
  <div>
    <!-- 头部 -->
    <Navbar :navbar="navbar" />
    <div
      class="retail-cont"
      :style="{background:'url('+require('../../assets/back_1.png')+') no-repeat center'}"
    >
      <div class="recont-top">
        <div class="recont-amount">
          <span>积分总收益</span>
          <div>{{staticSum.total_income}}</div>
        </div>
      </div>
      <div class="recont-main">
        <div class="recont-use">
          <span>可用</span>
          <b>{{staticSum.usable_remain}}</b>
        </div>
        <div class="recont-cash">
          <span>可提取</span>
          <b>{{staticSum.withdrawable_remain}}</b>
        </div>
      </div>
      <div class="recont-mbtn">
        <van-button class="btn" type="danger" @click="checkIn">分享送积分</van-button>
      </div>
    </div>
    <div class="retail-fun">
      <div class="refun-cash">
        <div>
          <a href="#" @click="jumpTowithdrow">
            <img src="../../assets/retail/cash.png" alt="提现" />
            <span>提现</span>
          </a>
        </div>
      </div>
      <div class="refun-fri">
        <router-link to="/retail/invite">
          <img src="../../assets/retail/invite.png" alt="邀请好友" />
          <span>邀请好友</span>
        </router-link>
      </div>
    </div>
    <yd-cell-group>
      <router-link to="/customer">
        <yd-cell-item arrow>
          <van-icon slot="icon" name="friends" color="#f4242a" size="1.5rem" />
          <span slot="left">推广用户</span>
          <span slot="right"></span>
        </yd-cell-item>
      </router-link>
      <router-link to="/invest">
        <yd-cell-item arrow>
          <van-icon slot="icon" name="diamond" color="#dc9711" size="1.5rem" />
          <span slot="left">有奖消费</span>
          <span slot="right"></span>
        </yd-cell-item>
      </router-link>
      <router-link to="/expand">
        <yd-cell-item arrow>
          <img slot="icon" style="width:1.5rem;height: 1.5rem" src="../../assets/retail/expand.png" />
          <span slot="left">推广业绩</span>
          <span slot="right"></span>
        </yd-cell-item>
      </router-link>
      <router-link to="/earnings">
        <yd-cell-item arrow>
          <img
            slot="icon"
            style="width:1.5rem;height: 1.5rem"
            src="../../assets/retail/earnings.png"
          />
          <span slot="left">积分明细</span>
          <span slot="right"></span>
        </yd-cell-item>
      </router-link>
      <router-link to="/record">
        <yd-cell-item arrow>
          <img slot="icon" style="width:1.5rem;height: 1.5rem" src="../../assets/retail/cash2.png" />
          <span slot="left">提取记录</span>
          <span slot="right"></span>
        </yd-cell-item>
      </router-link>
    </yd-cell-group>
  </div>
</template>
<script>
import Navbar from "../../components/navbar";
import { getStatis } from "../../../api/retail/";
import commJs from "../../comm";
import { UserDetail } from "../../../api/";

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
          coment: "积分中心",
          // 颜色
          color: "#ffffff"
        }
      },
      staticSum: {
        freeze_remain: 0,
        mine_invest_remain: 0,
        mine_invest_total: 0,
        subline_invest_remain: 0,
        subline_invest_total: 0,
        total_income: 0,
        total_investable: 0,
        usable_remain: 0,
        withdrawable_remain: 0,
        withdrawable_total: 0
      }
    };
  },
  methods: {
    // 获取汇总数据
    getSum() {
      let _this = this;
      getStatis().then(res => {
        _this.staticSum = res.data;
      });
    },
    // 现在改了，分享，分享的商品被点击以后领积分
    checkIn() {
      let path = "/retail/share?text=分享送积分";
      this.$router.push({ path });
    },
    jumpTowithdrow() {
      let that = this;
      let day = new Date().getDay();
      if (0 == day) {
        commJs.Dialog2("周日不可以提现，请周一再申请", "error", 1000, that);
        return;
      }
      let params = {
        userId: sessionStorage.getItem("userId")
          ? sessionStorage.getItem("userId")
          : ""
      };
      UserDetail(params).then(res => {
        if (res.status === 0) {
          let card = res.data.bank_card;
          let bank = res.data.bank_name;
          let realname = res.data.realname;
          if (realname && card && bank) {
            sessionStorage.setItem(
              "user_card",
              JSON.stringify({
                username: realname,
                bank_card: card,
                opening_bank: bank
              })
            );
            let path = "/retail/withdraw/cashwithdrawal";
            this.$router.push({ path });
          } else {
            that.$toast("您没有提现银行卡，请先在设置中绑定");
          }
        }
      });
    },
    // 原来是签到-已经废弃的方法
    checkInDeprecated() {
      let that = this;
      checkInApi().then(res => {
        if (res.status == 0) {
          var active = "success";
          that.getSum();
        } else {
          var active = "error";
        }
        commJs.Dialog2(res.msg, active, 1000, that);
      });
    }
  },
  mounted() {
    this.getSum();
    let u = this.$route.query.u;
    console.log(u);
    if (u) {
      this.navbar.left[0].to = u;
    }
  }
};
</script>

<style lang="less" scoped>
.retail-cont {
  height: 13.375rem;
  color: #fff;
  .recont-top {
    display: flex;
    justify-content: space-between;
    padding-left: 0.9375rem;
    .recont-amount {
      margin-top: 1.5625rem;
      span {
        font-size: 0.75rem;
      }
      div {
        font-size: 1.875rem;
        margin-top: 0.625rem;
        margin-bottom: 0.875rem;
        font-family: DIN;
        font-weight: bold;
      }
    }
    .recont-signin {
      .right {
        width: 4rem;
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
  }
  .recont-main {
    display: flex;
    justify-content: space-between;
    padding-left: 0.9375rem;
    padding-right: 0.9375rem;
    .recont-use {
      font-size: 0.75rem;
      span {
        margin-right: 0.5rem;
      }
    }
    .recont-cash {
      font-size: 0.75rem;
      span {
        margin-right: 0.5rem;
      }
    }
  }
  .recont-mbtn {
    margin-top: 2rem;
    .btn {
      display: block;
      margin: 0 auto;
      width: 8.125rem;
      height: 2.75rem;
      font-size: 1rem;
      border-radius: 1.375rem;
    }
  }
}
.retail-fun {
  display: flex;
  justify-content: space-between;
  padding-top: 1rem;
  height: 5rem;
  .refun-cash {
    width: 50%;
    text-align: center;
    a {
      display: block;
      width: 100%;
      height: 100%;
      img {
        display: block;
        width: 1.5rem;
        height: 1.5rem;
        margin: 0 auto;
      }
      span {
        display: block;
        height: 1rem;
        line-height: 1rem;
        margin-top: 0.4rem;
        text-align: center;
        color: #333;
      }
    }
  }
  .refun-fri {
    width: 50%;
    text-align: center;
    img {
      display: block;
      width: 1.5rem;
      height: 1.5rem;
      margin: 0 auto;
    }
    span {
      display: block;
      height: 1rem;
      line-height: 1rem;
      margin-top: 0.4rem;
      text-align: center;
      color: #333;
    }
  }
}
/deep/ .yd-cell-item {
  border-bottom: 1px solid #ebedf0;
}
/deep/ .yd-navbar:after {
  content: none;
}
</style>
