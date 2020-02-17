<template>
  <div>
    <!-- 头部导航 -->
    <Navbar :navbar="navbar" />
    <!-- 列表 -->
    <div>
      <van-cell-group>
        <!--  right-icon="question-o" @click-right-icon="$toast('question')" -->
        <van-field
          v-model="username"
          :clearable="true"
          label="姓名"
          placeholder="请输入真实姓名"
          @input="change"
        />
        <van-field
          v-model="opening_bank"
          :clearable="true"
          label="开户行支行"
          placeholder="请输入具体到支行开户行"
          @input="change"
        />
        <van-field
          v-model="bank_card"
          :clearable="true"
          label="银行卡号"
          placeholder="请输入银行卡号"
          type="Number"
          @input="change"
        />
      </van-cell-group>
      <div class="bottom">
        <yd-button size="large" type="danger" @click.native="submitFun" :disabled="disabled">下一步</yd-button>
      </div>
    </div>
  </div>
</template>

<script>
import { Toast } from "vant";
import Navbar from "../../../components/navbar";
import comm_js from "../../../comm/";
export default {
  components: {
    Navbar
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
            to: "/retail",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "提现",
          //颜色
          color: "#333333"
        }
      },

      // 姓名
      username: "",
      // 开户行
      opening_bank: "",
      // 银行卡
      bank_card: "",
      // 是否开启
      disabled: true
    };
  },
  methods: {
    change() {
      if (this.username && this.opening_bank && this.bank_card) {
        this.disabled = false;
      } else {
        this.disabled = true;
      }
    },
    submitFun() {
      // 校验

      // 用户名
      if (comm_js.test(this.username, "用户名", [1], this) == false) {
        return;
      }
      // 卡号
      if (comm_js.test(this.bank_card, "卡号", [1, 2], this) == false) {
        return;
      }
      // 卡号长度
      if (this.bank_card.length <= 16) {
        this.$dialog.toast({
          mes: "银行卡位数不能小于16位",
          timeout: 1500,
          icon: "error"
        });
        return;
      }
      // 开户行
      if (comm_js.test(this.opening_bank, "开户行", [1], this) == false) {
        return;
      }
      if (!this.disabled) {
        // 储存
        sessionStorage.setItem(
          "user_card",
          JSON.stringify({
            username: this.username,
            bank_card: this.bank_card,
            opening_bank: this.opening_bank
          })
        );

        this.$router.push("/retail/withdraw/cashwithdrawal");
      }
    }
  },
  mounted() {
    // 判断登录，如果没有登录，跳转登录
    if (comm_js.testLogin(this, "/retail", false) == false) {
      return;
    }
    this.change();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.bottom {
  padding: 0 0.9375rem;
  font-family: PingFang SC;
  font-size: 1rem;
}
/deep/ .yd-btn-danger:not(.yd-btn-loading) {
  background: #d7000f;
}
</style>

