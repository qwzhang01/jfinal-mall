<template>
  <div class="body">
    <!-- banner头部 -->
    <div class="banner">
      <!-- 搜索栏 -->
      <div class="search_div" :style="{'background': bannerBack, 'position':shownav?'fixed':''}">
        <div class="style_search">
          <!-- 选择地理位置 -->
          <div class="address">
            <span>{{city}}</span>
            <span>
              <img src="../assets/navbar_xiala.png" alt="下拉" />
            </span>
          </div>
          <!-- 搜索框 -->
          <div class="search">
            <router-link to="/search">
              <van-search
                placeholder="请输入搜索关键词"
                v-model="page_search"
                background="none"
                disabled="false"
              />
            </router-link>
          </div>
          <!-- 消息 -->
          <div class="sms">
            <img src="../assets/navbar_sms2.png" alt="消息" />
          </div>
        </div>
        <!-- 分类 -->
        <div class="classification_box">
          <div class="classification">
            <div
              class="classification_coment"
              v-for="(item,key) in classification"
              :key="key"
              @click="classFun(item,key)"
            >
              <span :class="[classification_index == key ?'active_class':'']">{{item.name}}</span>
            </div>
          </div>
          <img
            src="../assets/class_icon.png"
            alt="分类"
            class="class_icon"
            @click="shownav = !shownav"
          />
        </div>
        <!-- 分类背景 -->
        <transition name="slide-fade">
          <div class="shownav" v-show="shownav">
            <div class="shownavBack">
              <div clas="title">全部频道</div>
              <!-- 分类 -->
              <div class="classBody">
                <div
                  class="classNav"
                  v-for="(item,key) in classification.filter(item => item.id > 0 )"
                  :key="key"
                  @click="classFun(item, key+1 )"
                >
                  <div class="classNavImg">
                    <img v-lazy="item.image" :alt="item.name" />
                  </div>
                  <div class="classNavTitle">{{item.name}}</div>
                </div>
              </div>
            </div>
          </div>
        </transition>
      </div>
      <!--轮播图 -->
      <Banner
        :vertical="0"
        :indicators="0"
        :images="page_banner"
        v-if="page_banner"
        @introduce="introduceSelf"
        class="apgeBanner"
        @onChange="onChangeBanner"
      />
    </div>
    <!--  导航 每页显示10个 -->
    <div class="nav_box" v-if="page_nav.length > 0">
      <van-swipe indicator-color="#FD1517">
        <van-swipe-item
          v-for="(item,index) in Math.ceil( page_nav.length / 10 )"
          :key="index"
          class="nav"
          :style="{'height':index>=1?'10.875rem':'inherit'}"
        >
          <div
            class="nav_div"
            v-for="(item,key) in page_nav.slice( index == 0 ? 0 : index * 10, index == 0 ? 10 : (index + 1) * 10 )"
            :key="key"
            style="width: 20%;"
          >
            <div class="nav_btn" @click="introduceSelf({item})">
              <div class="nav_btn_img">
                <img v-lazy="item.img" :alt="item.title" />
              </div>
              <div class="nav_btn_font">{{item.title}}</div>
            </div>
          </div>
        </van-swipe-item>
      </van-swipe>
    </div>
    <!-- 管家资讯 -->
    <div class="info_box">
      <div class="information" v-if="zi_xun.length > 0">
        <div class="information_left">
          <span>管家</span>
          <span>资讯</span>
        </div>
        <div class="information_rigth" v-if="zi_xun">
          <router-link to="/infoList">
            <yd-rollnotice autoplay="4000">
              <yd-rollnotice-item v-for="(item,key) in zi_xun" :key="key">
                <div class="coment">{{item.title}}</div>
              </yd-rollnotice-item>
            </yd-rollnotice>
          </router-link>
        </div>
      </div>
    </div>
    <!-- 营销广告 -->
    <div
      class="promote"
      v-if="AdvertiseData.bigAdvertise.img || AdvertiseData.smallAdvertise.length > 0"
    >
      <!-- 大广告 -->
      <div
        class="bigAdvertise"
        @click="introduceSelf({item:AdvertiseData.bigAdvertise})"
        v-if="AdvertiseData.bigAdvertise.img"
      >
        <img :src="AdvertiseData.bigAdvertise.img" alt="大广告" />
      </div>
      <!-- 小广告 -->
      <div
        :style="{'background':AdvertiseData.smallAdvertiseBackgroundColor}"
        class="smallAdvertise"
        v-if="AdvertiseData.smallAdvertise"
      >
        <div class="smallAdvertise_1" v-for="(item,key) in AdvertiseData.smallAdvertise" :key="key">
          <img :src="item.img" alt="广告" @click="introduceSelf({item})" />
        </div>
      </div>
    </div>
    <!-- 爆品秒杀 -->
    <div
      class="recommend"
      v-if="timeData.length >0 && flashGoodsListArr && flashGoodsListArr.length > 0"
      style="margin-top: 0;"
    >
      <!-- 标题 -->
      <div class="titles">
        <div>
          <img src="../assets/shu_icon.png" alt="icon" class="icon" />
          <span class="leftTitle">爆品秒杀</span>
        </div>
        <div class="more">
          <router-link to="/flash_sale_list">
            <span>更多</span>
            <van-icon name="arrow" color="#E80002" size=".75rem" />
          </router-link>
        </div>
      </div>
      <!-- 时间 -->
      <div class="timeList">
        <div v-for="(item,key) in timeData" :key="key" class="flash_time" @click="selectTime(item)">
          <div :class="[activeTime.flashId == item.flashId?'active' :'','activeTime']">{{item.font}}</div>
          <div class="font">
            <span
              :class="[activeTime.flashId == item.flashId?'active' :'','activeTime']"
            >{{ item.number == 1 ?'抢购中':'即将开抢'}}</span>
          </div>
        </div>
      </div>
      <List
        :list_type="5"
        :list="flashGoodsListArr"
        :flashActive="activeTime.number"
        :theme="4"
        @introduce="flashFun"
        class="products"
      ></List>
    </div>

    <!-- 管家推荐 -->
    <div class="recommend recommend2" v-if="preferential.length>0">
      <div class="titles">
        <div>
          <img src="../assets/shu_icon.png" alt="icon" class="icon" />
          <span class="leftTitle">管家推荐</span>
        </div>
      </div>
      <div class="guang_gao" v-for="(item,key) in preferential" :key="key">
        <img
          v-lazy="item.img"
          alt="icon"
          class="guang_gao_img"
          @click.stop="introduceSelf({item})"
          v-if="item.img"
        />
        <div class="list" v-if="item.good">
          <List :list_type="3" :list="item.good" @introduce="flashFun">
            <div slot="slot_1" class="transverse_div"></div>
            <div slot="slot_2" class="transverse_div"></div>
          </List>
        </div>
      </div>
    </div>

    <!-- 猜你喜欢 -->
    <Like @introduceSelf="flashFun" />

    <!-- 万物互联 -->
    <!-- <div class="connect">
          <img src="../assets/connect.png" alt="万物互联" />
    </div>-->

    <!-- 空盒子 -->
    <div class="empty"></div>
    <!-- 底部导航 -->
    <Nav class="foot_nav" :btn="1" />
  </div>
