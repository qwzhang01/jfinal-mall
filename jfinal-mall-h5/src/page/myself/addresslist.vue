<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar"/>
    <!-- 列表 -->
    <div class="list_box">
      <!-- <div :class="['list',item.is_default?'default':'']" v-for="(item,key) in list" :key="key"> -->
      <div class="list" v-for="(item,key) in list" :key="key">
        <div class="user" @click="select(switchs?item:'')">
          <!-- 用户 -->
          <div>
            <span>{{item.consignee}}</span>
            <span>{{item.mobile}}</span>
            <van-tag round type="danger" size="medium" color="#D7000F" v-if="item.is_default">默认</van-tag>
          </div>
          <!-- 地址 -->
          <div>{{item.provinceed}} {{item.cityed}} {{item.districted}} {{item.twoned}} {{item.address}}</div>
        </div>
        <!-- 修改 -->
        <div class="icon">
          <img src="../../assets/modify.png" alt="修改" @click="Jump(item)">
        </div>
      </div>
    </div>
    <!-- 提交 -->

    <yd-button
      size="large"
      shape="angle"
      type="primary"
      color="#ffffff"
      bgcolor="#D7000F"
      class="btn"
      @click.native="Jump()"
    >新建收货地址</yd-button>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import { GetAddress } from "../../../api/";
import commJs from "../../comm/";
export default {
  components: {
    Navbar
  },
  data() {
    return {
      // 类型
      type: "",
      // 规定能开启的页面
      startUp: ["goodsinfo", "orderinfo", "ordinaryorder"],
      // 跳转地址
      jump: "",
      // 是否开启地址选择开关
      switchs: false,
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
          coment: "地址管理",
          //颜色
          color: "#333333"
        }
      },
      // 地址列表
      list: []
    };
  },
  methods: {
    // 获取收货地址
    GetAddressFun() {
      let that = this;
      GetAddress().then(res => {
        if (res.status == 0) {
          that.list.push(...res.data);
        }
      });
    },
    // 跳转
    Jump(item) {
      this.$router.push({
        path: "/modifyaddress?u=" + this.navbar.left[0].to,
        query: {
          item: item ? JSON.stringify(item) : ""
        }
      });
    },
    // 选中
    select(item) {
      if (item) {
        let consignee = {
          address: `${item.provinceed} ${item.cityed} ${item.districted}${
            item.twoned
          }${item.address}`,
          address_id: item.address_id,
          district: item.is_default,
          mobile: item.mobile,
          nickname: item.consignee
        };
        let sessionNmae;
        // 锁
        if (this.$route.query.u.includes("lock")) {
          sessionNmae = "lock";
        } else {
          sessionNmae = "consignee";
        }
        sessionStorage.setItem(sessionNmae, JSON.stringify(consignee));
        this.$router.push({
          path: this.navbar.left[0].to
        });
      }
    },
    //开启地址选择开关
    Switch() {
      // 规定能开启的页面
      if (this.startUp.includes(this.type)) {
        this.switchs = true;
      }
    }
  },
  mounted() {
    this.GetAddressFun();
    // 路由配置
    this.navbar.left[0].to = commJs.jumpUrl(this, this.navbar.left[0].to, [
      "type"
    ]);

    if (this.$route.query.type) {
      this.type = this.$route.query.type;
      this.Switch();
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.list_box {
  padding-bottom: 3.25rem;
  .list {
    display: flex;
    justify-content: space-between;
    background: #ffffff;
    height: 5.75rem;
    align-items: center;
    padding: 0 0.9375rem;
    border-bottom: 0.0625rem solid #e6e6e6;
    position: relative;
    // user
    .user {
      width: 90%;
      color: #333333;
      line-height: 1.1rem;
      div:nth-child(1) {
        font-size: 1rem;
        margin-bottom: 0.3rem;
        span:nth-child(1) {
          margin-right: 1.25rem;
        }
        span:nth-child(2) {
          margin-right: 0.625rem;
        }
      }
      div:nth-child(2) {
        font-size: 0.8125rem;
      }
    }

    // icon
    .icon {
      img {
        width: 1.375rem;
        height: 1.375rem;
      }
    }
  }
}
.btn {
  position: fixed;
  bottom: 0;
  height: 3.125rem;
  font-size: 1rem;
  font-family: PingFang-SC-Medium;
  margin-top: 0;
}
.default ::before {
  content: "";
  left: 0;
  right: 0;
  bottom: 0;
  height: 2px;
  position: absolute;
  background: -webkit-repeating-linear-gradient(
    135deg,
    #ff6c6c 0,
    #ff6c6c 20%,
    transparent 0,
    transparent 25%,
    #1989fa 0,
    #1989fa 45%,
    transparent 0,
    transparent 50%
  );
  background: repeating-linear-gradient(
    -45deg,
    #ff6c6c 0,
    #ff6c6c 20%,
    transparent 0,
    transparent 25%,
    #1989fa 0,
    #1989fa 45%,
    transparent 0,
    transparent 50%
  );
  background-size: 80px;
}
</style>

