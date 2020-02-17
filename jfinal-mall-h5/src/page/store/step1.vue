<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar"/>

    <!-- 表单 -->
    <div class="fromSubmint">
      <!-- 联系人 -->
      <!-- <van-cell-group>
        <van-field
          v-model="from.user"
          clearable
          label="联系人"
          type="text"
          @input="change"
          placeholder="请输入联系人姓名"
        />
      </van-cell-group>-->
      <!-- 手机号 -->
      <van-cell-group>
        <van-field
          v-model="from.mobile"
          clearable
          label="联系电话"
          type="number"
          @input="change"
          readonly
          placeholder="请填写手机号码"
        />
      </van-cell-group>
      <!-- 验证码 -->
      <van-cell-group>
        <van-field
          v-model="from.smsCode"
          @input="change"
          label="验证码"
          center
          clearable
          placeholder="请输入短信验证码"
        >
          <van-button
            slot="button"
            size="small"
            :disabled="smsactive"
            type="default"
            @click="smsFun"
          >{{comentSms}}</van-button>
        </van-field>
      </van-cell-group>

      <!-- 按钮 -->
      <van-button
        type="danger"
        size="large"
        :disabled="disabled"
        style="background:#D7000F;"
        @click.native="submit"
        class="submit"
      >确定</van-button>
    </div>
  </div>
</template>
<script>
import Navbar from "../../components/navbar";
import comm from "../../comm/";
import { userInfo, applyVerify, Sms } from "../../../api/store/";
import { mapActions, mapGetters } from "vuex";
import commJs from "../../comm/";
export default {
  components: {
    Navbar
  },
  data() {
    return {
      // 验证码状态
      smsactive: false,
      comentSms: "发送验证码",
      second: 60,
      // 提交
      disabled: true,
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/myself",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "我要开店",
          //颜色
          color: "#333333"
        },
        right: []
      },
      from: {
        mobile: "",
        smsCode: "",
        user: ""
      }
    };
  },
  methods: {
    ...mapActions(["stepFun"]),
    // 提交
    submit() {
      let that = this;
      // 联系人
      // if (comm.test(this.from.user, "联系人", [1], this) == false) {
      //   return;
      // }
      // 判断电话号码
      if (comm.test(this.from.mobile, "电话号码", [1, 3], this) == false) {
        return;
      }
      // 存入缓存
      let data = {
        mobile: this.from.mobile,
        user: this.from.user
      };
      applyVerify({ smsCode: this.from.smsCode }).then(res => {
        if (res.status == 0) {
          comm.Dialog2(res.msg, "success", 1500, that);
          setTimeout(() => {
            that.stepFun(data);
            that.$router.push({
              path: "/step2"
            });
          }, 1600);
        } else {
          comm.Dialog2(res.msg, "error", 1500, that);
        }
      });
    },
    // 发送验证码
    smsFun() {
      let that = this;
      // 判断电话号码
      if (comm.test(this.from.mobile, "电话号码", [1, 3], this) == false) {
        return;
      }
      Sms({ phone: this.from.mobile }).then(res => {
        if (res.status == 0) {
          that.second = 60;
          that.setTime();
        }
        comm.Dialog2(res.msg, "success", 1000, that);
      });
    },
    // 监听
    change() {
      if (this.from.mobile && this.from.smsCode) {
        this.disabled = false;
      } else {
        this.disabled = true;
      }
    },
    // 验证码
    setTime() {
      var interId = setInterval(() => {
        // 大于1
        if (this.second > 1) {
          this.second--;
          this.smsactive = true;
          this.comentSms = `${this.second}s 后再发送`;
        } else {
          // 清除定时器
          clearInterval(interId);
          this.smsactive = false;
          this.comentSms = "发送验证码";
        }
      }, 1000);
    },
    userInfoFun() {
      let that = this;
      userInfo().then(res => {
        if (res.status == 0) {
          that.from.mobile = res.data.mobile;
          that.change();
        } else {
          console.log(res.msg);
        }
      });
    }
  },
  computed: {
    ...mapGetters(["getStep"])
  },
  mounted() {
    this.from = Object.assign(this.from, this.getStep.step1);
    // 判断是否登录
    if (commJs.testLogin(this, "/myself") == false) {
      return;
    }
    this.userInfoFun();
  }
};
</script>

<style lang="less" scoped>
/deep/ .van-cell-group ::-webkit-input-placeholder {
  color: #999999;
  font-family: PingFang SC;
}

/deep/ .van-cell-group {
  color: #333333;
  font-family: PingFang SC;
  font-size: 1rem;
  border-bottom: 1px solid #ebedf0;
}
#group {
  background: #ffffff;
}
/deep/ .van-button::before {
  content: none;
}
/deep/ .van-button--default {
  border: none;
}
/deep/ .van-cell {
  padding: 15px 0px;
  line-height: inherit;
}

[class*="van-hairline"]::after {
  content: none;
}

.from .van-button,
.submit {
  height: 2.8125rem;
  font-size: 1rem;
  font-family: PingFang SC;
  border-radius: 0.25rem;
}
// /deep/.van-button--small {
//   height: 0;
// }

.submit {
  margin-top: 2.375rem;
}
.fromSubmint {
  padding: 2.8125rem 0.9375rem 0 0.9375rem;
  background: #ffffff;
  height: -webkit-fill-available;
}
/deep/ .van-field__label {
  width: 4.375rem;
}
</style>

