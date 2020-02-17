<template>
  <div>
    <!-- 头部导航 -->
    <Navbar :navbar="navbar" />
    <div>
      <!-- 顶部 -->
      <van-popup v-model="show" position="top" :overlay="false">
        <div class="popup">
          <img src="../../assets/aert.png" alt="icon" />
          <span>为保证账户安全，请先完成身份验证</span>
        </div>
      </van-popup>
      <!-- 手机号 -->
      <div class="mobile">当前手机号：{{phone}}</div>
      <yd-cell-group>
        <yd-cell-item>
          <input type="number" slot="right" v-model="code" @input="change" placeholder="请输入验证码" />
          <!-- ↓↓关键代码是这里↓↓ -->
          <yd-sendcode slot="right" v-model="start1" @click.native="sendCode1" type="warning"></yd-sendcode>
          <!-- ↑↑关键代码是这里↑↑ -->
        </yd-cell-item>
      </yd-cell-group>
      <!-- 下一步 -->
      <div class="bottom">
        <yd-button size="large" type="danger" :disabled="disabled" @click.native="Authentication">认证</yd-button>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import { Dialog } from "vant";
import comm_js from "../../comm/";
import { Sms, Apply } from "../../../api/";
import { setTimeout } from "timers";
import { mapActions, mapGetters } from "vuex";

export default {
  components: {
    Navbar
  },
  data() {
    return {
      // 是否开启
      disabled: true,
      start1: false,
      show: true,
      // 验证码
      code: "",
      // 电话号码
      phone: "",
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/cashwithdrawal",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "身份验证",
          //颜色
          color: "#333333"
        }
      }
    };
  },
  methods: {
    // 顶部弹窗
    showFun() {
      let that = this;
      setTimeout(() => {
        that.show = false;
      }, 3000);
    },
    // 验证码
    sendCode1() {
      this.$dialog.loading.open("发送中...");
      setTimeout(() => {
        this.SmsFun();
      }, 1000);
    },
    // 请求
    SmsFun() {
      let that = this;
      let params = {
        phone: that.getUser.mobile
      };
      Sms(params).then(res => {
        if (res.status == 0) {
          comm_js.Dialog2("已发送", "success", 1500, this);
          that.start1 = true;
          that.$dialog.loading.close();
        }
      });
    },

    // 监听
    change() {
      if (this.code) {
        this.disabled = false;
      } else {
        this.disabled = true;
      }
    },
    // 提交认证
    Authentication() {
      //校验验证码
      // if(!this.start1){
      //    comm_js.Dialog2('请发送验证码','',1500,this);
      //   return;
      // }
      if (!this.disabled) {
        this.ApplyFun();
      }
    },
    // 请求
    ApplyFun() {
      let that = this;
      let user = JSON.parse(sessionStorage.getItem("user_card"));
      let params = {
        userId: sessionStorage.getItem("user_id"),
        name: user.username,
        bankName: user.opening_bank,
        bankCard: user.bank_card,
        phoneCaptcha: this.code,
        amount: user.amount
      };
      Apply(params).then(res => {
        if (res.status == 0) {
          comm_js.Dialog2("验证成功,已提交！", "success", 1200, that);
          setTimeout(() => {
            that.$router.push("/wallet");
          }, 1300);
        } else {
          comm_js.Dialog2(res.msg, "error", 1200, that);
        }
      });
    }
  },
  computed: {
    ...mapGetters(["getUser"])
  },
  mounted() {
    this.showFun();
    this.phone = this.getUser.mobile ? this.getUser.mobile : "";
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.popup {
  width: 100%;
  height: 2.75rem;
  color: #d7000f;
  background: #f7e1e2;
  display: flex;
  font-size: 0.9375rem;
  font-family: PingFang SC;
  align-items: center;
  justify-content: center;
  img {
    width: 0.875rem;
    height: 0.875rem;
    margin-right: 0.5rem;
  }
}
/deep/ .yd-btn-danger:not(.yd-btn-loading) {
  background: #d7000f;
}
.mobile {
  height: 3.25rem;
  color: #333333;
  font-size: 1rem;
  margin-left: 0.9375rem;
  display: flex;
  align-items: center;
}

/deep/ .yd-cell-item {
  height: 3.25rem;
  color: #333333;
  font-size: 1rem;
}
/deep/ .yd-btn {
  background: #ffffff;
  color: #333333;
  font-family: PingFang SC;
}
/deep/ .yd-btn-warning:not(.yd-btn-loading):active {
  background: #ffffff !important;
}
/deep/ .yd-btn-warning:not(.yd-btn-loading) {
  background: #ffffff !important;
}
/deep/ .yd-btn-disabled {
  color: #999999;
}
.bottom {
  padding: 0 0.9375rem;
  font-family: PingFang SC;
  font-size: 1rem;
}
/deep/ .van-dialog__confirm .van-button__text {
  color: #d7000f !important;
}
</style>

