<template>
  <div>
    <Navbar :navbar="navbar" />
    <div class="recuser-num">总提取：{{totalWithdraw}}</div>
    <div class="recuser-list">
      <van-row class="recuser-title">
        <van-col span="10">时间</van-col>
        <van-col span="7">积分</van-col>
        <van-col span="7">状态</van-col>
      </van-row>
      <van-pull-refresh v-model="isRefreshing" @refresh="onRefresh">
        <van-list
          class="recuser-data"
          v-model="isLoading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <van-row v-for="item in list" :key="item.id">
            <van-col span="10">{{item.create_time}}</van-col>
            <van-col span="7">{{item.amount}}</van-col>
            <van-col span="7">{{item.statuText}}</van-col>
          </van-row>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>
<script>
import { getStatis, getMyWithdraw } from "../../../api/retail/";
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
      totalWithdraw: 0,
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
          coment: "提取记录",
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
      let _this = this;
      getStatis().then(res => {
        _this.totalWithdraw = res.data.withdrawable_total;
      });
    },
    formatStatus(list) {
      list.forEach(item => {
        var statuText = "";
        switch (item.withdraw_status) {
          case 1:
            statuText = "待审核 ";
            break;
          case 2:
            statuText = "待打款 ";
            break;
          case 3:
            statuText = "已完成 ";
            break;
          case 5:
            statuText = "审核拒绝";
            break;
          default:
            return;
        }
        item.statuText = statuText;
      });
      return list;
    },
    getPage() {
      this.pageNumber++;
      let params = { pageSize: this.pageSize, pageNumber: this.pageNumber };
      getMyWithdraw(params)
        .then(res => {
          this.list = this.list.concat(this.formatStatus(res.data.list));
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
  margin-top: 1.25rem;
  background: #fff;
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
