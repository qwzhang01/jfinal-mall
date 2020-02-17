<template>
  <div>
    <!-- banner头部 -->
    <div class="banner">
      <!-- 搜索栏 -->
      <div class="search_div">
        <div class="style_search">
          <!-- 选择地理位置 -->
          <div class="address">
            <span>{{city}}</span>
            <span>
              <img src="../assets/navbar_xiala.png" alt="下拉">
            </span>
          </div>
          <!-- 搜索框 -->
          <div class="search" @click="introduceSelf({item:{jump_type:7}},null,this)">
            <van-search
              placeholder="请输入搜索关键词"
              v-model="page_search"
              background="none"
              disabled="false"
            />
          </div>
          <!-- 消息 -->
          <div class="sms">
            <img src="../assets/navbar_sms.png" alt="消息">
          </div>
        </div>
      </div>
      <!-- 分类 -->

      <!--轮播图 -->
      <Banner
        :vertical="0"
        :indicators="0"
        :images="page_banner"
        v-if="page_banner"
        @introduce="introduceSelf"
      />
    </div>
    <!-- 商城通知 -->
    <van-notice-bar v-if="page_notice" :text="page_notice" left-icon="volume-o"/>
    <!-- 导航 -->
    <div class="nav" v-if="page_nav">
      <div class="nav_div" v-for="(item,key) in page_nav" :key="key">
        <div class="nav_btn" @click="introduceSelf({item})">
          <div class="nav_btn_img">
            <img v-lazy="item.image" :alt="item.title">
          </div>
          <div class="nav_btn_font">{{item.title}}</div>
        </div>
      </div>
    </div>
    <!-- 管家资讯 -->
    <div class="information">
      <div class="information_left">
        <span>管家</span>
        <span>资讯</span>
      </div>
      <div v-if="zi_xun" class="information_rigth">
        <yd-rollnotice autoplay="2000">
          <yd-rollnotice-item v-for="(item,key) in zi_xun" :key="key">
            <router-link to="/infoList" class="routerInfo">
              <div class="coment">{{item.title}}</div>
            </router-link>
          </yd-rollnotice-item>
        </yd-rollnotice>
        <van-icon name="arrow" color="#999999"/>
      </div>
    </div>
    <!-- 营销活动 -->
    <!-- <div class="promote" v-if="page_promote"  @click="introduceSelf({item:page_promote})">
        <img v-lazy="page_promote.image" alt="活动" />
    </div>-->
    <div class="promote" @click="shop_discount(3)">
      <img src="../assets/rushShopping.png" alt="活动">
    </div>

    <!-- 管家推荐 -->
    <div class="recommend">
      <!-- 标题 -->
      <div class="title">
        <span>管家推荐</span>
        <span>精品百货</span>
      </div>
      <!-- 内容 -->
      <List :list="list_data_1" :list_type="1" :theme="1">
        <div slot="img_title" class="img_title" slot-scope="props">
          <!-- 限时抢购 和 团购-->
          <div class="rob_shopping" @click="shop_discount(props.text.type)">
            <div class="time_div">
              <span
                :class="[props.text.type==1?'rob_title':'r_title']"
                slot="left"
              >{{props.text.type==1?'限时抢购':'超值团购'}}</span>
              <!-- 抢购时，开启时间 -->
              <div v-if="props.text.type == 1">
                <yd-countdown class="time" :time="page_fascinate.secKill.time">
                  <!-- <em>{%d0}</em><em>{%d1}</em><em>{%d2}</em>天 -->
                  <span class="time_1">
                    <em>{%h1}</em>
                    <em>{%h2}</em>
                  </span>
                  <i>:</i>
                  <span class="time_1">
                    <em>{%m1}</em>
                    <em>{%m2}</em>
                  </span>
                  <i>:</i>
                  <span class="time_1">
                    <em>{%s1}</em>
                    <em>{%s2}</em>
                  </span>
                </yd-countdown>
              </div>
            </div>
            <!-- 标题和物品 -->
            <div class="recommend_shop">
              <div class="recommend_shop_title">
                <!-- 标题 -->
                <div
                  class="title_shop"
                >{{props.text.type==1? page_fascinate.secKill.desc : page_fascinate.teamShop.desc}}</div>
                <!-- 折扣 -->
                <div class="shop_discount">
                  <span>{{props.text.type==1? page_fascinate.secKill.promtDesc :page_fascinate.teamShop.promtDesc}}</span>
                </div>
              </div>
              <!-- 物品 -->
              <div class="recommend_shop_img">
                <img
                  :src="props.text.type==1?page_fascinate.secKill.image:page_fascinate.teamShop.image"
                  alt="沙发"
                  class="jian_tou_right"
                >
              </div>
            </div>
          </div>
        </div>
      </List>
    </div>
    <!-- 主推分类 -->
    <div class="recommend" v-if="page_flagship_category">
      <List
        :list="page_flagship_category"
        class="list_main"
        :list_type="1"
        :theme="1"
        @introduce="introduceSelf"
      />
    </div>
    <!-- 智慧生活 一级主打产品 -->
    <div class="recommend wisdom">
      <!-- 标题 -->
      <div class="title">
        <span>智慧生活</span>
        <!-- <span>更多 <img src="../assets/you_jian_tou.png" alt="更多" class="jian_tou_right" /></span> -->
      </div>
      <!-- 类别一 -->
      <div
        class="guang_gao"
        v-if="page_flagship_major.first"
        @click="introduceSelf({item:page_flagship_major.first})"
      >
        <img v-lazy="page_flagship_major.first.image" :alt="page_flagship_major.first.name">
      </div>
      <!-- 类别 -->
      <List
        :list="page_flagship_major.end"
        class="list_main"
        :list_type="1"
        :theme="1"
        @introduce="introduceSelf"
        v-if="page_flagship_major.end"
      />
    </div>
    <!-- 热销榜单 -->
    <div class="recommend">
      <!-- 标题 -->
      <div class="title">
        <span>热销榜单</span>
      </div>
      <!-- 热销榜单 列表 -->
      <div class="shop_ranking_div">
        <div
          class="shop_ranking"
          v-for="(item,key) in shop_ranking.topSellingGood"
          :key="key"
          @click="introduceSelf({item:key==0? shop_ranking.topSellingGood[1]: key==1?shop_ranking.topSellingGood[0]: shop_ranking.topSellingGood[2]})"
        >
          <!-- 商品图片 -->
          <div class="shop_ranking_img">
            <img
              :src="key==0? require('../assets/2.png'):key==1?require('../assets/1.png'):require('../assets/3.png')"
            >
            <img
              v-lazy="key==0? shop_ranking.topSellingGood[1].image: key==1?shop_ranking.topSellingGood[0].image: shop_ranking.topSellingGood[2].image"
            >
          </div>
          <!-- 好评率 -->
          <div class="shop_ranking_coment">
            <div>{{key==0? shop_ranking.topSellingGood[1].name: key==1?shop_ranking.topSellingGood[0].name: shop_ranking.topSellingGood[2].name}}</div>
            <div>{{key==0? shop_ranking.topSellingGood[1].promotDesc: key==1?shop_ranking.topSellingGood[0].promotDesc: shop_ranking.topSellingGood[2].promotDesc}}</div>
          </div>
        </div>
      </div>
      <!-- 热销类型 -->
      <div class="ranking_list_div">
        <div
          class="ranking_list"
          v-for="(item,key) in shop_ranking.topHot"
          :key="key"
          @click="introduceSelf({item})"
        >
          <div class="shop_introduce">
            <div>{{item.rank_title}}</div>
            <div>
              <img src="../assets/icon_1.png">
              <span>{{item.name}}</span>
            </div>
            <div>{{item.promot_desc}}</div>
          </div>
          <div class="shop_icon">
            <img v-lazy="item.image" alt>
          </div>
        </div>
      </div>
    </div>
    <!-- 旅游出行 -->
    <div class="recommend wisdom lv_you" v-if=" Page_flagship_vice.length > 0 ">
      <!-- 标题 -->
      <div class="title">
        <span>旅游出行</span>
        <!-- <span>更多 <img src="../assets/you_jian_tou.png" alt="更多" class="jian_tou_right" /></span> -->
      </div>
      <!-- 列表 -->
      <List :list_type="2" :list="Page_flagship_vice" @introduce="introduceSelf"/>
    </div>
    <!-- 猜你喜欢 -->
    <div class="like" v-if=" like.list.length > 0 ">
      <!-- 标题 -->
      <div class="title">
        <span>猜你喜欢</span>
      </div>
      <!-- 列表 -->
      <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="PageFavouriteFun"
      >
        <List :list_type="1" :list="like.list" :theme="1" @introduce="introduceSelf"/>
      </van-list>
    </div>

    <!-- 万物互联 -->
    <!-- <div class="connect">
          <img src="../assets/connect.png" alt="万物互联" />
    </div>-->
    <yd-backtop></yd-backtop>
    <!-- 空盒子 -->
    <div class="empty"></div>
    <!-- 底部导航 -->
    <Nav class="foot_nav" :btn="1"/>
  </div>
