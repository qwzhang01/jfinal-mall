<template>
  <div>
    <!-- 头部 -->
    <Navbar :navbar="navbar" />
    <div class="recuser-num">
      共
      <span>{{sublineSum}}</span>人
    </div>
    <div class="recuser-list">
      <van-row class="recuser-title">
        <van-col span="10">昵称</van-col>
        <van-col span="10">手机</van-col>
        <van-col span="4">订单</van-col>
      </van-row>
      <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
        <van-list
          class="recuser-data"
          v-model="isLoading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <van-row v-for="item in list" :key="item.user_id">
            <van-col span="10">{{item.nickname}}</van-col>
            <van-col span="10">{{item.mobile}}</van-col>
            <van-col span="4">{{item.order_count}}</van-col>
          </van-row>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>
<script>
import { getMyCustomers, getMyCustomerSum } from "../../../api/retail/";
import Navbar from "../../components/navbar";
export default {
  components: {
    Navbar
  },
  data() {
    return {
      isRefreshing: false,
      isLoading: false,
      finished: false,
      sublineSum: 0,
      list: [],
      pageSize: 10,
      pageNumber: 0,
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        background: "#D7000F",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/retail",
            color: "#ffffff"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "推广用户",
          // 颜色
          color: "#ffffff"
        }
      }
    };
  },
  methods: {
    onLoad(e) {
      this.getPage();
      this.getSum();
    },
    onRefresh() {
      this.list = [];
      this.pageNumber = 0;
      this.finished = false;
      this.getPage();
    },
    getSum() {
      let that = this;
      getMyCustomerSum().then(res => {
        that.sublineSum = res.data;
      });
    },
    getPage() {
      this.pageNumber++;
      let params = { pageSize: this.pageSize, pageNumber: this.pageNumber };
      getMyCustomers(params)
        .then(res => {
          this.list = this.list.concat(res.data.list);
          // 加载状态结束
          this.isRefreshing = false;
          this.isLoading = false;
          // 数据全部加载完成
          if (res.data.lastPage) {
            this.finished = true;
          }
        })
        .catch(e => {
          this.$toast("获取我的用户信息失败");
          console.log(e);
        });
    }
  },
  created() {},
  mounted() {}
};
</script>

<style lang="less" scoped>
.recuser-num {
  height: 7rem;
  line-height: 7rem;
  text-align: center;
  font-size: 1rem;
  color: #fff;
  background-color: #e70c12;
  span {
    margin: 0 10px;
    font-family: DIN;
    font-weight: bold;
    font-size: 1.2rem;
  }
}
.recuser-list {
  background: rgb(250, 250, 250);
  .recuser-title {
    margin-top: 1.25rem;
    padding: 0.2rem 0.9375rem;
    border-bottom: 1px solid #f7f7f7;
    .van-col {
      text-align: center;
      font-size: 14px;
      line-height: 2.4rem;
    }
  }
  .recuser-data {
    .van-row {
      padding: 0.1rem 0.9375rem;
      border-bottom: 1px solid #f7f7f7;
      .van-col {
        text-align: center;
        font-size: 0.85rem;
        line-height: 2.4rem;
      }
    }
  }
}
/deep/ .yd-navbar:after {
  content: none;
}
</style>
