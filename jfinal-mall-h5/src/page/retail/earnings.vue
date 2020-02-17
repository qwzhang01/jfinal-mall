<template>
  <div>
    <!-- 头部 -->
    <Navbar :navbar="navbar" />
    <div class="recuser-list">
      <van-row class="recuser-title">
        <van-col span="10">时间</van-col>
        <van-col span="4">来源</van-col>
        <van-col span="5">数值</van-col>
        <van-col span="5">类型</van-col>
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
            <van-col span="10">{{item.create_time}}</van-col>
            <van-col span="4">{{businessTypeList[item.business_type]}}</van-col>
            <van-col span="5">{{(item.type === 1? '+':'-') + item.amount}}</van-col>
            <van-col span="5">{{isWithdrawList[item.is_withdraw]}}</van-col>
          </van-row>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>
<script>
import { getStatis, getMyEarnings } from "../../../api/retail/";
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
      staticSum: {
        waitVerificationSum: "",
        useralbeSum: "",
        waitCheckInSum: ""
      },
      isWithdrawList: ["", "可提现", "不可提现"],
      businessTypeList: [
        "",
        "注册",
        "分享商品",
        "取消订单",
        "提现",
        "消费",
        "邀请奖励",
        "邀请用户",
        "下单赠送",
        "提现失败",
        "线下赠送"
      ],
      list: [],
      pageSize: 10,
      pageNumber: 0,
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        // background: "#D7000F",
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
          coment: "积分明细",
          // 颜色
          color: "#333333"
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
        _this.staticSum.waitVerificationSum = res.data.mine_invest_remain;
        _this.staticSum.useralbeSum = res.data.usable_remain;
        _this.staticSum.waitCheckInSum = res.data.freeze_remain;
      });
    },
    getPage() {
      this.pageNumber++;
      let params = { pageSize: this.pageSize, pageNumber: this.pageNumber };
      getMyEarnings(params)
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
          this.$toast("获取我的收益信息失败");
          console.log(e);
        });
    }
  },
  created() {},
  mounted() {}
};
</script>

<style lang="less" scoped>
.retear-title {
  padding-top: 0.3rem;
  padding-bottom: 0.5rem;
  height: 7rem;
  background: #e70c12;
  .cold {
    margin-top: 1rem;
    color: #fff;
    font-size: 1rem;
    line-height: 1.8rem;
    padding-left: 0.9375rem;
  }
  .other {
    margin-top: 1rem;
    color: #fff;
    font-size: 14px;
    line-height: 1.6rem;
    padding: 0 0.9375rem;
  }
}
.recuser-list {
  margin-top: 1.25rem;
  background: #fff;
  .recuser-title {
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
