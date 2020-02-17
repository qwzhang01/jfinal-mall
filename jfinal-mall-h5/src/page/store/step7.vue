<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar"/>
    <!-- 审核中.... -->
    <div class="coment" v-if="active == 2">
      <img src="../../assets/examine.png" alt="icon">
      <p>您的申请已处理，正在进行信息核验，请耐心等待！</p>
    </div>
    <!-- 审核状态 -->
    <div class="status" v-if=" active == 3 || active == 4">
      <van-icon :name="active==4?'info':'checked'" color="#ffffff" size="1rem"/>
      <span>{{active == 4?'实人认证状态: 审核不通过':active == 3?'恭喜你审核通过，祝您生意兴隆':''}}</span>
    </div>
    <!-- 审核不通过结果 -->
    <div class="result" v-if="active == 4">
      <div class="title">原因及建议如下</div>
      <div class="text">{{result}}</div>
    </div>
    <!-- 按钮 -->
    <van-button
      type="danger"
      size="large"
      style="background:#D7000F;"
      @click.native="submit"
      class="submit"
    >{{active == 2 || active == 4 ?'重新认证':'确认'}}</van-button>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import commJs from "../../comm";
import { UserDetail } from "../../../api/";
import { applyFailReason } from "../../../api/store/";
export default {
  components: {
    Navbar
  },
  data() {
    return {
      // 1 未开店 2 开店审核中 3 开店成功 4 审核失败
      active: 1,
      // 审核失败结果
      result: "",
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
          coment: "开店审核",
          //颜色
          color: "#333333"
        },
        right: []
      }
    };
  },
  methods: {
    submit() {
      let path;
      // 审核失败
      if (this.active == 4) {
        path = "/step1";
      }
      // 重新认证
      else if (this.active == 2) {
        path = "/step1";
      } else {
        path = "/myself";
      }
      this.$router.push({ path });
    },
    // 用户信息
    UserDetailFun() {
      let that = this;
      UserDetail().then(res => {
        if (res.status == 0) {
          // 更改路由 我要开店(1 未开店 2 开店审核中 3 开店成功 4 审核失败)
          that.active = res.data.storeStatus;
          if (that.active == 4) {
            that.resultFun(res.data.storeId);
          }
        }
      });
    },
    // 审核失败原因
    resultFun(storeId) {
      let that = this;
      applyFailReason({ storeId }).then(res => {
        if (res.status == 0) {
          that.result = res.data;
        } else {
          console.log(res.msg);
        }
      });
    }
  },
  mounted() {
    // 判断是否登录
    if (commJs.testLogin(this, "/myself") == false) {
      return;
    }
    this.UserDetailFun();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.coment {
  text-align: center;
  margin-top: 3.75rem;
  p {
    color: #666666;
    font-size: 1rem;
    width: 60%;
    margin: 0 auto;
    line-height: 1.5rem;
    font-family: PingFang SC;
  }
  img {
    width: 9.4375rem;
    height: 9.375rem;
    margin-bottom: 3rem;
  }
}
.submit {
  position: fixed;
  bottom: 0;
}
.result {
  background: #ffffff;
  padding-left: 0.9375rem;
  .title {
    padding: 0.9375rem 0;
    font-size: 1rem;
    color: #333333;
    font-family: PingFang SC;
    border-bottom: 1px solid #e6e6e6;
  }
  .text {
    padding: 0.9375rem 0.9375rem 0.9375rem 0;
    color: #999999;
    font-size: 0.875rem;
    font-family: PingFang SC;
  }
}
.status {
  padding: 0.8125rem 0;
  background: #d7000f;
  color: #ffffff;
  font-size: 0.9375rem;
  font-family: PingFang SC;
  display: flex;
  justify-content: center;
  align-items: center;
  span {
    margin-left: 0.625rem;
  }
}
</style>

