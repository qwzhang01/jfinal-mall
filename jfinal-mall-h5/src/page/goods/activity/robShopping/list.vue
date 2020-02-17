<template>
  <div class="body">
    <!-- 头部导航 -->
    <Navbar :navbar="navbar" />
    <!-- 内容 -->
    <div class="coment">
      <!-- 图片 -->
      <div class="banner">
        <img v-lazy="banner" alt="头图" />
      </div>
      <!-- 时间列表 -->
      <div class="timeList">
        <div v-for="(item,key) in timeData" :key="key" class="flash_time" @click="selectTime(item)">
          <div
            :class="[activeTime.lotteryId == item.lotteryId?'active':'','activeTime']"
          >{{item.fontTime}}</div>
          <div class="font">
            <span
              :class="[activeTime.lotteryId == item.lotteryId?'active':'','activeTime']"
            >{{ item.status == 1 ?'正在开抢':'即将开抢'}}</span>
          </div>
        </div>
      </div>
      <div class="alert">
        <div>
          <span>距离本场{{activeTime.status == 1?'结束':'开始'}}时间</span>
          <yd-countdown
            class="countdown"
            :time="activeTime.time"
            format="<i>{%d}</i> <i>{%h}</i> <i>{%m}</i> <i>{%s}</i>"
          ></yd-countdown>
        </div>
        <!-- 标签 -->
        <span class="label" @click="show = !show">
          今日
          <van-icon name="arrow-down" />
        </span>
      </div>
      <!-- 列表 -->
      <!-- <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="lotteryListFun"  id="screen"> -->
      <List :list_type="4" :list="list" @joinActivity="joinActivity" />
      <!-- </van-list> -->
    </div>

    <!-- 选择 -->
    <yd-popup v-model="show" position="bottom" height="40%">
      <van-picker
        show-toolbar
        title="活动时间"
        :columns="lotteryDateListTime"
        @cancel="show = !show"
        @confirm="onConfirm"
      />
    </yd-popup>
  </div>
</template>

<script>
import { Dialog } from "vant";
import commJs from "../../../../comm/";
import Navbar from "../../../../components/navbar";
import List from "../../../../components/list";
import {
  lotteryTimeList,
  lotteryDateList,
  lotteryList
} from "../../../../../api/goods/";
export default {
  components: {
    Navbar,
    List
  },
  data() {
    return {
      show: false,
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "一元抽奖",
          //颜色
          color: "#333333"
        }
      },
      loading: false,
      finished: false,
      pageSize: 10, // 每条显示页数
      pageNumber: 1, // 当前页数
      list: [], // 列表数据
      totalPage: 1, // 总页数
      // banner
      banner: "",
      // 规则图片
      regulation: "",
      // 今日
      lotteryDateListTime: [],
      // 时间列表
      timeData: [],
      // 选中的时间
      activeTime: {
        lotteryId: ""
      }
    };
  },
  methods: {
    joinActivity(item) {
      if (item.isMax == 1) {
        commJs.Dialog2("参与人数已满", "error", 1000, this);
        return;
      }
      if (item.isSellOut == 1) {
        commJs.Dialog2("商品已售罄", "error", 1000, this);
        return;
      }

      this.$router.push({
        path: `/goodsinfo?goodId=${item.goodId}&lotteryGoodId=${
          item.lotteryGoodId
        }`
      });
      sessionStorage.setItem("upper", "/robShoppingList");
    },
    // 确定
    onConfirm(value, index) {
      this.show = !this.show;
      this.lotteryTimeListFun(this.lotteryDateListTime[index]);
    },
    // 切换
    selectTime(item) {
      this.pageNumber = 1;
      this.activeTime = item;
      this.lotteryListFun();
    },
    // 获取活动日期列表
    lotteryDateListFun() {
      let that = this;
      lotteryDateList().then(res => {
        if (res.status == 0) {
          that.lotteryDateListTime = res.data;
        } else {
          commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
    },
    // 获取活动banner&活动时间列表
    lotteryTimeListFun(lotteryDate = "") {
      let that = this;
      let data = {
        lotteryDate
      };
      lotteryTimeList(data).then(res => {
        if (res.status == 0) {
          // 获取规则
          that.regulation = res.data.regulation;
          // 获取banner
          that.banner = res.data.banner;
          // 时间
          that.timeData = res.data.lotteryTimeList;
          if (that.timeData.length > 0) {
            // 获取结束时间
            if (that.timeData[0].status == 1) {
              that.timeData[0].time = that.timeData[0].endTime.replace(
                /-/g,
                "/"
              );
            } else {
              // 获取开始时间
              that.timeData[0].time = that.timeData[0].startTime.replace(
                /-/g,
                "/"
              );
            }
            that.activeTime = Object.assign(that.activeTime, that.timeData[0]);
            that.lotteryDateListFun();
            that.lotteryListFun();
          }
        } else {
          commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
    },
    // 获取活动商品列表
    lotteryListFun() {
      let that = this;
      let data = {
        lotteryId: that.activeTime.lotteryId
      };
      lotteryList(data).then(res => {
        if (res.status == 0) {
          that.list = res.data.list;
          window.scrollTo(0, 0);
          // 总页数 > 当前页数
          // setTimeout(() => {
          //       if(res.data.totalPage >= that.pageNumber){
          //           that.pageSize = res.data.pageSize;
          //           that.totalPage = res.data.totalPage;
          // that.list.push(...res.data.list);
          // that.pageNumber = parseInt(that.pageNumber) + 1;
          //     }else{
          //        that.finished = true;
          //     }
          //       // 加载状态结束
          //       that.loading = false;

          // }, 500);
        } else {
          commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
    }
  },
  mounted() {
    // 判断是否登录
    if (commJs.testLogin(this, "/robShoppingList") == false) {
      return;
    }
    this.lotteryTimeListFun();
  }
};
</script>

<style lang="less" scoped>
.body {
  background: #a90125;
  height: -webkit-fill-available;
}
// 头图
.banner {
  width: 100%;
  img {
    width: 100%;
  }
}

.alert {
  color: #ffdda0;
  font-size: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  margin: 0.2rem 0;
  .label {
    display: flex;
    color: #ffffff;
    align-items: center;
    background: rgba(230, 230, 230, 0.2);
    width: 3rem;
    font-size: 0.8rem;
    justify-content: center;
    position: absolute;
    right: 0;
    border-top-left-radius: 0.9375rem;
    border-bottom-left-radius: 0.9375rem;
    padding: 0.2rem;
  }
}
.activeTime {
  color: #e59b7b;
}
.active {
  color: #ffdda0;
  font-size: 0.8rem;
}
/deep/ .countdown {
  margin-left: 0.5rem;
  position: relative;
  bottom: 0.15rem;
}
/deep/ .countdown span i {
  background: #ffdda0;
  color: #a90125;
  font-size: 0.8125rem;
  padding: 0.1rem 0.2rem;
  border-radius: 0.2rem;
}
/deep/ .shop_box {
  background: #a90125 !important;
  display: flex;
  flex-wrap: wrap;
  // justify-content:space-between;
  padding: 0 0.35rem 0.8rem 0.35rem;
  /deep/ .shop {
    margin-left: 0rem !important;
    width: 32.5% !important;
  }
  /deep/ .title {
    line-height: 1rem;
    word-wrap: break-word;
  }
}

/deep/ .van-picker__cancel {
  color: #999999;
}
</style>