</template>

<script>
import {
  PegeBanner,
  PageSearch,
  PageNav,
  PageZixun,
  PagePromote,
  PageflagshipCategory,
  PageflagshipMajor,
  PagehotRank,
  PageflagshipVice,
  PageFavourite,
  PageFascinate
} from "../../api";
import Banner from "../components/banner";
import List from "../components/list";
import Nav from "../components/nav";
import comm_js from "../comm/index";
export default {
  components: {
    Banner,
    List,
    Nav
  },
  data() {
    return {
      // 搜索框
      page_search: "",
      // banner
      page_banner: [],
      // 商城通知
      page_notice: "",
      // 导航按钮
      page_nav: [],
      //资讯
      zi_xun: [],
      // 营销活动
      page_promote: "",
      // 限时抢购 和 团购
      list_data_1: [{ type: 1 }, { type: 2 }],
      // 主推产品分类
      page_flagship_category: [],
      // 智慧生活
      page_flagship_major: {
        first: [],
        end: []
      },
      // 热销榜
      shop_ranking: {
        topSellingGood: [],
        topHot: []
      },
      // 旅游出行
      Page_flagship_vice: [],
      // 猜你喜欢
      like: {
        pageSize: 10, // 每条显示页数
        pageNumber: 1, // 当前页数
        list: [], // 列表数据
        totalPage: 1 // 总页数
      },
      // 滚动加载
      loading: false,
      finished: false,
      // 资讯弹窗
      xun_show: false,
      // 城市
      city: "深圳",
      // 秒杀和拼团
      page_fascinate: {
        // 秒杀
        secKill: {
          // 说明
          desc: "",
          // 促销说明
          promtDesc: "",
          // 时间
          time: "",
          // 图片地址
          image: ""
        },
        //拼团
        teamShop: {
          // 说明
          desc: "",
          // 促销说明
          promtDesc: "",
          // 图片地址
          image: ""
        }
      }
    };
  },
  methods: {
    /**
     * joker
     * 获取banner
     */
    PegeBannerFun() {
      let that = this;
      PegeBanner({}).then(res => {
        if (res.status == 0) {
          that.page_banner = [...res.data];
        }
      });
    },
    /**
     * joker
     * 获取默认搜索的内容
     */
    PageSearchFun() {
      let that = this;
      PageSearch({}).then(res => {
        if (res.status == 0) {
          that.page_search = res.data ? res.data : "";
        }
      });
    },
    /**
     * joker
     * 获取首页金刚区按钮
     */
    PageNavFun() {
      let that = this;
      PageNav({}).then(res => {
        if (res.status == 0) {
          that.page_nav = [...res.data];
        }
      });
    },
    /**
     * joker
     * 获取首页咨询
     */
    PageZixunFun() {
      let that = this;
      PageZixun({}).then(res => {
        if (res.status == 0) {
          that.zi_xun = [...res.data];
        }
      });
    },
    /**
     * joker
     * 获取首页推广信息
     */
    PagePromoteFun() {
      let that = this;
      PagePromote({}).then(res => {
        if (res.status == 0) {
          that.page_promote = res.data;
        }
      });
    },
    /**
     * joker
     * 获取主打分类产品
     */
    PageflagshipCategoryFun() {
      let that = this;
      PageflagshipCategory({}).then(res => {
        if (res.status == 0) {
          that.page_flagship_category = res.data;
        }
      });
    },
    /**
     * joker
     * 获取一级主打产品(智慧生活)
     */
    PageflagshipMajorFun() {
      let that = this;
      PageflagshipMajor({}).then(res => {
        if (res.status == 0) {
          if (res.data.length > 0) {
            // 数组首个元素
            that.page_flagship_major.first = res.data[0];
            // 剩下的
            res.data.shift();
            that.page_flagship_major.end = res.data ? res.data : [];
          }
        }
      });
    },
    /**
     *joker
     * 获取热销榜
     */
    PagehotRankFun() {
      let that = this;
      PagehotRank({}).then(res => {
        if (res.status == 0) {
          that.shop_ranking.topSellingGood = res.data.topSellingGood;
          that.shop_ranking.topHot = res.data.topHot;
        }
      });
    },
    /**
     * joker
     * 获取二级主打产品信息(旅游出行)
     */
    PageflagshipViceFun() {
      let that = this;
      PageflagshipVice({}).then(res => {
        if (res.status == 0) {
          that.Page_flagship_vice = res.data;
        }
      });
    },
    /**
     * joker
     * 猜你喜欢
     * @param pageSize number   当前条数
     * @param pageNumber number 当前页数
     */
    PageFavouriteFun() {
      let that = this;
      let params = {
        pageSize: that.like.pageSize,
        pageNumber: that.like.pageNumber
      };
      PageFavourite(params).then(res => {
        if (res.status == 0) {
          // 总页数 > 当前页数
          setTimeout(() => {
            if (res.data.totalPage >= that.like.pageNumber) {
              that.like.pageSize = res.data.pageSize;
              that.like.totalPage = res.data.totalPage;
              that.like.list.push(...res.data.list);
              that.like.pageNumber = parseInt(that.like.pageNumber) + 1;
            } else {
              that.finished = true;
            }
            // 加载状态结束
            that.loading = false;
          }, 500);
        }
      });
    },
    /**
     * 点击跳转
     */
    introduceSelf(data) {
      let that = this;
      comm_js.jump(data, that);
    },
    /**
     * 获取城市
     */
    showPosition() {
      var that = this;
      var geolocation = new BMap.Geolocation();
      geolocation.getCurrentPosition(function(r) {
        if (this.getStatus() == BMAP_STATUS_SUCCESS) {
          // 获取经纬度
          let lat = r.point.lat;
          let lng = r.point.lng;
          let province = r.address.province; // 省
          var str = r.address.city.toString();
          that.city = str.substr(0, str.length - 1); //  市
        }
      });
    },
    /**
     * joker
     * 打开优惠（限时抢购，拼团）
     */
    shop_discount(type) {
      // 限时抢购
      if (type == 1) {
        var path = `/flash_sale_list`;
      } else if (type == 2) {
        // 拼团
        var path = `/groupBuyingList`;
      } else if (type == 3) {
        // 一元抢购
        var path = `/robShoppingList`;
      } else {
        return;
      }
      this.$router.push({
        path
      });
    },
    /**
     * joker
     * 秒杀和拼团
     */
    PageFascinateFun() {
      let that = this;
      PageFascinate({}).then(res => {
        // console.log(res);
        if (res.status == 0) {
          that.page_fascinate = res.data;
        }
        // console.log(that.page_fascinate);
      });
    }
  },
  mounted() {
    let that = this;
    this.PegeBannerFun();
    this.PageSearchFun();
    this.PageNavFun();
    this.PageZixunFun();
    this.PagePromoteFun();
    this.PageflagshipCategoryFun();
    this.PageflagshipMajorFun();
    this.PagehotRankFun();
    this.PageflagshipViceFun();
    this.PageFavouriteFun();
    this.showPosition();
    this.PageFascinateFun();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->

<style lang="less" scoped>
// banner头部
.banner {
  position: relative;
  height: 11.8125rem;

  // 搜索框
  .search_div {
    z-index: 100;
    position: absolute;
    top: 10%;
    font-size: 0.6rem;
    color: #ffffff;
    width: 100%;
    .style_search {
      padding: 0 0.9rem;
      width: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    // 选择地理位置
    .address {
      margin-right: 0.6rem;
      width: 5.5rem;
      span:nth-child(1) {
        margin-right: 0.375rem;
      }
      img {
        width: 0.625rem;
        height: 0.375rem;
      }
    }
    // 搜索框
    .search {
      width: 100%;
      height: 1.75rem;
      /deep/ .van-search {
        padding: 0;
        height: 1.75rem;
      }
      /deep/.van-search .van-cell {
        padding: 0 10px 0 0;
      }
      /deep/ .van-field__body {
        height: 1.75rem;
      }
      /deep/ .van-search__content {
        background-color: #ffffff;
        border-radius: 0.625rem;
      }
    }
    // 消息
    .sms {
      margin-left: 1rem;
      align-items: center;
      display: flex;
      img {
        width: 1.375rem;
        height: 1.375rem;
      }
    }
  }
  // 轮播
  /deep/.van-swipe,
  /deep/.van-swipe__track {
    width: 100%;
    height: 11.8125rem;
    img {
      width: 100%;
      height: 100%;
    }
  }
}

// 导航
.nav {
  display: flex;
  flex-wrap: wrap;
  background: #ffffff;
  border-bottom: 1px solid rgba(230, 230, 230, 1);
  .nav_div {
    width: 25%;
    height: 5.4375rem;
    display: flex;
    align-items: center;
    .nav_btn {
      font-size: 0.8rem;
      font-family: PingFang-SC-Medium;
      color: rgba(51, 51, 51, 1);
      margin: 0 auto;
      .nav_btn_img {
        img {
          width: 3rem;
          height: 3rem;
        }
      }
    }
  }
}

// 管家资讯
.information {
  display: flex;
  align-items: center;
  background: #ffffff;
  height: 3rem;
  // overflow: hidden;
  .information_left {
    margin: 0 0.3rem 0 0.9375rem;
    width: 25%;
    span:nth-child(1) {
      font-family: PangMenZhengDao;
      font-weight: bold;
      color: rgba(51, 51, 51, 1);
      font-size: 1.125rem;
    }
    span:nth-child(2) {
      font-family: PangMenZhengDao;
      color: #ffffff;
      font-size: 1rem;
      padding: 0 0.45rem 0 0.2rem;
      background: url("../assets/zi_xun.png") no-repeat center;
      background-size: 100% 100%;
    }
  }
  .information_rigth {
    align-items: center;
    width: 75%;
    display: flex;
    .zi_xun {
      width: 90%;
    }
    /deep/ .van-swipe__track,
    /deep/ .van-swipe-item,
    /deep/ .van-swipe {
      height: 2rem !important;
      width: 98% !important;
      line-height: 1rem;
      font-size: 0.6rem;
    }

    i {
      margin-right: 1.2rem;
    }
  }
}
//营销活动
.promote {
  width: 100%;
  margin-top: 0.9375rem;
  background: #ffffff;
  img {
    width: 100%;
  }
}
// 管家推荐
.recommend {
  margin-top: 0.5rem;
  background: #ffffff;
  padding: 0 0.9375rem 0.9375rem;

  // 标题
  .title {
    padding-top: 0.9375rem;
    padding-bottom: 0.4rem;
    span:nth-child(1) {
      font-family: PingFang-SC-Bold;
      color: #333333;
      font-size: 1rem;
    }
    span:nth-child(2) {
      font-family: PingFang-SC-Bold;
      color: #be9e6d;
      font-size: 0.875rem;
      margin-left: 0.3125rem;
    }
  }
  // 限时抢购
  .img_title {
    padding-left: 0.3rem;
    width: 98%;
    position: absolute;
    top: 10%;
    left: 0.2rem;
  }
  .time_div {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
  }
  /deep/ .yd-list-item {
    position: relative;
  }
  .rob_title {
    font-size: 1rem;
    color: #54c4b4;
    margin-bottom: 0.1rem;
    margin-right: 0.3rem;
    font-family: PingFang-SC-Heavy;
  }
  .r_title {
    font-size: 1rem;
    color: #e56a75;
    margin-bottom: 0.1rem;
    margin-right: 0.3rem;
    font-family: PingFang-SC-Heavy;
  }
  .time {
    /deep/ span .time_1 {
      color: #54c4b4;
      background: #54c4b4;
      color: #ffffff;
      padding: 0.1rem 0.2rem;
      border-radius: 0.1rem;
    }
    /deep/ i {
      color: #54c4b4;
    }
  }
  .title_shop {
    font-size: 0.8125rem;
    color: #666666;
    font-family: PingFang-SC-Medium;
    margin: 0.4375rem 0 0.75rem 0.1rem;
  }
  .shop_discount span {
    padding: 0.1rem 0.2rem;
    border-radius: 0.1rem;
    font-size: 0.8125rem;
    color: #ffffff;
    font-family: PingFang-SC-Medium;
    background: #ff5547;
  }
  /deep/ .yd-list-mes {
    height: 6.5625rem;
    padding: 0;
  }
  /deep/ .yd-list-img img {
    margin-top: 0;
    height: 6.5625rem;
  }

  @media screen and (max-width: 320px) {
    /deep/ .yd-list-mes {
      height: 6.8rem;
      padding: 0;
    }
  }
  /deep/ .yd-list-img {
    overflow: inherit;
    padding: 0;
  }
  .recommend_shop {
    display: flex;
    justify-content: space-between;
  }
  .recommend_shop_img {
    margin-top: 0.5rem;
    img {
      width: 3.3rem;
      height: 3.4rem;
      padding: 0.3rem;
    }
  }
  /deep/ .yd-list a:nth-child(1) .yd-list-mes {
    background: #f1faf6;
    border-radius: 0.25rem;
  }
  /deep/ .yd-list a:nth-child(2) .yd-list-mes {
    background: #fae8e1;
    border-radius: 0.25rem;
  }
}
// 主推分类
.list_main {
  padding-top: 0.9375rem;
}
// 右箭头
.jian_tou_right {
  width: 1.5rem;
  height: 1rem;
}
// 智慧生活
.wisdom {
  .title {
    padding-bottom: 1.25rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
    span:nth-child(2) {
      display: flex;
      align-items: center;
      color: #999999;
    }
  }
  //广告
  .guang_gao {
    width: 100%;
    height: 8.75rem;
    img {
      width: 100%;
      height: 100%;
    }
  }
}

// 热销榜单
.shop_ranking_div {
  display: flex;
  margin: 0.375rem 0 0.625rem 0;
  .shop_ranking {
    width: 8.4375rem;
    margin-right: 0.25rem;
    overflow: hidden;
    border-radius: 0.3125rem;
    .shop_ranking_img {
      position: relative;
      text-align: center;
      background: #f7f7f7;
      // 榜单图片
      img:nth-child(1) {
        width: 1.9375rem;
        height: 1.5625rem;
        position: absolute;
        top: 0;
        left: 0.2rem;
      }
      // 商品图片
      img:nth-child(2) {
        width: 100%;
        height: 7.8125rem;
        display: block;
      }
    }

    //  好评率
    .shop_ranking_coment {
      background: #f4e9d9;
      padding: 0.3rem 0.53125rem;
      div:nth-child(1) {
        font-size: 0.875rem;
        color: #333333;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        margin-bottom: 0.2rem;
      }
      div:nth-child(2) {
        color: #be9e6d;
        font-size: 0.625rem;
        text-align: center;
      }
    }
  }
}
// 热销类型
.ranking_list_div {
  display: flex;
  flex-wrap: wrap;
  .ranking_list {
    display: flex;
    width: 49%;
    margin: 0 0.1rem 0.1rem 0;
    height: 5rem;
    background: #f7f7f7;
    border-radius: 0.3125rem;
    padding: 0.625rem 0.625rem 0.625rem 0;
    justify-content: space-between;
    align-items: center;
    .shop_introduce {
      width: 60%;
      margin-right: 5%;
      div:nth-child(1) {
        color: #be9e6d;
        font-size: 0.6875rem;
        font-family: PingFang-SC-Bold;
        margin-left: 0.59375rem;
      }
      div:nth-child(2) {
        margin: 0.1rem 0;
        img {
          width: 0.09375rem;
          height: 0.75rem;
          margin-right: 0.3rem;
          position: relative;
          top: 1px;
        }
        font-size: 0.75rem;
        color: #333333;
        font-family: PingFang-SC-Medium;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      div:nth-child(3) {
        color: #999999;
        margin-left: 0.59375rem;
        font-size: 0.6875rem;
      }
    }
    .shop_icon {
      width: 35%;
      text-align: right;
      img {
        width: 100%;
      }
    }
  }
}
// 旅游出行
.like {
  margin-top: 0.9375rem;
  background: #ffffff;
  padding: 0 0.9375rem 0.9375rem;
  .title {
    font-family: PingFang-SC-Bold;
    color: #333333;
    font-size: 1rem;
    padding-top: 0.9375rem;
    padding-bottom: 1.25rem;
  }
  /deep/ .yd-list-title {
    font-weight: inherit;
    color: #333333;
    font-size: 0.875rem;
    margin: 0.625rem 0;
  }
  /deep/ .demo-list-price {
    color: #d7000f;
    font-size: 1rem;
  }
}
// 万物互联
.connect {
  position: fixed;
  bottom: 5rem;
  left: 1rem;
  img {
    width: 2.9375rem;
    height: 2.8125rem;
  }
}
// 空盒子
.empty {
  width: 100%;
  height: 5rem;
}

/deep/ .yd-backtop-inlay {
  width: 2.8rem;
  height: 2.8rem;
  bottom: 5.375rem !important;
}
.information_rigth {
  width: 70%;
  .routerInfo {
    width: 98%;
    .coment {
      width: 100%;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}
</style>
