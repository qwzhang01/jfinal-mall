<template>
  <div>
    <!-- 头部 -->
    <div
      class="header"
      :style="{background:'url('+require('../../assets/back.png')+') no-repeat center'}"
    >
      <!-- 头部导航 -->
      <Navbar :change_navbar="btn_navbar" :navbar="navbar" />
      <!-- 用户 -->
      <div class="user">
        <!-- 头像 -->
        <div class="img">
          <router-link to="/set">
            <img v-lazy="userInfo.head_pic" alt="头像" />
          </router-link>
        </div>
        <div class="info">
          <!-- 资料 -->
          <div class="moblie">{{userInfo.nickname}}</div>
          <!-- <div class="role">{{userInfo.levelName}}</div> -->
          <!-- <div>
            <div>查看编辑资料</div>
            <div class="progressbar">
              <yd-progressbar type="line" :progress="(progress_mun / progress_total)" trail-width="2" trail-color="#FE5D51"></yd-progressbar>
              <span>
                <yd-countup :endnum="progress_mun" class="countup"></yd-countup> / {{progress_total}}
              </span>
            </div>
          </div>-->
        </div>
      </div>
    </div>

    <!-- 我的订单 -->
    <div class="order">
      <div class="title">
        <span>我的订单</span>
        <span>
          <router-link to="/orderlist?tabl=0">
            <yd-navbar-next-icon color="#999999">查看全部订单</yd-navbar-next-icon>
          </router-link>
        </span>
      </div>
      <!-- 订单状态 -->
      <div class="order_list">
        <div class="order_div" v-for="(item,key) in order_list" :key="key">
          <router-link :to="'/orderlist?tabl='+(key+1)">
            <div class="img">
              <!-- 订单数 -->
              <yd-badge
                type="danger"
                v-if=" key==0 ? userInfo.waitPay:key==1 ? userInfo.waitSend : key==2 ? userInfo.waitReceive : key==3 ? userInfo.uncomment_count :userInfo.return_count"
              >
                {{
                // 待付款
                key==0 ? userInfo.waitPay :
                // 待发货
                key==1 ? userInfo.waitSend :
                // 待收货
                key==2 ? userInfo.waitReceive :
                // 待评价
                key==3 ? userInfo.uncomment_count :
                // 退换货
                userInfo.return_count
                }}
              </yd-badge>
              <img :src="require('../../assets/payment_'+key+'.png')" :alt="item" />
            </div>
            <div>{{item}}</div>
          </router-link>
        </div>
      </div>
    </div>
    <!-- 工具 -->
    <div class="tool">
      <!-- 标题 -->
      <div class="title">
        <span>其他工具</span>
      </div>
      <!-- 列表 -->
      <div class="list">
        <div v-for="(item,key) in tool" :key="key" class="tool_list">
          <router-link :to="item.path">
            <div>
              <img :src="item.icon" alt="icon" />
            </div>
            <div>{{item.name}}</div>
          </router-link>
        </div>
        <div class="clear"></div>
      </div>
    </div>
    <!-- 底部导航 -->
    <Nav class="foot_nav" :btn="4" v-if="active" />
  </div>
