<template>
  <div>
    <!-- 头部 -->
    <Navbar :navbar="navbar" />
    <!-- 表单 -->
    <yd-cell-group>
      <router-link to="/myinfo">
        <yd-cell-item arrow class="headIcon_div">
          <span slot="left">
            <div class="Info">
              <div class="headIcon">
                <img v-lazy="from.head_pic" alt="头像" class="headIcon" />
              </div>
              <div class="Info_1">
                <div>
                  <span>{{from.nickname}}</span>
                  <img
                    :src="from.sex==1? require('../../assets/man.png') :require('../../assets/girl.png')"
                    alt="性别"
                    v-if="from.sex"
                  />
                </div>
                <!-- <div>{{from.levelName}}</div> -->
              </div>
            </div>
          </span>
        </yd-cell-item>
      </router-link>
      <router-link to="/addresslist?u=set">
        <yd-cell-item arrow>
          <span slot="left">地址管理</span>
        </yd-cell-item>
      </router-link>
      <yd-cell-item arrow>
        <span slot="left">账户安全</span>
        <span slot="right">密码</span>
      </yd-cell-item>
      <router-link
        :to="{path: '/cartSet', query:{bank_name: from.bank_name, bank_card: from.bank_card, realname: from.realname, u: 'set'}}"
      >
        <yd-cell-item arrow>
          <span slot="left">提现设置</span>
        </yd-cell-item>
      </router-link>
      <yd-cell-item arrow style="margin-top:.625rem;">
        <span slot="left">通用</span>
      </yd-cell-item>
    </yd-cell-group>
    <div class="bottom" v-if="!isWeiXin">
      <yd-button size="large" type="danger" class="submit" @click.native="out">退出登录</yd-button>
    </div>
  </div>
</template>

<script>
import { Dialog } from "vant";
import Navbar from "../../components/navbar";
import commFun from "../../comm/";
import { UserDetail, logout } from "../../../api/";
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
            to: "/myself",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "账户设置",
          //颜色
          color: "#333333"
        }
      },
      isWeiXin: false,
      // 表单
      from: {
        head_pic: ""
      }
    };
  },
  methods: {
    // 退出登录
    out() {
      let that = this;
      commFun
        .Dialog("温馨提示", "确定退出登录？", Dialog)
        .then(() => {
          console.log("确定");
          logout({ token: sessionStorage.getItem("token") }).then(res => {
            sessionStorage.clear();
            that.$router.push({
              path: "/login"
            });
          });
        })
        .catch(() => {
          console.log("取消");
        });
    },
    // 用户信息
    UserDetailFun() {
      let that = this;
      let params = {
        userId: sessionStorage.getItem("userId")
          ? sessionStorage.getItem("userId")
          : ""
      };
      UserDetail(params).then(res => {
        if (res.status == 0) {
          that.from = Object.assign(that.from, res.data);
        }
      });
    }
  },
  mounted() {
    this.isWeiXin = commFun.isWeiXin();
    this.UserDetailFun();
  }
};
</script>

<style lang="less" scoped>
.headIcon_div {
  width: 100%;
  height: 5.1875rem;
  margin-bottom: 0.625rem;
  .Info {
    display: flex;
    align-items: center;
  }
  .Info_1 {
    div:nth-child(1) {
      span:nth-child(1) {
        font-size: 1.125rem;
        color: #333333;
      }
      img {
        width: 1rem;
        height: 1rem;
        position: relative;
        top: 0.1rem;
      }
    }
    div:nth-child(2) {
      margin-bottom: 0.1rem;
      font-size: 0.875rem;
      color: #999999;
    }
  }
  .headIcon {
    margin-right: 0.2rem;
    img {
      width: 3.9375rem;
      height: 3.9375rem;
      border-radius: 50%;
    }
  }
}
/deep/ .yd-cell {
  background: #f5f5f5;
}
/deep/ .yd-cell-item {
  background: #ffffff;
}
/deep/ .yd-btn-block {
  margin-top: 0;
}
.submit {
  height: 2.8125rem;
  font-size: 1rem;
  font-family: PingFang SC;
  border-radius: 0.25rem;
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

