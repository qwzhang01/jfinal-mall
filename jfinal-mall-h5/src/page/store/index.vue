<template>
  <div>
    <!-- 搜索 -->
    <div class="Search" v-if="true">
      <div class="left">
        <router-link :to="navbar.left[0].to">
          <van-icon name="arrow-left" size="1rem"/>
        </router-link>
      </div>
      <Search @searchFun="searchFun" :search="search"/>
    </div>
    <!-- </transition> -->

    <!-- 店铺名字 -->
    <div class="store_box">
      <img v-lazy="storeBack" alt="背景图" class="storeBack">
      <div class="store">
        <div class="store_left">
          <div class="store_logo">
            <img :src="store_data.storeLogo" alt="logo">
          </div>
          <div class="store_info">
            <div class="store_name">{{store_data.storeName}}</div>
            <div class="store_data">
              <div>销量 {{store_data.saleCount}}</div>
              <div>粉丝 {{store_data.fansCount}}</div>
            </div>
          </div>
        </div>
        <div class="store_right">
          <yd-button
            type="hollow"
            :class="[store_data.isCollect?'NoFollow':'Follow']"
            @click.native="storeBtn(store_data.isCollect)"
          >{{store_data.isCollect?'已关注':'未关注'}}</yd-button>
        </div>
      </div>
    </div>

    <!-- 导航 -->
    <van-tabs v-model="active" class="van-tabs" @click="Switch">
      <!-- 活动 -->
      <div v-if="active == 0">
        <!-- 优惠券 -->
        <div class="box" v-if="store_data.coupons.length > 0">
          <div class="lable">优惠活动</div>
          <Coupon :type="3" :coupon="coupon"/>
        </div>
        <!-- 大家正在抢 -->
        <div class="box" v-if="store_data.activityGood.length > 0">
          <div class="lable2" @click="more">
            <span>大家正在抢</span>
            <span>
              查看全部
              <van-icon name="arrow"/>
            </span>
          </div>
          <List :list_type="'3'" :list="storeList" class="storeList" @introduce="Good"/>
        </div>
      </div>
      <van-tab v-for="(item,key) in nav" :title="item" :key="key">
        <!-- 宝贝推荐 -->
        <div v-if="active == 0" class="baby">
          <div class="lable3">
            <img src="../../assets/recommend_store.png" alt="推荐">
            <span>宝贝推荐</span>
          </div>
          <!-- 筛选 -->
          <Lable
            :distanceActive="distanceActive"
            :screen="screen"
            :screenIndex="screenIndex"
            :arrowactive="arrowactive"
            @screenFun="screenFun"
          />
          <van-list
            v-model="loading"
            :finished="finished"
            finished-text="没有更多了"
            @load="goodListFun"
            id="screen"
          >
            <List :list_type="5" :theme="1" :list="list" @introduce="Good"/>
          </van-list>
        </div>
        <!-- 新品 -->
        <div v-if="active == 1">
          <!-- 筛选 -->
          <Lable
            :distanceActive="distanceActive"
            :screen="screen"
            :screenIndex="screenIndex"
            :arrowactive="arrowactive"
            @screenFun="screenFun"
          />
          <van-list
            v-model="loading"
            :finished="finished"
            finished-text="没有更多了"
            @load="goodListFun"
            id="screen"
          >
            <List :list_type="5" :theme="1" :list="list" @introduce="Good"/>
          </van-list>
        </div>
        <!-- 促销 -->
        <div v-if="active == 2">
          <van-list
            v-model="loading"
            :finished="finished"
            finished-text="没有更多了"
            @load="goodListFun"
            id="screen"
          >
            <List :list_type="4" :list="list" @joinActivity="joinActivity"/>
          </van-list>
        </div>
      </van-tab>
    </van-tabs>

    <!-- 筛选 -->
    <yd-popup v-model="show" position="right" width="80%">
      <Screen :screenFrom="screenFrom" @selectFun="selectFun"/>
    </yd-popup>

    <!-- 分类弹窗 -->
    <div class="classification" v-if="classActive">
      <div class="classificationList">
        <div v-if="store_data.categoryHighLevel.length > 0">
          <van-cell
            v-for="(item, key) in store_data.categoryHighLevel"
            :key="key"
            :title="item.category"
            @click="classification(item)"
          />
        </div>
        <div v-else>暂无数据</div>
      </div>
      <div id="triangle-down"></div>
    </div>
    <!-- 蒙版 -->
    <div class="back" v-if="classActive" @click=" classActive = false "></div>

    <!-- 底部 -->
    <div class="foot">
      <div @click="classActive = !classActive">商品分类</div>
      <div>
        <a :href="'tel:'+store_data.servicePhone">联系客服</a>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import Lable from "../../components/lable";
