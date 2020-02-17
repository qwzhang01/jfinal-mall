<template>
  <div>
    <!-- 头部导航 -->
    <Navbar :navbar="navbar" />
    <!-- 商品和物流 -->

    <!-- 多个商品  -->
    <div class="shop shops" v-if="ship_detail.goodList.length > 1">
      <div class="img">
        <img src="../../assets/lv_you.png" alt="商品" @click.stop="introduceSelf(item)" />
      </div>
    </div>
    <div class="shop">
      <!-- 单个商品 -->
      <div v-if="ship_detail.goodList.length == 1">
        <div class="img" v-for="(item,key) in ship_detail.goodList" :key="key">
          <img :src="item.goodImg" alt="商品" @click.stop="introduceSelf(item)" />
        </div>
      </div>
      <!-- 物流 -->
      <div class="company">
        <div>
          <span>物流状态：</span>
          <span>{{ship_detail.status === 0? '未发货': ship_detail.status === 1? '已发货': '已签收'}}</span>
        </div>
        <div>
          <span>物流公司：</span>
          <span>{{ship_detail.name}}</span>
        </div>
        <div @click="ship_detail.code ? code(ship_detail.code) : '' ">
          <span>物流编号：</span>
          <span id="code">{{ship_detail.code}}</span>
        </div>
        <div>客服电话：{{ship_detail.phone}}</div>
      </div>
    </div>
    <!-- 物流信息 -->
    <div class="log">
      <van-steps
        direction="vertical"
        :active="ship_detail.detail.length"
        v-for="(items,index) in ship_detail.detail"
        :key="index"
      >
        <van-step>
          <h3>【城市】物流状态1</h3>
          <p>2016-07-12 12:40</p>
        </van-step>
        <van-step>
          <h3>【城市】物流状态2</h3>
          <p>2016-07-11 10:00</p>
        </van-step>
        <van-step>
          <h3>快件已发货</h3>
          <p>2016-07-10 09:30</p>
        </van-step>
      </van-steps>
      <div v-if="ship_detail.detail.length == 0" class="none">暂无物流信息</div>
    </div>
  </div>
</template>

<script>
import { Dialog } from "vant";
import comm_js from "../../comm/";
import Navbar from "../../components/navbar";
import { ShipDetail } from "../../../api/index.js";
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
            to:
              this.$route.query.tab1 >= 0
                ? "/orderlist?tabl=" + this.$route.query.tab1
                : "/" +
                  this.$route.query.page +
                  "?orderId=" +
                  this.$route.query.orderId,
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "物流信息",
          //颜色
          color: "#333333"
        }
      },
      // 数据
      ship_detail: {
        code: "",
        detail: [],
        goodList: [],
        name: "",
        status: ""
      }
    };
  },
  methods: {
    // 查看物流
    ShipDetailFun(orderId) {
      let that = this;
      let params = {
        userId: sessionStorage.getItem("user_id"),
        orderId
      };
      ShipDetail(params).then(res => {
        if (res.status == 0) {
          that.ship_detail = res.data;
        } else {
          comm_js.Dialog2(res.msg, "error", 1500, that);
        }
      });
    },
    // 复制
    code(item) {
      comm_js.copey("#code", item, this);
    },
    // 跳转商品
    introduceSelf(item) {
      // console.log(item);
      comm_js.jump({ item }, "", this);
    }
  },
  mounted() {
    if (this.$route.query.orderId) {
      this.ShipDetailFun(this.$route.query.orderId);
    }
  }
};
</script>

<style lang="less" scoped>
.shop {
  background: #ffffff;
  display: flex;
  padding: 0.9375rem;
  // 商品
  .img {
    margin-right: 0.9375rem;
    img {
      width: 4.375rem;
      height: 4.375rem;
      border: 1px solid rgba(230, 230, 230, 1);
    }
  }
  // 物流
  .company {
    font-size: 0.8125rem;
    color: #999999;
    line-height: 1.2rem;
    div:nth-child(1) {
      color: #333333;
      span:nth-child(2) {
        color: #d7000f;
      }
    }
  }
}

.shops {
  padding: 0.9375rem 0.9375rem 0 0.9375rem;
  width: 100%;
  overflow-y: hidden;
}
// 物流信息
.log {
  margin-top: 0.9375rem;
  background: #ffffff;
}
.none {
  text-align: center;
  padding: 1rem 0;
}
</style>

