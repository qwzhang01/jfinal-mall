<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar" @navRight="navRightBtn" />

    <!-- 表单 -->
    <div class="fromSubmint">
      <!-- 标题 -->
      <transition name="slide-fade" v-if="!modify">
        <h5 v-show="titleShow">欢迎来到积分商城</h5>
      </transition>
      <!-- 手机号 -->
      <van-cell-group>
        <van-field
          v-model="from.mobile"
          clearable
          type="number"
          @input="change"
          placeholder="请输入手机号码"
        />
      </van-cell-group>
      <!-- 验证码 -->
      <van-cell-group v-if="!switchactive">
        <van-field v-model="from.smsCode" @input="change" center clearable placeholder="请输入验证码">
          <van-button
            slot="button"
            size="small"
            :disabled="smsactive"
            type="default"
            @click="smsFun"
          >{{comentSms}}</van-button>
        </van-field>
      </van-cell-group>

      <!-- 输入密码 -->
      <van-cell-group v-if="switchactive || modify">
        <van-field
          v-model="from.password"
          :type="password1"
          placeholder="请输入密码"
          :right-icon="password1 == 'password'?'closed-eye':'eye-o'"
          clearable
          @input="change"
          @click-right-icon="transformation(1)"
        />
      </van-cell-group>

      <!-- 再次输入密码 -->
      <van-cell-group v-if="modify">
        <van-field
          v-model="from.password2"
          :type="password2"
          placeholder="请再次输入密码"
          :right-icon="password2 == 'password'?'closed-eye':'eye-o'"
          clearable
          @input="change"
          @click-right-icon="transformation(2)"
        />
      </van-cell-group>

      <!-- 切换 -->
      <div class="switch" v-if="!modify">
        <span @click="switchFun">
          {{!switchactive?'账号密码登录':'短信登录'}}
          <van-icon name="arrow" />
        </span>
        <span @click="modifyFun">忘记密码？</span>
      </div>
      <!-- 按钮 -->
      <van-button
        type="danger"
        size="large"
        :disabled="disabled"
        style="background:#D7000F;"
        @click.native="submit"
        class="submit"
      >{{btnCoemnt}}</van-button>
      <!-- 管家协议 -->
      <div class="checked" v-if="!modify">
        <van-checkbox v-model="from.checked" checked-color="#D7000F" class="checked">
          已阅读并同意
          <span @click.stop="agreement">《积分商城用户协议、隐私政策》</span>
        </van-checkbox>
      </div>
    </div>
  </div>