import List from "../../components/list";
import Search from "../../components/search";
import Coupon from "../../components/coupon";
import Screen from "../../components/screen";
import commJs from "../../comm";
import { Dialog } from "vant";
import {
  storeDetail,
  collect,
  cancelCollect,
  goodList,
  searchForm
} from "../../../api/store";
import { setTimeout } from "timers";
export default {
  components: {
    Navbar,
    Search,
    List,
    Coupon,
    Lable,
    Screen
  },
  data() {
    return {
      // 筛选弹窗
      show: false,
      // 规格数据
      screenFrom: {
        // 起始价格
        startMoeny: "",
        // 结束价格
        endMoeny: "",
        // 品牌
        brand: [],
        // 规格
        specification: []
      },
      // 选中的规格
      selectNorms: {},
      store_id: "",
      // 背景图片
      storeBack: "",
      // 1 推荐 2 新品 3 促销 4 商品分类
      type: 1,
      sortColumn: "",
      sortType: "ASC",
      pageNumber: 1,
      pageSize: 10,
      totalPage: 1,
      // 商品分类ID
      categoryId: "",
      loading: false,
      finished: false,
      // 1：从高价格开始排序  2：从低价格开始排序
      arrowactive: 0,
      // 离指定id的距离
      distance: 0,
      distanceActive: false,
      //分类状态
      classActive: false,
      list: [], // 列表数据
      // 宝贝筛选
      screen: ["综合", "销量", "价格", "筛选"],
      screenIndex: 0,
      store_data: {
        activityGood: [],
        coupons: [],
        categoryHighLevel: [],
        storeName: ""
      },
      // 搜索状态
      searchActive: false,
      search: "",
      // 导航菜单
      nav: ["推荐", "新品", "促销"],
      // 滚动加载
      active: 0,

      //优惠券
      coupon: [
        {
          money: 20,
          ps: "满199元可用",
          active: true
        },
        {
          money: 200,
          ps: "满199元可用",
          active: false
        }
      ],
      // 大家正在抢
      storeList: [],
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
          coment: "卖家店铺",
          //颜色
          color: "#333333"
        },
        right: [
          {
            icon_img: require("../../assets/search.png"),
            // 目标地址
            to: "",
            coment: "",
            color: "Search",
            // 图标大小
            size: "1rem"
          },
          {
            icon_img: require("../../assets/icon_more.png"),
            // 目标地址
            to: "",
            // 图标大小
            size: "1rem"
          }
        ]
      }
    };
  },
  methods: {
    // 点击产品详情
    navRight(nav) {
      // console.log(nav);
      // 点击搜索
      if (nav.color == "Search") {
        this.searchActive = !this.searchActive;
      }
    },
    // 搜索
    searchFun(data) {
      console.log(data);
      this.search = data;
      if (!data) {
        this.searchActive = !this.searchActive;
      } else {
        commJs.history(data);
      }

      this.fun();
    },
    // 获取首页详情
    storeDetailFun(active = false) {
      storeDetail({ storeId: this.store_id }).then(res => {
        this.$dialog.loading.close();
        if (res.status == 0) {
          this.store_data = Object.assign(this.store_data, res.data);
          this.storeBack = res.data.storeBanner;
          // 大家正在抢
          if (res.data.activityGood.length > 0) {
            if (active) {
              this.storeList = [];
            }
            this.storeList.push(
              ...res.data.activityGood.map(item => {
                item.image = item.originalImg;
                item.shop_price = item.price;
                item.text = `已抢5${item.buyNum}件`;
                item.goods_name = item.title;
                return item;
              })
            );
          }
        } else {
          commJs.Dialog2(res.msg, "error", 1000, this);
        }
      });
    },
    // 筛选
    screenFun(key) {
      this.screenIndex = key;
      this.pageNumber = 1;
      this.categoryId = "";
      // 综合
      if (key == 0) {
        console.log("综合");
        this.sortColumn = "";
        this.sortType = "DESC";
      }

      // 销量
      if (key == 1) {
        console.log("销量");
        this.sortColumn = "sale";
        this.sortType = "DESC";
      }

      // 价格
      if (key == 2) {
        console.log("价格");
        this.sortColumn = "price";
        if (this.arrowactive == 1) {
          this.sortType = "DESC";
        } else {
          this.sortType = "ASC";
        }
        this.arrowactive = this.arrowactive == 1 ? 2 : 1;
      } else {
        this.arrowactive = 0;
      }
      this.loading = false;
      this.finished = false;
      this.goodListFun(true);
      // 筛选
      if (key == 3) {
        console.log("筛选");
        this.show = !this.show;
        return;
      }
    },
    // 筛选结果
    selectFun(item) {
      console.log(item);
      this.show = !this.show;
      this.selectNorms = item;
      this.fun();
      this.screenIndex = 3;
    },
    // 店铺关注
    storeBtn(active) {
      let that = this;
      // 判断是否登录
      if (
        commJs.testLogin(
          this,
          `/storepage?store_id=${this.store_id}&u=${this.navbar.left[0].to}`,
          false
        ) == false
      ) {
        return;
      }
      this.$dialog.loading.open("请求中...");
      if (active) {
        var ajax = cancelCollect({ storeId: this.store_id });
      } else {
        var ajax = collect({ storeId: this.store_id });
      }
      ajax.then(res => {
        if (res.status == 0) {
          that.storeDetailFun(true);
        } else {
          commJs.Dialog2(res.msg, "error", 1000, this);
        }
      });
    },
    // 查看商品
    Good(item) {
      commJs.deletData(["lotteryGoodId", "flashGoodId"]);
      let url = "/storepage?store_id=" + this.store_id;
      commJs.jump(item, this, url);
    },
    // 参加活动
    joinActivity(data) {
      commJs.deletData(["lotteryGoodId", "flashGoodId"]);
      let url =
        "/storepage?store_id=" +
        this.store_id +
        "&lotteryGoodId=" +
        data.lotteryId;
      commJs.jump({ item: data }, this, url);
    },
    onScroll() {
      let scrolled =
        document.documentElement.scrollTop || document.body.scrollTop;

      if (scrolled > this.distance) {
        // 大一指定id距离
        this.distanceActive = true;
      } else {
        // 小于指定id距离
        this.distanceActive = false;
      }
    },
    fun() {
      this.list = [];
      this.arrowactive = 0;
      setTimeout(() => {
        window.scrollTo(0, 0);
        this.distance = document.getElementById("screen").offsetTop;
        // console.log(this.distance);
      }, 1000);
      this.distanceActive = false;
      this.screenIndex = 0;
      this.loading = false;
      this.finished = false;
      this.pageNumber = 1;
      this.sortColumn = "";
      this.sortType = "ASC";
      setTimeout(() => {
        this.goodListFun(true);
      }, 300);

      // return;
    },
    // 切换导航
    Switch(item) {
      setTimeout(() => {
        window.scrollTo(0, 0);
        this.distance = document.getElementById("screen").offsetTop;
        // console.log(this.distance);
      }, 800);
      this.type = item + 1;
      this.selectNorms = {};
      this.searchFormFun();

      this.fun();
    },
    // 查看更多
    more() {
      this.active = 2;
      this.Switch(2);
    },
    // 点击分类
    classification(item) {
      console.log(item);
      // this.classActive =false;
      // this.type = 4;
      // this.categoryId = item.categoryId;
      this.$router.push({
        path: `/storeClassList?storeId=${this.store_id}&categoryId=${
          item.categoryId
        }&title=${item.category}&u=${this.navbar.left[0].to}`
      });
    },
    // 店铺商品
    goodListFun(active = false) {
      let that = this;
      let data = Object.assign(
        {
          storeId: this.store_id,
          type: this.type,
          sortColumn: this.sortColumn,
          sortType: this.sortType,
          categoryId: this.categoryId,
          pageNumber: this.pageNumber,
          pageSize: this.pageSize,
          goodName: this.search
        },
        this.selectNorms
      );
      goodList(data).then(res => {
        if (res.status == 0) {
          // 总页数 > 当前页数
          setTimeout(() => {
            if (res.data.totalPage >= that.pageNumber) {
              that.pageSize = res.data.pageSize;
              that.totalPage = res.data.totalPage;
              if (active) {
                that.list = [];
              }
              that.list.push(...res.data.list);
              that.pageNumber = parseInt(that.pageNumber) + 1;
            } else {
              that.finished = true;
            }
            // 加载状态结束
            that.loading = false;
          }, 500);
        }
      });
    },
    // 自定义查询
    searchFormFun() {
      let that = this;
      let data = {
        storeId: this.store_id,
        goodName: this.search
      };
      searchForm(data).then(res => {
        // console.log(res);
        if (res.status == 0) {
          that.screenFrom = {};
          that.screenFrom = Object.assign(that.screenFrom, res.data);
        }
      });
    }
  },
  mounted() {
    if (this.$route.query.u) {
      this.navbar.left[0].to = commJs.jumpUrl(this, this.navbar.left[0].to, [
        "type"
      ]);
    } else {
      this.navbar.left[0].to = "/";
    }

    // console.log(this.navbar.left[0].to);
    if (this.$route.query.store_id) {
      this.store_id = this.$route.query.store_id;
      this.storeDetailFun();
      // this.goodListFun();
      this.searchFormFun();
      this.$nextTick(function() {
        setTimeout(() => {
          this.distance = document.getElementById("screen").offsetTop;
          window.addEventListener("scroll", this.onScroll);
        }, 2000);
      });
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
// 搜索
.slide-fade-enter-active {
  transition: all 0.3s ease;
}
.slide-fade-enter,
.slide-fade-leave-to {
  transform: translateX(12rem);
  opacity: 0;
}
/deep/ .van-search {
  background: none !important;
}
.Search {
  display: flex;
  // background: #ffffff;
  align-items: center;
  font-size: 0.8rem;
  width: 100%;
  position: absolute;
  top: 0.375rem;
  z-index: 100;
  // background-size: 100% 100%;
  .left {
    display: flex;
    align-items: center;
    padding-top: 0.5rem;
    justify-content: center;
    color: #333333;
    width: 1.5rem;
    margin-left: 0.5rem;
  }
  .search,
  /deep/ .van-search {
    width: 100%;
  }
  /deep/ .van-field__control {
    padding-top: 0.1875rem;
  }
}
// 店铺
.store_box {
  position: relative;
  .storeBack {
    position: absolute;
    width: 100%;
    height: 100%;
  }
  .store {
    z-index: 100;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 0.9375rem;
    height: 8.4375rem;

    .store_left {
      z-index: 100;
      display: flex;
      align-items: center;
      color: #ffffff;
      font-family: PingFang SC;
      margin-top: 1.875rem;
      .store_logo {
        border: 2px solid #ffffff;
        height: 2.8125rem;
        width: 2.8125rem;
        img {
          width: 100%;
          height: 100%;
        }
      }
      .store_info {
        margin-left: 0.625rem;
        .store_name {
          font-size: 0.875rem;
        }
        .store_data {
          font-size: 0.75rem;
          display: flex;
          align-items: center;
          div:nth-child(1) {
            padding-right: 0.5rem;
          }
          div:nth-child(2) {
            padding-left: 0.5rem;
            border-left: 1px solid #ffffff;
          }
        }
      }
    }
    .store_right {
      z-index: 100;
      margin-top: 1.875rem;
      button {
        font-size: 0.625rem;
        height: 1.5rem;
        border-radius: 2.5rem;
        border: none;
      }
      .Follow {
        background: #fb3154;
        color: #ffffff;
      }
      .NoFollow {
        background: #ffffff;
        color: #fb3154;
      }
    }
  }
}

// 优惠活动
.box {
  padding: 0.9375rem;
  background: #ffffff;
  margin-bottom: 0.625rem;
  .lable {
    margin-bottom: 0.5rem;
    color: #999999;
    font-size: 0.75rem;
    font-family: PingFang SC;
  }
}
// 大家正在抢
.lable2 {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  span:nth-child(1) {
    color: #333333;
    font-family: PingFang SC;
    font-size: 0.9375rem;
  }
  span:nth-child(2) {
    display: flex;
    align-items: center;
    color: #999999;
    font-family: PingFang SC;
    font-size: 0.875rem;
  }
}
// 宝贝推荐
.baby {
  background: #ffffff;
  // padding: 0 .9375rem;
  .lable3 {
    text-align: center;
    font-size: 0.9375rem;
    padding-top: 0.9375rem;
    img {
      width: 0.875rem;
      height: 0.875rem;
    }
  }
}

.van-tabs {
  margin-bottom: 3rem;
}
.foot {
  display: flex;
  height: 2.8125rem;
  position: fixed;
  bottom: 0;
  background: #ffffff;
  width: 100%;
  font-size: 1rem;
  align-items: center;
  z-index: 120;
  div {
    text-align: center;
    width: 50%;
  }
  div:nth-child(2) {
    background: #d7000f;
    color: #ffffff;
    height: 100%;
    line-height: 3rem;
    a {
      width: 100%;
      height: 100%;
      display: block;
    }
  }
}

// 商品列表

/deep/ .yd-list-theme1 .yd-list-item .yd-list-title {
  font-weight: inherit;
}
/deep/ .yd-list-other {
  height: 1.8rem;
}
/deep/ .demo-list-price {
  font-size: 1rem;
  color: #d7000f;
}

// 分类弹窗
.classification {
  position: fixed;
  bottom: 3.5rem;
  left: 0.8rem;
  z-index: 120;
  .classificationList {
    text-align: center;
    color: #333333;
    height: 10rem;
    width: 11rem;
    overflow-x: hidden;
    font-size: 1rem;
    /deep/ .van-cell {
      background: #f5f5f5;
      border-bottom: 1px solid #e6e6e6;
      /deep/ span {
        word-wrap: normal;
        text-overflow: ellipsis;
        white-space: nowrap;
        overflow: hidden;
        width: 9.5rem;
        display: block;
      }
    }
  }
  #triangle-down {
    margin: 0 auto;
    width: 0;
    height: 0;
    border-left: 1rem solid transparent;
    border-right: 1rem solid transparent;
    border-top: 0.625rem solid #f5f5f5;
  }
}
/deep/ .van-cell:not(:last-child)::after {
  display: none;
}

/deep/ .van-cell:nth-last-child(1) {
  border-bottom: none !important;
}
.back {
  background: #999999;
  opacity: 0.5;
  width: 100%;
  z-index: 100;
  bottom: 0;
  height: 100%;
  position: fixed;
}
/deep/.van-field__control {
  padding-top: 0.1875rem;
}
/deep/ .van-tab--active {
  color: #d7000f;
}
/deep/ .van-tabs__line {
  background: #d7000f;
}
/deep/ .transverse_list {
  font-family: PingFang SC;
}
</style>