</template>
<script>
import Nav from "../../components/nav";
import { Dialog } from "vant";
import Navbar from "../../components/navbar";
import { UserDetail } from "../../../api/";
import commJs from "../../comm/index";
export default {
  components: {
    Nav,
    Navbar
  },
  data() {
    return {
      // 设备信息
      view: commJs.equipment(),
      // 底部导航栏是否激活
      active: true,
      // 总人数
      progress_total: 50,
      // 实际人数
      progress_mun: 1,
      // 头部导航栏
      navbar: {
        // 样式调整
        background: "#D7000f",
        height: "2.125rem",
        // 左边
        left: [],
        // 右边
        right: [
          {
            icon_img: require("../../assets/set.png"),
            // 目标地址
            to: "/set",
            // 图标大小
            size: "1.3rem"
          },
          {
            icon_img: require("../../assets/navbar_sms.png"),
            // 颜色
            color: "#ffffff",
            // 目标地址
            to: "#",
            // 图标大小
            size: "1.3rem"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "我的",
          // 颜色
          color: "#ffffff"
        }
      },
      // 用户信息
      userInfo: { head_pic: "" },
      // 订单
      order_list: ["待付款", "待发货", "待收货", "待评价"],
      // 其他工具
      tool: [
        // {
        //   icon:require('../../assets/yunyingshang.png'),
        //   name:'申请运营商',
        //   path:''
        // },
        // {
        //   icon: require('../../assets/gongyingshang.png'),
        //   name: '我要开店',
        //   path: '/step1'
        // },
        {
          icon: require("../../assets/retail/icon.png"),
          name: "积分中心",
          path: "/retail"
        },
        {
          icon: require("../../assets/kaquanbao.png"),
          name: "卡券包",
          path: ""
        },
        {
          icon: require("../../assets/shoucang.png"),
          name: "我的收藏",
          path: "/collection"
        },
        {
          icon: require("../../assets/tuan.png"),
          name: "我的拼团",
          path: ""
        },
        {
          icon: require("../../assets/luck_draw.png"),
          name: "我的抽奖",
          path: "/robShoppingorderList"
        },
        {
          icon: require("../../assets/shouhuodizhi.png"),
          name: "收货地址",
          path: "/addresslist"
        }
        // {
        //   icon: require('../../assets/qianbao.png'),
        //   name: '我的钱包',
        //   path: '/wallet'
        // }
      ]
    };
  },
  methods: {
    // 点击头部导航栏
    btn_navbar(data) {
      console.log(data);
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
          that.userInfo = Object.assign(that.userInfo, res.data);
          // 更改路由 我要开店(1 未开店 2 开店审核中 3 开店成功 4 审核失败)
          // if (res.data.storeStatus != 1) {
          //   this.tool[0].path = "/step7";
          // }
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

<style lang="less" scoped>
.header {
  background-size: 100% 100%;
}
// 用户
.user {
  display: flex;
  height: 11.875rem;
  padding: 4.2rem 0.9375rem 0.9375rem 0.9375rem;
  .img {
    img {
      width: 4.0625rem;
      height: 4.0625rem;
      border-radius: 50%;
      display: block;
    }
  }
  .info {
    div {
      margin-top: 0.2rem;
      color: #ffffff;
    }
    margin-left: 1rem;
    margin-top: 0.5rem;
    .moblie {
      font-size: 1.125rem;
    }
    .role {
      font-size: 0.75rem;
    }
  }
  .progressbar {
    /deep/ .yd-progressbar {
      width: 10rem;
      margin-right: 1rem;
      position: relative;
      top: -0.2rem;
    }
    display: flex;
    align-items: center;
    span {
      display: block;
      display: flex;
    }
    .countup {
      margin-right: 0.15rem;
    }
  }
}
// 标题
.title {
  display: flex;
  justify-content: space-between;
  height: 2.8125rem;
  align-items: center;
  padding: 0 0.6875rem;
  border-bottom: 0.0625rem solid #e6e6e6;
  span:nth-child(1) {
    color: #333333;
    font-size: 1rem;
  }
  span:nth-child(2) {
    span {
      color: #999999;
      font-size: 0.875rem;
    }
  }
}

// 我的订单
.order {
  background: #ffffff;
  height: 7.5rem;
  width: 93%;
  margin: 0 auto;
  border-radius: 0.625rem;
  position: relative;
  top: -2rem;
  .order_list {
    display: flex;
    .order_div {
      flex: auto;
      text-align: center;
      margin-top: 1.1rem;
      div:nth-child(2) {
        margin-top: 0.1rem;
      }
    }
    .img {
      width: 1.875rem;
      height: 1.5rem;
      margin: 0 auto;
      position: relative;
      /deep/ span {
        position: absolute;
        left: 0.8rem;
        bottom: 0.8rem;
      }
      img {
        width: 100%;
      }
    }
  }
}

// 工具
.tool {
  background: #ffffff;
  width: 93%;
  margin: 0 auto;
  border-radius: 0.625rem;
  position: relative;
  top: -1.25rem;
  .list {
    padding-bottom: 0.8rem;
    .tool_list {
      float: left;
      width: 25%;
      text-align: center;
      font-size: 0.8125rem;
      color: #666666;
      margin-top: 0.9375rem;
      img {
        width: 1.5rem;
        height: 1.5rem;
      }
    }
  }
}
.clear {
  clear: both;
}
/deep/ .yd-navbar:after {
  content: none;
}
</style>