</template>
<script>
import Navbar from "../../components/navbar";
import comm from "../../comm/";
import { sms, signInPw, signInSmsCode, reSetPw } from "../../../api/";
import { setInterval, setTimeout } from "timers";
export default {
  components: {
    Navbar
  },
  data() {
    return {
      // 短信修改密码
      modify: "",
      titleShow: false,
      // 切换
      switchactive: false,
      // 验证码状态
      smsactive: false,
      comentSms: "获取验证码",
      second: 60,
      // 提交
      disabled: true,
      btnCoemnt: "登录",
      password1: "password",
      password2: "password",
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: sessionStorage.getItem("recordUrl")
              ? sessionStorage.getItem("recordUrl")
              : "/myself",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "短信登录",
          //颜色
          color: "#333333"
        },
        right: [
          {
            coment: "注册",
            to: "",
            color: "#666666"
          }
        ]
      },
      from: {
        mobile: "",
        smsCode: "",
        password: "",
        checked: true
      }
    };
  },
  methods: {
    // 跳转
    navRightBtn(data) {
      if (data.coment == "注册") {
        var Jump = "/register";
      } else if (data.coment == "登录") {
        var Jump = "/login";
        this.navbar.title.coment = !this.switchactive
          ? "短信登录"
          : "账号密码登录";
        this.navbar.left[0].coment = "返回";
        this.modify = "";
      }

      this.$router.push({ path: Jump, query: {} });
    },
    // 提交
    submit() {
      let that = this;
      this.$dialog.loading.open("请求中...");
      //登录
      if (!this.modify) {
        if (!this.from.checked) {
          comm.Dialog2("请同意相关协议", "error", 1000, that);
          return;
        }
        // 验证码登录
        if (!this.switchactive) {
          let params = {
            mobile: this.from.mobile,
            smsCode: this.from.smsCode,
            openId: sessionStorage.getItem("openId")
              ? sessionStorage.getItem("openId")
              : "",
            wxAuthCode: sessionStorage.getItem("wx_auth_code")
              ? sessionStorage.getItem("wx_auth_code")
              : ""
          };
          var Fun = signInSmsCode(params);
        } else {
          // 账号密码
          let params = {
            username: this.from.mobile,
            password: this.from.password,
            openId: sessionStorage.getItem("openId")
              ? sessionStorage.getItem("openId")
              : "",
            wxAuthCode: sessionStorage.getItem("wx_auth_code")
              ? sessionStorage.getItem("wx_auth_code")
              : ""
          };
          var Fun = signInPw(params);
        }
        Fun.then(res => {
          if (res.status == 0) {
            comm.Dialog2(res.msg, "success", 1000, that);
            sessionStorage.setItem(
              "userId",
              res.data.userId ? res.data.userId : ""
            );
            sessionStorage.setItem(
              "token",
              res.data.accessToken ? res.data.accessToken : ""
            );
            sessionStorage.setItem(
              "wx_auth_code",
              res.data.wxAccessToken ? res.data.wxAccessToken : ""
            );
            sessionStorage.setItem(
              "openId",
              res.data.openId ? res.data.openId : ""
            );
            setTimeout(() => {
              that.$router.push({
                path: sessionStorage.getItem("recordUrl")
                  ? sessionStorage.getItem("recordUrl")
                  : "/",
                query: {}
              });
            }, 1000);
          } else {
            comm.Dialog2(res.msg, "error", 1000, that);
          }
          that.$dialog.loading.close();
        });
      } else {
        // 修改密码
        // 检验
        if (this.from.password != this.from.password2) {
          comm.Dialog2("两次输入的密码不一致！", "error", 1000, that);
          return;
        }

        let params = {
          mobile: this.from.mobile,
          smsCode: this.from.smsCode,
          password: this.from.password,
          confirmPassword: this.from.password2
        };
        reSetPw(params).then(res => {
          if (res.status == 0) {
            comm.Dialog2(res.msg, "success", 1000, that);
            setTimeout(() => {
              that.$router.push({
                path: "/myself",
                query: {}
              });
            }, 1000);
          } else {
            comm.Dialog2(res.msg, "error", 1000, that);
          }
          that.$dialog.loading.close();
        });
      }
    },
    // 查看协议
    agreement() {
      console.log("查看协议");
    },
    // 密码显示和转换
    transformation(num) {
      if (num == 1) {
        this.password1 == "password"
          ? (this.password1 = "text")
          : (this.password1 = "password");
      } else {
        this.password2 == "password"
          ? (this.password2 = "text")
          : (this.password2 = "password");
      }
    },
    // 切换
    switchFun() {
      this.switchactive = !this.switchactive;
      if (!this.switchactive) {
        this.navbar.title.coment = "短信登录";
      } else {
        this.navbar.title.coment = "账号密码登录";
      }
    },
    // 发送验证码
    smsFun() {
      let that = this;
      // 判断电话号码
      if (comm.test(this.from.mobile, "电话号码", [1, 3], this) == false) {
        return;
      }
      sms({ phone: this.from.mobile }).then(res => {
        if (res.status == 0) {
          that.second = 60;
          that.setTime();
        }
        comm.Dialog2(res.msg, "success", 1000, that);
      });
    },
    // 监听
    change() {
      // 登录
      if (!this.modify) {
        // 验证码登录
        if (!this.switchactive) {
          if (this.from.mobile && this.from.smsCode) {
            this.disabled = false;
          } else {
            this.disabled = true;
          }
        }
        // 账号密码
        else {
          if (this.from.mobile && this.from.password) {
            this.disabled = false;
          } else {
            this.disabled = true;
          }
        }
      } else {
        // 修改密码
        if (
          this.from.mobile &&
          this.from.password &&
          this.from.password2 &&
          this.from.smsCode
        ) {
          this.disabled = false;
        } else {
          this.disabled = true;
        }
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
          this.comentSms = "获取验证码";
        }
      }, 1000);
    },
    // 忘记密码
    modifyFun(modify = "") {
      this.modify = modify ? modify : 1;
      this.navbar.title.coment = "修改密码";
      this.btnCoemnt = "提交";
      this.navbar.left[0].coment = "";
      this.navbar.right[0].coment = "登录";
    }
  },
  mounted() {
    // 开启动画
    this.titleShow = !this.titleShow;
    if (this.$route.query.modify) {
      this.modifyFun(this.$route.query.modify);
    }
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
  margin-top: 4rem;
}
.fromSubmint {
  padding: 1.875rem 0.9375rem 0 0.9375rem;
  background: #ffffff;
  height: -webkit-fill-available;
}
h5 {
  font-size: 1rem;
  padding-bottom: 3.125rem;
}

.slide-fade-enter-active {
  transition: all 1s ease;
}
.slide-fade-enter,
.slide-fade-leave-to {
  transform: translateX(3rem) rotate(9deg);
  opacity: 0;
}

.switch {
  display: flex;
  align-items: center;
  margin-top: 1.875rem;
  color: #666666;
  font-size: 0.75rem;
  justify-content: space-between;
}

.switch span {
  display: flex;
  align-items: center;
}
.checked {
  margin-top: 0.3rem;
  font-size: 0.625rem;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999999;
  /deep/ i {
    width: 1rem;
    height: 1rem;
    line-height: normal;
  }
  /deep/ .van-checkbox__label {
    margin-left: 0.3125rem;
  }
  span {
    color: #d7000f;
  }
}
</style>

