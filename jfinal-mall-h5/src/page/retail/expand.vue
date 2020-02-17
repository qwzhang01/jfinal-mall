<template>
  <div>
    <Navbar :navbar="navbar" />
    <!-- <van-tabs animated>
    <van-tab class="resin-list" v-for="(item,index) in tabs" :title="item" :key="index">-->
    <div style="background-color: white; margin-top: 1.25rem">
      <van-row class="resin-title">
        <van-col span="6">会员</van-col>
        <van-col span="12">商品</van-col>
        <van-col span="6">金额</van-col>
      </van-row>
      <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
        <van-list
          class="resin-data"
          v-model="isLoading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <van-row v-for="item in list" :key="item.user_id">
            <van-col span="6">{{item.nickname}}</van-col>
            <van-col span="12">{{item.good_name}}</van-col>
            <van-col span="6">￥{{item.good_price}}</van-col>
          </van-row>
        </van-list>
      </van-pull-refresh>
    </div>
    <!-- </van-tab>
    </van-tabs>-->
  </div>
</template>
<script>
import { getMySubInvest } from "../../../api/retail/";
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
      active: 0,
      tabs: ["全部", "已核销", "未核销"],
      list: [],
      pageSize: 10,
      pageNumber: 0,
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/retail",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "推广业绩",
          // 颜色
          color: "#333333"
        }
      }
    };
  },
  methods: {
    onLoad(e) {
      this.getPage();
    },
    onRefresh() {
      this.list = [];
      this.pageNumber = 0;
      this.finished = false;
      this.getPage();
    },
    getPage() {
      this.pageNumber++;
      let params = { pageSize: this.pageSize, pageNumber: this.pageNumber };
      getMySubInvest(params)
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

<style lang="less" scope>
// .resin-list {
//   margin-top: 1.25rem;
//   background: #fff;
.resin-title {
  padding: 0.2rem 0.9375rem;
  border-bottom: 1px solid #f7f7f7;
  .van-col {
    text-align: center;
    font-size: 14px;
    line-height: 2.4rem;
  }
}
.resin-data {
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
// }
/deep/ .yd-navbar:after {
  content: none;
}
</style>
