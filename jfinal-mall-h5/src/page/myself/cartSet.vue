<template>
  <div>
    <!-- 头部导航 -->
    <Navbar :navbar="navbar" />
    <div v-if="isSet">
      <!-- 列表 -->
      <van-row class="success">
        <div class="icon-img">
          <img src="../../assets/Success.png" />
        </div>
        <div class="tips">
          <span>您已经设置提现银行卡</span>
        </div>
      </van-row>

      <van-cell-group>
        <van-cell title="姓名" :value="realname" />
        <van-cell title="开户支行" :value="bank_card" />
        <van-cell title="银行卡号" :value="bank_name" />
      </van-cell-group>
      <div class="bottom">
        <yd-button size="large" type="danger" @click.native="reset">重新设置</yd-button>
      </div>
    </div>
    <div v-else>
      <van-cell-group>
        <van-field
          v-model="realname"
          clearable
          label="真实姓名"
          icon="question-o"
          placeholder="请输入真实姓名"
        />
        <van-field
          v-model="bank_name"
          clearable
          label="开户行支行"
          icon="question-o"
          placeholder="请输入开户行支行"
        />
        <van-field
          v-model="bank_card"
          clearable
          label="银行卡号"
          icon="question-o"
          placeholder="请输入银行卡号"
        />
      </van-cell-group>
      <div class="bottom">
        <yd-button size="large" type="danger" @click.native="save">保存</yd-button>
      </div>
    </div>
  </div>
</template>

<script>
import { Toast } from "vant";
import Navbar from "../../components/navbar";
import comm_js from "../../comm/";
import { BindBank } from "../../../api/";
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
            to: "/set",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "提现设置",
          //颜色
          color: "#333333"
        }
      },
      isSet: false,
      realname: "",
      bank_card: "",
      bank_name: ""
    };
  },
  methods: {
    reset() {
      this.isSet = false;
    },
    save() {
      let that = this;
      if (this.bank_name && this.bank_card) {
        BindBank({
          realname: this.realname,
          bank_name: this.bank_name,
          bank_card: this.bank_card
        }).then(res => {
          if (res.status === 0) {
            that.isSet = true;
            that.$router.push({
              path: "/cartSet",
              query: {
                bank_name: that.bank_name,
                bank_card: that.bank_card,
                realname: that.realname,
                u: "set"
              }
            });
          }
        });
      } else {
        this.$toast("请填写开户行支行以及银行卡号");
      }
    },
    initPage() {
      if (this.$route.query.bank_card) {
        this.bank_card = this.$route.query.bank_card;
      }
      if (this.$route.query.bank_name) {
        this.bank_name = this.$route.query.bank_name;
      }
      if (this.$route.query.realname) {
        this.realname = this.$route.query.realname;
      }
      if (this.realname && this.bank_name && this.bank_card) {
        this.isSet = true;
      }
    }
  },
  mounted() {
    this.initPage();
  }
};
</script>

<style lang="less" scoped>
.success {
  width: 100%;
  height: 12rem;
  background: rgba(255, 255, 255, 1);
  text-align: center;
  margin-bottom: 1rem;
  img {
    margin: 1.8rem 0 1rem 0;
    width: 4rem;
    display: inline-block;
    vertical-align: middle;
  }
  .tips {
    font-size: 1.1rem;
    font-family: PingFang SC;
    font-weight: 500;
    color: rgba(51, 51, 51, 1);
  }
}
.bottom {
  padding: 0 0.9375rem;
  font-family: PingFang SC;
  font-size: 1rem;
}
/deep/ .yd-btn-danger:not(.yd-btn-loading) {
  background: #d7000f;
}
</style>