</template>

<script>
import {
  Navs,
  Heads,
  Diamond,
  Info,
  Advertise,
  flashGoodsList,
  flashTimeList,
  SpecialShow
} from "../../api/index/";
import Banner from "../components/banner";
import Like from "../components/like";
import List from "../components/list";
import Nav from "../components/nav";
import commJs from "../comm/index";
import { mapActions, mapGetters } from "vuex";
export default {
  components: {
    Banner,
    List,
    Nav,
    Like
  },
  data() {
    return {
      // 分类
      classification: [],
      // 默认选中的key
      classification_index: 0,
      // 背景色
      bannerBack: "#f5f5f5",
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
      // 广告
      AdvertiseData: {
        bigAdvertise: "",
        smallAdvertise: []
      },
      // 秒杀时间
      timeData: [],
      // 当前时间
      activeTime: {},
      // 秒杀列表
      flashGoodsListArr: [],
      // 当前时间戳
      timestamp: 0,
      // 主推产品分类
      page_flagship_category: [],
      // 特惠专场
      preferential: [],
      // 资讯弹窗
      xun_show: false,
      // 城市
      city: "深圳",
      // 分类
      shownav: false
    };
  },
  methods: {
    ...mapActions(["slelctPageClassFun"]),
    // 获取分类
    NavFun() {
      let that = this;
      Navs().then(res => {
        if (res.status == 0) {
          that.classification.push(...res.data);
        }
      });
    },
    // 首页头部（banner、背景色）
    HeadsFun() {
      let that = this;
      Heads().then(res => {
        if (res.status == 0) {
          if (res.data.length > 0) {
            that.bannerBack = res.data[0].backgroundColor;
            that.page_banner.push(
              ...res.data.map(item => {
                return item;
              })
            );
          }
        }
      });
    },
    // 监听banner图片
    onChangeBanner(index) {
      this.bannerBack = index.backgroundColor;
    },
    // 首页导航区
    DiamondFun() {
      let that = this;
      Diamond().then(res => {
        if (res.status == 0) {
          that.page_nav = res.data;
        }
      });
    },
    // 资讯
    InfoFun() {
      let that = this;
      Info().then(res => {
        if (res.status == 0) {
          that.zi_xun = res.data;
        }
      });
    },
    // 广告
    AdvertiseFun() {
      Advertise().then(res => {
        if (res.status == 0) {
          this.AdvertiseData = Object.assign(this.AdvertiseData, res.data);
        }
      });
    },
    // 获取秒杀时间
    flashTimeListFun() {
      let that = this;
      flashTimeList().then(res => {
        if (res.status === 0) {
          that.timeData = res.data;
          if (that.timeData.length > 0) {
            that.activeTime = that.timeData[0];
            that.flashGoodsListFun(that.timeData[0].flashId);
          }
        }
      });
    },
    // 秒杀商品
    flashGoodsListFun() {
      let that = this;
      let data = {
        flashId: that.activeTime.flashId,
        pageSize: 3,
        pageNumber: 1
      };
      flashGoodsList(data).then(res => {
        that.flashGoodsListArr = [];
        if (res.status == 0) {
          that.flashGoodsListArr.push(
            ...res.data.map(item => {
              item.flashActive = true;
              item.image = item.imgPath;
              return item;
            })
          );
        } else if (res.status === 4005 && res.msg === "秒杀活动不存在") {
          that.flashGoodsListArr = [];
        } else {
          commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
    },
    // 点击获取秒杀商品列表
    selectTime(item) {
      this.activeTime = item;
      this.flashGoodsListFun(item.flashId);
    },
    // 特惠专场
    SpecialShowFun() {
      let that = this;
      SpecialShow().then(res => {
        if (res.status == 0) {
          if (res.data.length > 0) {
            that.preferential.push(...res.data);
          }
        }
      });
    },
    // 点击分类
    classFun(item, key) {
      this.classification_index = key;
      this.slelctPageClassFun({ key });
      if (item.id > 0) {
        this.$router.push({
          path: `/result?search=${item.name}&id=${item.id}&u=/`
        });
      }
    },
    //点击跳转
    introduceSelf(data) {
      let that = this;
      commJs.jumpNew(data, that);
    },
    // 商品跳转
    flashFun(data) {
      // 判断登录，如果没有登录，跳转登录
      if (commJs.testLogin(this, "/", false) == false) {
        return;
      }
      if (data.isSaleOut) {
        commJs.Dialog2("秒杀已售罄", "warning", 1000, this);
        return;
      }
      let path = "";
      // 秒杀
      if (data.item.hasOwnProperty("flashGoodId")) {
        if (
          data.item.point > this.getUser.usableRemain &&
          data.item.pointLimit > this.getUser.usableRemain
        ) {
          commJs.Dialog2("积分余额不满足参加秒杀的条件", "warning", 1000, this);
          return;
        }
        path = `/goodsinfo?goodId=${data.item.goodId}&flashGoodId=${
          data.item.percent == 100 ? "" : data.item.flashGoodId
        }&u=/`;
      } else {
        path = `/goodsinfo?goodId=${
          data.item.goodId ? data.item.goodId : data.item.goods_id
        }&u=/`;
      }
      this.$router.push({ path });
    },
    /**
     * 获取城市
     */
    showPosition() {
      let that = this;
      let geolocation = new BMap.Geolocation();
      geolocation.getCurrentPosition(function(r) {
        if (this.getStatus() == BMAP_STATUS_SUCCESS) {
          // 获取经纬度
          let lat = r.point.lat;
          let lng = r.point.lng;
          let province = r.address.province; // 省
          let str = r.address.city.toString();
          that.city = str.substr(0, str.length - 1); //  市
        }
      });
    }
  },
  computed: {
    ...mapGetters(["getpageClass", "getUser"])
  },
  mounted() {
    this.NavFun();
    this.HeadsFun();
    this.DiamondFun();
    this.InfoFun();
    this.AdvertiseFun();
    this.flashTimeListFun();
    this.SpecialShowFun();
    this.classification_index = this.getpageClass;
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->

<style lang="less" scoped>
// banner头部
.banner {
  position: relative;
  // 分类
  .classification_box {
    display: flex;
    align-items: center;
    padding: 0.875rem 0.625rem;

    .classification {
      display: -webkit-box;
      overflow-x: scroll;
      -webkit-overflow-scrolling: touch;
      font-family: PingFangSC-Medium;
      font-size: 0.8125rem;
      .classification_coment {
        padding-right: 1.3125rem;
        span {
          display: block;
          border-bottom: 2px solid none;
          padding-bottom: 0.2rem;
        }
      }
      .classification_coment:nth-last-child(1) {
        padding-right: 0;
      }
    }
    .class_icon {
      width: 1.125rem;
      height: 1.125rem;
      margin-left: 0.7rem;
      margin-bottom: 0.4rem;
    }
    .active_class {
      display: block;
      border-bottom: 2px solid #ffffff !important;
      padding-bottom: 0.2rem;
    }
  }
  // banner
  .apgeBanner {
    position: absolute;
    bottom: -4rem;
    width: 94%;
    left: 3%;
    /deep/ .van-swipe {
      border-radius: 0.3rem;
      height: 10rem;
    }
    // 轮播
    /deep/.van-swipe,
    /deep/.van-swipe__track {
      width: 100%;
      img {
        width: 100%;
        height: 100%;
      }
    }
  }

  // 搜索框
  .search_div {
    z-index: 100;
    padding-top: 0.625rem;
    height: 11.5rem;
    border-bottom-right-radius: 5%;
    border-bottom-left-radius: 5%;
    margin-bottom: 4.5rem;
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
}

// 导航
.nav_box {
  .nav {
    display: flex;
    flex-wrap: wrap;
    background: #ffffff;
    .nav_div {
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
            margin-bottom: 0.3125rem;
          }
        }
      }
    }
  }
  /deep/ .van-swipe__track {
    // height: 12rem;
  }
  /deep/.van-swipe {
    background: #ffffff;
  }
  /deep/ .van-swipe__indicators {
    bottom: 0px;
  }
  /deep/ .van-swipe__indicators .van-swipe__indicator {
    width: 0.9375rem;
    background-color: #999999;
    border-radius: 0.625rem;
    height: 0.1875rem;
  }
  .nav_btn_font {
    text-align: center;
  }
}
// 管家资讯
.info_box {
  background: #ffffff;
  padding: 0.625rem 0.8125rem;
}
.information {
  display: flex;
  align-items: center;
  height: 2rem;
  padding: 0 0.5rem;
  border-radius: 0.625rem;
  background: #f3f3f3;
  border-top: 1px solid rgba(230, 230, 230, 1);

  .information_left {
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
    width: 72%;
    padding-left: 0.625rem;
    /deep/ .yd-rollnotice-item {
      background: #f3f3f3;
    }
    .coment {
      width: 100%;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
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
    display: block;
  }
  .smallAdvertise {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    padding: 0.9375rem 0 0.3rem 0;
    .smallAdvertise_1 {
      width: 46.5%;
      height: 5.25rem;
      margin-bottom: 0.4375rem;
      img {
        width: 100%;
        height: 5.25rem;
      }
    }
    .smallAdvertise_1:nth-child(2n - 1) {
      margin-right: 0.625rem;
    }
  }
}
// 标题
.titles {
  display: flex;
  align-items: center;
  padding-top: 0.3375rem;
  padding-bottom: 0.4rem;
  font-family: PingFangSC-Medium;
  justify-content: space-between;
  div {
    display: flex;
    align-items: center;
  }
  .leftTitle {
    font-family: PingFang-SC-Bold;
    color: #e80002;
    margin-left: 0.3rem;
    font-size: 1rem;
  }
  .more {
    color: #e80002;
    font-size: 0.75rem;
    span {
      position: relative;
      top: -0.0625rem;
    }
  }
  .icon {
    width: 0.1875rem;
    height: 1rem;
  }
}

// 爆品秒杀
.recommend {
  margin-top: 0.5rem;
  background: #ffffff;
  padding: 0 0.9375rem 0;
  .timeList {
    color: #747474;
    background: #ffffff;
  }
  /deep/ .flash_time {
    padding: 0 1rem 0 0;
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
  .active {
    color: #f80030;
    font-size: 0.875rem;
  }
  /deep/ .yd-list-theme4 {
    padding: 0;
  }
}
.recommend2 {
  padding: 0;
  position: relative;
  .guang_gao {
    margin-bottom: 0.9375rem;
  }
  .guang_gao_img {
    width: 100%;
  }
  .titles {
    padding: 0.875rem 0 0.4rem 0.9375rem;
  }
  .transverse_div {
    width: 1rem;
    height: auto;
  }
  .list {
    position: relative;
    top: -1rem;
    height: 10.2rem;
    /deep/ .transverse_list_div {
      padding: 0.1rem 0.2rem;
    }
    /deep/ .minx {
      padding: 0.8rem;
      background: #ffffff;
      border-radius: 0.625rem;
    }
  }
  /deep/ .transverse_list:nth-last-child(1) {
    margin-right: 0;
  }
}
.products {
  /deep/ .yd-list-img {
    width: 7.5625rem !important;
    padding: 50px 0 !important;
    overflow: inherit;
  }
  /deep/ .yd-list-title {
    -webkit-line-clamp: 2;
    font-weight: inherit;
    color: #000000;
  }
}

.likeList {
  /deep/ .yd-list-other {
    height: 2rem;
    padding: 1rem 0 0.3rem 0;
  }
  /deep/ .demo-list-price {
    color: #f1012f;
    font-size: 0.875rem;
  }
  /deep/ .rigth_foot {
    span {
      color: #f1012f;
      background: #fceaea;
      font-family: PingFangSC-Medium;
      font-size: 0.625rem;
      padding: 0.2rem 0.5rem;
      border-radius: 3rem;
    }
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

.body {
  // background: #ffffff;
}

.shownav {
  background: rgba(74, 74, 74, 0.5);
  height: 100%;
  width: 100%;
  position: fixed;
  z-index: 100;
  top: 5rem;
  color: #333333;
}

.shownavBack {
  width: 100%;
  background: #ffffff;
  padding: 1rem 0.9375rem;
  .title {
    font-size: 0.875rem;
  }
}
.classBody {
  display: flex;
  flex-wrap: wrap;
  min-height: 6rem;
  max-height: 18rem;
  overflow-y: scroll;
}
.classNav {
  width: 24%;
  margin: 1rem 4.5% 0 4.5%;
  .classNavImg {
    img {
      width: 100%;
      height: 4rem;
    }
  }
  .classNavTitle {
    text-align: center;
    font-size: 0.8rem;
  }
}

// 过渡动画
.slide-fade-enter-active {
  transition: all 0.1s ease;
}
.slide-fade-leave-active {
  transition: all 0.8s cubic-bezier(1, 0.5, 0.8, 3);
}
.slide-fade-enter, .slide-fade-leave-to
/* .slide-fade-leave-active for below version 2.1.8 */ {
  transform: translateX(18rem);
  opacity: 0;
}
</style>
