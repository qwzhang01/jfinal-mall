<template>
  <div>
    <Navbar :navbar="navbar" style="background: red;" />

    <div class="haeder" :style="{'height':selected.length > 0 ? height+'rem':'4rem'}">
      <!-- 时间 -->
      <div class="timeList">
        <div v-for="(item,key) in timeData" :key="key" class="flash_time" @click="selectTime(item)">
          <div :class="[activeTime.flashId == item.flashId?'activeTime':'','time']">{{item.font}}</div>
          <div class="font">
            <span
              :class="[activeTime.flashId == item.flashId?activeTime.number==1?'newActiveClass':'activeClass':'']"
            >{{ timestamp > item.start_time?'正在开抢':'即将开抢'}}</span>
          </div>
        </div>
      </div>
      <div class="back" v-if="selected.length>0"></div>
      <!-- 必抢*精品 -->
      <div class="shop" v-if="selected.length>0">
        <div class="title">必选*精品</div>
        <List
          :list_type="7"
          :list="selected"
          :flashActive="activeTime.number"
          @joinActivity="joinActivity"
        />
      </div>
    </div>
    <!-- 秒杀商品列表 -->
    <div class="list">
      <!-- 标题 -->
      <div class="titleList">
        <div class="title_describe">限时限量 好货低价</div>
        <div>
          <span class="title_time">距离本场{{ activeTime.number == 1?'结束':'开始'}}</span>
          <yd-countdown
            class="count_down"
            :time="activeTime.time"
            format="<i>{%d}</i> <i>{%h}</i> <i>{%m}</i> <i>{%s}</i>"
          ></yd-countdown>
        </div>
      </div>
      <!-- 列表 -->
      <div class="shopList">
        <van-list
          v-model="loading"
          :finished="finished"
          :immediate-check="false"
          finished-text="没有更多了"
          @load="flashGoodPageFun"
        >
          <List
            :list_type="7"
            :flashActive="activeTime.number"
            :list="flashlist"
            @joinActivity="joinActivity"
          />
        </van-list>
      </div>
    </div>
  </div>
</template>

