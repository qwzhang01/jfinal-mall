<template>
  <div class="container">
    <div class="login-div">
      <el-form
        :model="ruleForm2"
        :rules="rules2"
        ref="ruleForm2"
        label-position="left"
        label-width="0px"
        class="login-form"
      >
        <div class="title">
          <span>{{set.title}}</span>
        </div>
        <el-form-item prop="account">
          <el-input
            type="text"
            v-model="ruleForm2.account"
            auto-complete="off"
            placeholder="请输入账号"
            @keyup.enter.native="handleSubmit2"
          ></el-input>
        </el-form-item>
        <el-form-item prop="checkPass">
          <el-input
            type="password"
            v-model="ruleForm2.checkPass"
            auto-complete="off"
            placeholder="请输入密码"
            @keyup.enter.native="handleSubmit2"
          ></el-input>
        </el-form-item>
        <el-form-item prop="safecode_iput">
          <div>
            <el-input
              type="text"
              v-model="ruleForm2.safecode_iput"
              auto-complete="off"
              placeholder="请输入验证码"
              @keyup.enter.native="handleSubmit2"
              style="width: 240px;float: left;"
            ></el-input>
            <el-image :src="codeUrl" title="点击刷新" alt="验证码" @click.native="refreshCode"></el-image>
          </div>
        </el-form-item>
        <!-- <el-checkbox v-model="checked" checked class="remember">记住密码</el-checkbox> -->
        <el-form-item style="width:100%;">
          <el-button
            type="primary"
            style="width:45%;"
            @click.native.prevent="handleSubmit2"
            :loading="logining"
          >登录</el-button>
          <el-button style="width:45%;" @click.native.prevent="handleReset2">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="tips-login">
        <span>版权所有：{{set.title}}</span>
        <a href="http://static.centbrowser.com/installer_32/centbrowser_2.5.6.57.exe">下载谷歌浏览器</a>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from "@/api/login";
import { setToken, setUserId } from "@/util/cookie";
import set from "@/set.js";

export default {
  data() {
    return {
      set,
      logining: false,
      codeUrl: "/tomcat/web/staff/image?t=",
      ruleForm2: {
        account: "",
        checkPass: "",
        safecode_iput: ""
      },
      rules2: {
        account: [{ required: true, message: "请输入账号", trigger: "blur" }],
        checkPass: [{ required: true, message: "请输入密码", trigger: "blur" }],
        safecode_iput: [
          { required: true, message: "请输入验证码", trigger: "blur" }
        ]
      },
      checked: true
    };
  },
  methods: {
    refreshCode() {
      let timestamp = new Date().getTime();
      this.codeUrl = "/tomcat/web/staff/image?t=" + timestamp;
    },
    handleReset2() {
      this.$refs.ruleForm2.resetFields();
    },
    handleSubmit2(ev) {
      let _this = this;
      _this.$refs.ruleForm2.validate(valid => {
        if (valid) {
          _this.logining = true;
          login(
            _this.ruleForm2.account,
            _this.ruleForm2.checkPass,
            _this.ruleForm2.safecode_iput
          ).then(obj => {
            _this.logining = false;
            let { msg, status, data } = obj;
            if (status !== 0) {
              _this.refreshCode();
            } else {
              // 保存登录信息至cookie
              setToken(data.accessToken);
              setUserId(data.userId);
              // 保存路由相关信息至状态
              _this.$store.dispatch("loadOperator");
              // 跳转首页
              _this.$router.push({ path: "/" });
            }
          });
        } else {
          return false;
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
  height: 100%;
  background: #2f3f4f;
  text-align: center;
  margin: 0;
  padding: 0;
  .login-div {
    display: block;
    position: absolute;
    top: 25%;
    left: 35%;
    .login-form {
      -webkit-border-radius: 5px;
      border-radius: 0 30px 0 30px;
      -moz-border-radius: 5px;
      background-clip: padding-box;
      width: 350px;
      padding: 35px 35px 15px 35px;
      background: #fff;
      border: 1px solid #eaeaea;
      // box-shadow: 0 0 25px #cac6c6;
      .title {
        margin: 0px auto 40px auto;
        text-align: center;
        font-size: 26px;
        color: #14a1bf;
      }
      .remember {
        margin: 0px 0px 35px 0px;
      }
    }
    .tips-login {
      width: 100%;
      margin-top: 45px;
      span {
        color: #999;
      }
      a {
        margin-left: 20px;
        color: #14a1bf;
        text-decoration: none;
      }
    }
  }
}
</style>