<script>
import { flashListTime, flashGoodPage } from "../../../../api/goods/";
import commJs from "../../../comm";
import Navbar from "../../../components/navbar";
import List from "../../../components/list";
import { mapActions, mapGetters } from "vuex";
export default {
  components: {
    Navbar,
    List
  },
  data() {
    return {
      height: 15,
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/",
            color: "#ffffff"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "限时秒杀",
          //颜色
          color: "#ffffff"
        },
        right: []
      },
      // 时间
      timeData: [],
      // 选中的时间数据
      activeTime: {},
      activeFlashId: 0,
      // 滚动加载
      loading: false,
      finished: false,
      pageSize: 10, // 每条显示页数
      pageNumber: 1, // 当前页数
      totalPage: 1, // 总页数
      // 列表
      flashlist: [],
      // 当前的时间戳
      timestamp: "",
      // 每日精选
      selected: []
    };
  },
  methods: {
    //获取秒杀时间
    flashListTimeFun() {
      let that = this;
      flashListTime().then(res => {
        if (res.status == 0) {
          that.timeData = res.data;
          if (that.timeData.length > 0) {
            that.activeTime = that.timeData[0];
            that.activeTime.time = commJs.dateHandle(
              that.activeTime.number == 2
                ? that.activeTime.start_time
                : that.activeTime.end_time,
              "/"
            );
            that.flashGoodPageFun();
          }
        }
      });
    },
    // 获取秒杀列表
    flashGoodPageFun() {
      let that = this;
      let data = {
        flashId: that.activeTime.flashId,
        pageSize: that.pageSize,
        pageNumber: that.pageNumber
      };
      if (!data.flashId) {
        return;
      }
      flashGoodPage(data).then(res => {
        if (res.status == 0) {
          if (res.data.totalPage >= that.pageNumber) {
            that.pageSize = res.data.pageSize;
            that.totalPage = res.data.totalPage;
            if (that.selected.length == 0) {
              let tmpList = res.data.list.filter(i => i.percent !== 100);
              if (tmpList && tmpList.length > 0) {
                that.selected.push(tmpList[0]);
              } else {
                that.selected.push(res.data.list[0]);
              }
            }
            that.flashlist.push(...res.data.list);
            that.pageNumber = parseInt(that.pageNumber) + 1;
          } else {
            that.finished = true;
          }
          // 加载状态结束
          that.loading = false;
        }
      });
    },
    // 选择时间
    selectTime(item) {
      this.activeTime = item;
      this.pageNumber = 1;
      this.activeTime.time = commJs.dateHandle(
        this.activeTime.number === 2 ? item.start_time : item.end_time,
        "/"
      );
      this.loading = true;
      this.finished = false;
      this.selected = [];
      this.flashlist = [];
      this.flashGoodPageFun();
    },
    // 点击商品
    joinActivity(item) {
      // console.log(item);
      if (item.percent === 100) {
        commJs.Dialog2("秒杀商品已售罄", "warning", 1000, this);
        return;
      }
      if (
        item.point > this.getUser.usableRemain &&
        item.pointLimit > this.getUser.usableRemain
      ) {
        commJs.Dialog2("积分余额不满足参加秒杀的条件", "warning", 1000, this);
        return;
      }
      this.$router.push({
        path: `/goodsinfo?goodId=${item.goodId}&flashGoodId=${
          item.percent == 100 ? "" : item.flashGoodId
        }&u=/flash_sale_list`
      });
    }
  },
  computed: {
    ...mapGetters(["getUser"])
  },
  mounted() {
    this.flashListTimeFun();
    this.timestamp = parseInt(new Date().getTime() / 1000);
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
/deep/ .yd-navbar {
  background: #48403e !important;
}
// 时间列表
.timeList {
  display: flex;
  align-items: center;
  width: 100%;
  height: 3.5rem;
  line-height: 1.3rem;
  overflow-x: scroll;
  overflow-y: hidden;
  white-space: nowrap;
  font-family: PingFang-SC-Medium;
  background: #48403e;
  color: #ffffff;
  .flash_time {
    text-align: center;
    padding: 0 1rem;
    .time {
      font-size: 0.875rem;
    }
    .font {
      font-size: 0.75rem;
      span {
        border-bottom: 3px solid #48403e;
        padding-bottom: 0.3rem;
      }
    }
  }
}
.activeClass,
.newActiveClass {
  border-bottom: 3px solid #3ec000 !important;
  font-size: 0.875rem;
}
.newActiveClass {
  border-bottom: 3px solid #d7000f !important;
}
.activeTime {
  font-size: 1rem !important;
}
// 头部
.haeder {
  position: relative;
}
// 精品
.shop {
  background: #ffffff;
  margin: 0.9375rem;
  border-radius: 0.625rem;
  padding: 0.6875rem;
  position: absolute;
  // width:100%;
  width: 92%;
  top: 3rem;
  .title {
    font-size: 1rem;
    color: #333333;
    font-family: PingFang-SC-Bold;
    font-weight: bold;
    margin-bottom: 0.625rem;
  }
}
.shopList {
  padding: 0 0.9375rem;
  /deep/ .shop_div {
    // background: red;
    padding: 0.625rem 0;
    border-bottom: 1px solid #e6e6e6;
  }
}
.back {
  width: 100%;
  height: 7rem;
  background: #48403e;
  border-bottom-right-radius: 50%;
  border-bottom-left-radius: 50%;
}
// 秒杀列表标题
.titleList {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  font-size: 0.9375rem;
  color: #333333;
  align-items: center;
  padding: 0.5rem 0.9375rem;
  border-bottom: 1px solid #e6e6e6;
  .title_describe,
  .title_time {
    margin-top: 0.2rem;
  }
}

.count_down {
  margin-left: 0.3rem;
  /deep/ span i {
    background: #333333;
    color: #ffffff;
    padding: 0.1rem 0.2rem;
    font-size: 0.75rem;
  }
}
.list {
  background: #ffffff;
}
/deep/ .yd-navbar:after {
  display: none;
}
</style>

