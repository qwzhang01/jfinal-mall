<template>
  <div>
    <!-- 导航栏 -->
    <Navbar :navbar="navbar" @navRight="navRight" />

    <!-- 商品详情四个菜单 -->
    <div class="fixedNav" v-if="Distance">
      <van-tabs v-model="active" animated @click="clickNav">
        <van-tab v-for="(item,key) in fixedNav" :title="item.title" :key="key"></van-tab>
      </van-tabs>
    </div>

    <div class="coment_box">
      <div id="shop_harder">
        <!-- bannber -->
        <Banner
          :images="bannerImg"
          :vertical="0"
          v-if="showBanner"
          :indicators="1"
          @introduce="bannerLook"
        />
        <!-- 拼团商品规则 -->
        <div class="rule" v-if="activityType == 3" @click="more('rule')">规则</div>
        <!-- 视频 -->
        <Video v-if="!showBanner" :src="videoSrc"></Video>
        <!-- 活动标签 -->
        <Label
          :type="'groupTime'"
          :activity="activity"
          v-if="activitySwitch.activity || activitySwitch.lotteryActivity"
        />
        <div class="good_hader_info">
          <div class="ordinary">
            <div class="ordinary_rmb" v-if="activityType == 1">
              <span>
                <i>￥</i>
                {{good.shop_price - good.point_as_money}}
                <i
                  v-if="good.point_as_money > 0"
                >+ {{good.point_as_money}}积分</i>
              </span>
              <span>原价￥{{good.market_price}}</span>
            </div>
            <div class="goods_name_1" v-else>{{good.goods_name}}</div>
            <div>
              <van-icon
                :name="good.is_collect?'star':'star-o'"
                size="1.1875rem"
                :color="good.is_collect?'#D7000F':''"
                @click="collectFun(good.is_collect)"
              />
              <p>{{good.collect_sum}}人收藏</p>
            </div>
          </div>
          <!-- 标题 -->
          <div class="goods_name" v-if="activityType == 1">{{good.goods_name}}</div>
        </div>
        <!-- 商家地址 -->
        <div class="lable_3" v-if="good.goods_remark">{{good.goods_remark}}</div>
        <!-- 活动标签2 -->
        <div class="lable_1" v-if="false">
          <span>
            <img
              src="http://www.bjgjyun.com/public/upload/goods/thumb/3000/goods_thumb_3000_400_400.jpeg"
              alt
              srcset
            />
          </span>
          <span>商品正在参加秋新势力周，快来抢购！</span>
        </div>
        <div
          class="lable_1"
          v-if="activity && ((activity.pointLimit && activity.pointLimit > 0) || (activity.point && activity.point > 0))"
        >
          <span>
            <img src="../../assets/shu_icon.png" alt="icon" class="icon" />
          </span>
          <span>抢购门槛：积分余额不低于{{activity.pointLimit > activity.point? activity.pointLimit: activity.point}}</span>
        </div>
      </div>
      <!-- 产品规格和地址 -->
      <div>
        <div class="boxNorms" v-if="good.selectedSku && good.selectedSku.key">
          <div class="boxNorms2">
            <!-- 商品规格  -->
            <div class="Norms">
              <div class="lable_2">已选</div>
              <div>{{good.selectedSku.key_name}}</div>
            </div>
            <div>
              <img src="../../assets/more2.png" alt="icon" class="icon" />
            </div>
          </div>
        </div>
        <!-- 地址 -->
        <router-link :to="'/addresslist?u=goodsinfo?goodId='+goodId+'&type=goodsinfo'">
          <div class="boxNorms">
            <div class="boxNorms2 address">
              <div class="Norms">
                <div class="lable_2">送至</div>
                <div>{{consignee.address}}</div>
              </div>
              <div>
                <img src="../../assets/more2.png" alt="icon" class="icon" />
              </div>
            </div>
          </div>
        </router-link>

        <!-- 说明 -->
        <div class="explainBox" v-if="explain">
          <div class="explain">
            <span v-for="(item,key) in explain" :key="key">
              <img src="../../assets/yes.png" alt="yes" />
              {{item}}
            </span>
          </div>
          <div>
            <img src="../../assets/more2.png" alt="icon" class="icon" />
          </div>
        </div>
      </div>
      <!-- 活动标签二 -->
      <Label
        :type="'personnel'"
        :personnelist="personnelist"
        :activity="activity"
        @more="more"
        :titleShow="true"
        class="personnelist"
        v-if="activitySwitch.lotteryActivity"
      />
      <!-- 买家评价 -->
      <div class="evaluateTitle_box" id="evaluateTitle">
        <div class="evaluateTitle">
          <div>买价评论（{{statistics.total_sum}}）</div>
          <router-link :to="'/valuateList?goodId='+goodId">
            <div>
              <span style="color:#D7000F">{{statistics.positiveRate}}</span> 好评率
              <van-icon name="arrow" size=".9375rem" />
            </div>
          </router-link>
        </div>
        <EvaluateList :list="evaluateList" />
        <!-- 查看更多 -->
        <div class="btnMore">
          <router-link :to="'/valuateList?goodId='+goodId">
            <van-button size="normal">查看更多</van-button>
          </router-link>
        </div>
      </div>
      <!-- 商家店铺 -->
      <div class="store">
        <div class="storeTitle">
          <router-link
            :to="'/storepage?store_id='+storeData.store_id+'&u=/goodsinfo?goodId='+goodId"
          >
            <div class="store_1">
              <div>
                <img :src="storeData.store_logo" alt="店铺" />
              </div>
              <div class="store_name">
                <div>{{storeData.store_name}}</div>
                <div>商品数量：{{storeData.totalGoodNum}}件</div>
              </div>
            </div>
          </router-link>
          <div class="into_store">
            <router-link
              :to="'/storepage?store_id='+storeData.store_id+'&u=/goodsinfo?goodId='+goodId"
            >
              <van-button size="small">进店逛逛</van-button>
            </router-link>
          </div>
        </div>
        <!-- 店铺推荐 -->
        <div class="evaluateTitle evaluateTitle2">
          <div>店铺推荐</div>
          <router-link
            :to="'/storepage?store_id='+storeData.store_id+'&u=/goodsinfo?goodId='+goodId"
          >
            <div>
              查看更多
              <van-icon name="arrow" size=".9375rem" />
            </div>
          </router-link>
        </div>
        <List :list_type="'3'" :list="storeList" class="storeList" @introduce="lookGood" />
      </div>
      <!-- 商品详情 -->
      <div class="details" id="details">
        <div class="detailsTitle">商品详情</div>
        <div class="detailsImg">
          <img
            :src="img"
            alt="商品图片"
            v-for="(img,key) in good.goodDetail"
            :key="key"
            @click="viewImg(good.goodDetail,key)"
          />
        </div>
      </div>
      <!-- 猜你喜欢 -->
      <Like @introduceSelf="introduceSelf" />
    </div>
    <!-- 选择产品规格 -->
    <Specifications
      v-if="showBase"
      :actionButon="actionButon"
      @AddCartClicked="AddCartClicked"
      @BuyClicked="BuyClicked"
    />
    <!-- 购买菜单 -->
    <div class="shop_action" v-if="!activitystart">
      <van-goods-action>
        <a style="width:3rem;" :href="'tel:'+storeData.service_phone">
          <van-goods-action-icon icon="chat-o" text="客服" />
        </a>
        <van-goods-action-icon
          :info="cartNum"
          icon="cart-o"
          text="购物车"
          :to="'/cart?u=/goodsinfo?goodId='+goodId"
        />
        <van-goods-action-icon
          icon="shop-o"
          text="店铺"
          :to="'./storepage?store_id='+storeData.store_id+'&u=/goodsinfo?goodId='+goodId"
        />
        <van-goods-action-button
          text="加入购物车"
          type="warning"
          @click="addCart()"
          class="van-goods-action-button--first"
        />
        <van-goods-action-button
          primary
          text="立即购买"
          type="danger"
          @click="purchase()"
          class="van-goods-action-button--last"
        />
      </van-goods-action>
    </div>
    <!-- 秒杀参与菜单 标签三-->
    <Label :type="'join'" v-if="activitystart && activitySwitch.activity" @joinFun="joinFun" />
    <!-- 抢购抽奖参与按钮 -->
    <div class="shop_action" v-if="activitystart && activitySwitch.lotteryActivity">
      <van-goods-action>
        <a style="width:3rem;" :href="'tel:'+storeData.service_phone">
          <van-goods-action-icon icon="chat-o" text="客服" />
        </a>
        <van-goods-action-icon
          :info="cartNum"
          icon="cart-o"
          text="购物车"
          :to="'/cart?u=/goodsinfo?goodId='+goodId"
        />
        <van-goods-action-icon
          icon="shop-o"
          text="店铺"
          :to="'./storepage?store_id='+storeData.store_id+'&u=/goodsinfo?goodId='+goodId"
        />
        <van-goods-action-button
          text="单独购买"
          type="warning"
          @click="purchase()"
          class="van-goods-action-button--first"
        />
        <van-goods-action-button
          primary
          text="发起拼单"
          type="danger"
          @click="sharePurchase()"
          class="van-goods-action-button--last"
        />
      </van-goods-action>
    </div>
    <!-- 抢购查看更多 -->
    <yd-popup v-model="show1" position="center" width="90%">
      <div class="coment_lottery" :style="{'padding':showregulation? '.5rem 0 0 0' :'0.5rem'}">
        <!-- 删除icon -->
        <div class="del">
          <img src="../../assets/del_1.png" alt="del" @click="show1= !show1" />
        </div>
        <!-- 活动人数 -->
        <div v-if="!showregulation">
          <div class="title">参与人数</div>
          <div class="div_personnelist">
            <Label
              :type="'personnel'"
              :personnelist="personnelist"
              :activity="activity"
              :titleShow="false"
              class="personnelist"
              v-if="activitySwitch.lotteryActivity"
            />
          </div>
        </div>
        <div v-else class="div_personnelist" style="height:24rem">
          <img v-lazy="regulation" alt="规则图片" class="reg" />
        </div>
      </div>
    </yd-popup>
    <div v-show="showTip" class="showtip" @click="showTip = false">
      <img src="../../assets/invite/top.png" />
      <div class="sclose">点击右上角，发送给好友</div>
    </div>
  </div>
</template>
<script>
import { Toast, Dialog, ImagePreview } from "vant";
import Navbar from "../../components/navbar";
import List from "../../components/list";
import Like from "../../components/like";
import EvaluateList from "../../components/evaluateList";
import Label from "./label";
import Specifications from "./specifications";
import Video from "../../components/video";
import Banner from "../../components/banner";
import { mapActions, mapGetters } from "vuex";
import { PageFavourite } from "../../../api/";
import {
  PreFlashOrderValid,
  PreFlashOrder,
  PreLotteryOrder,
  PreOrder,
  SubOrder
} from "../../../api/order.js";
import {
  goodsDetail,
  Collect,
  cancelCollect,
  lotteryTimeList
} from "../../../api/goods/";
import { WxJsConfig } from "../../../api/";
import { addGoodsToCart } from "../../../api/cart.js";
import comm from "../../comm/index";
import { setInterval, setTimeout } from "timers";
import commJs from "../../comm";
export default {
  components: {
    Navbar,
    Banner,
    Video,
    Label,
    Specifications,
    EvaluateList,
    List,
    Like
  },
  data() {
    return {
      // 显示分享蒙版
      showTip: false,
      // 抢购查看更多
      show1: false,
      // 计算距离，是否显示导航
      Distance: false,
      // 是否显示规格
      showBase: false,
      // 规则图片
      regulation: "",
      showregulation: false,
      // 活动开关
      activitySwitch: {
        // 秒杀
        activity: false,
        // 一元抢购
        lotteryActivity: false
      },
      // 商品类型 (默认为普通商品)
      activityType: 1,
      // 活动是否开始(默认未开启)
      activitystart: false,
      // 活动相关参数
      activity: {},
      // 商品id
      goodId: "",
      shareUserId: "",
      // 顶部菜单
      fixedNav: [
        { index: "0", title: "商品", onScroll: 0 },
        { index: "1", title: "评价", onScroll: 0 },
        { index: "2", title: "详情", onScroll: 0 },
        { index: "3", title: "推荐", onScroll: 0 }
      ],
      // 顶部导航条
      active: "0",
      // banner图片
      showBanner: true,
      // 视频地址
      videoSrc: "",
      // banner
      bannerImg: [],
      // 商品
      good: {
        goods_name: "",
        spec_goods_price: [],
        selectedSku: {}
      },
      // 地址
      consignee: {},
      // 商家
      storeData: {},
      actionButon: "",
      // 选中的规格
      selectNorms: {
        normsName: ""
      },
      // 评价汇总
      statistics: {},
      // 商品说明
      explain: ["7天无理由退换货", "送货上门", "48小时发货"],
      // 一元抢购
      lotteryGoodId: "",
      // 抢购人员
      personnelist: {
        userList: []
      },
      // 评价列表
      evaluateList: [],
      // 商家推荐店铺
      storeList: [],
      // 秒杀id
      flashGoodId: "",
      // 购物车个数
      cartNum: 0,
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "商品详情",
          //颜色
          color: "#333333"
        },
        right: [
          {
            icon_img: require("../../assets/share.png"),
            // 目标地址
            to: "",
            fn: "handleShareClick",
            coment: "",
            color: "#333333",
            // 图标大小
            size: "1rem"
          },
          {
            icon_img: require("../../assets/more.png"),
            // 目标地址
            to: "",
            // 图标大小
            size: "1rem"
          }
        ]
      },
      toUrl: ""
    };
  },
  methods: {
    ...mapActions(["goodsDataFun"]),
    // 点击导航上的icon
    navRight(data) {
      // 关闭视频
      if (data.coment == "关闭视频") {
        this.showBanner = !this.showBanner;
        let vId = document.getElementById("video");
        this.navbar.right[0].icon_img = require("../../assets/share.png");
        this.navbar.right[0].coment = "";
        this.navbar.right[0].color = "#333333";
        vId.pause();
      } else if (data.coment == "返回") {
        // 清除缓存
        commJs.deletData(["lotteryGoodId", "flashGoodId"]);
        this.$router.push({
          path: this.toUrl
        });
      } else if (data.fn === "handleShareClick") {
        this.handleShareClick();
      }
      return;
    },
    /**
     * 处理分享逻辑
     */
    handleShareClick() {
      this.showTip = true;
    },
    /**
     * 获取距离
     * id: shop_harder    商品 0
     * id: evaluateTitle  评价 1
     * id: details        详情 2
     * id: like           推荐 3
     */
    distance() {
      this.$nextTick(function() {
        setTimeout(() => {
          if (document.getElementById("shop_harder")) {
            this.fixedNav[0].onScroll = document.getElementById(
              "shop_harder"
            ).offsetTop;
          }
          if (document.getElementById("evaluateTitle")) {
            this.fixedNav[1].onScroll =
              document.getElementById("evaluateTitle").offsetTop - 60;
          }
          if (document.getElementById("details")) {
            this.fixedNav[2].onScroll =
              document.getElementById("details").offsetTop - 60;
          }
          if (document.getElementById("like")) {
            this.fixedNav[3].onScroll =
              document.getElementById("like").offsetTop - 80;
          }
          this.Distance = true;
        }, 2000);
      });
    },
    /**
     * 锚点
     * id: shop_harder    商品 0
     * id: evaluateTitle  评价 1
     * id: details        详情 2
     * id: like           推荐 3
     */
    clickNav(index, title) {
      if (index == 0) {
        var idName = "shop_harder";
      } else if (index == 1) {
        var idName = "evaluateTitle";
      } else if (index == 2) {
        var idName = "details";
      } else if (index == 3) {
        var idName = "like";
      }
      commJs.slide(idName, 50);
    },
    /**
     * 监听
     */
    onScroll() {
      let scrolled =
        document.documentElement.scrollTop || document.body.scrollTop;
      // 商品
      if (
        this.fixedNav[0].onScroll <= scrolled &&
        scrolled < this.fixedNav[1].onScroll
      ) {
        this.active = 0;
      } else if (
        this.fixedNav[1].onScroll <= scrolled &&
        scrolled < this.fixedNav[2].onScroll
      ) {
        this.active = 1;
      } else if (
        this.fixedNav[2].onScroll <= scrolled &&
        scrolled < this.fixedNav[3].onScroll
      ) {
        this.active = 2;
      } else if (scrolled > this.fixedNav[3].onScroll) {
        this.active = 3;
      }
    },
    /**
     * 商品详情
     */
    goodsDetailFun() {
      let that = this;
      let data = {
        goodId: this.goodId,
        flashGoodId: this.flashGoodId,
        lotteryGoodId: this.lotteryGoodId,
        shareUserId: this.shareUserId
      };
      goodsDetail(data).then(res => {
        if (res.status == 0) {
          // 整合数据
          that.good = Object.assign(that.good, res.data.good);
          that.good.is_collect = res.data.is_collect;
          that.good.goodDetail = res.data.goodDetailImg;
          // 商品规格
          that.good.spec_goods_price = res.data.spec_goods_price;
          that.good.goods_spec_list = res.data.goods_spec_list;
          that.good.shop_price = that.good.spec_goods_price[0].price;
          that.good.market_price = that.good.spec_goods_price[0].market_price;
          // 乳沟只有一个SKU，则默认选中这个sku
          if (that.good.spec_goods_price.length === 1) {
            that.good.selectedSku = that.good.spec_goods_price[0];
          }
          // banner图片 数据处理
          that.bannerFun(res.data.gallery);
          // 地址
          if (!sessionStorage.getItem("consignee")) {
            that.consignee = res.data.consignee;
          } else {
            that.consignee = Object.assign(
              this.consignee,
              JSON.parse(sessionStorage.getItem("consignee"))
            );
          }
          that.activityType = res.data.activityType;
          sessionStorage.removeItem("absentNum");
          // 购物个数
          that.cartNum = res.data.cartNum ? res.data.cartNum : 0;
          if (res.data.activityType > 1) {
            if (res.data.activityType == 2) {
              // 时间转换
              this.activityFun(res.data.activity, res.data);
              // 秒杀
              that.activitySwitch.activity = true;
            } else if (res.data.activityType == 3) {
              // 时间转换
              this.activityFun(res.data.lotteryActivity, res.data);
              // 抢购
              that.activitySwitch.lotteryActivity = true;
              that.robGood(res.data);
            }

            // 商品类型
            that.good.activityType = res.data.activityType;
            // 活动是否开始
            that.good.activityActive = this.activity.active;
            if (that.activity.active == 1) {
              // 每人限购一个
              that.good.store_count = 1;
              // 更改活动价格
              that.good.shop_price =
                res.data.activityType == 3
                  ? res.data.lotteryActivity.lotteryPrice
                  : res.data.activityType == 2
                    ? res.data.activity.flashPrice
                    : that.good.shop_price;
            }
          }
          // 评论
          let arrComment = res.data.comment;
          that.evaluateList = arrComment.map(item => {
            if (item.is_show) {
              item.add_time = commJs.dateHandle(item.add_time);
              return item;
            }
          });
          // 评价汇总
          that.statistics = Object.assign(that.statistics, res.data.statistics);
          // 商店
          that.storeData = res.data.store;
          // 店铺推荐
          that.storeList = res.data.recommend_goods;

          that.distance();
        } else {
          commJs.Dialog2(res.msg, "error", 1500, that);
        }
      });
    },
    //抢购查看更多/规则
    more(item) {
      if (item == "rule") {
        this.showregulation = true;
      } else {
        this.showregulation = false;
      }
      this.show1 = !this.show1;
    },
    // banner 数据处理
    bannerFun(bannerData) {
      this.bannerImg = bannerData.map((item, index) => {
        let obj = {
          jump_type: 0,
          img: item
        };
        // 视频路径是否存在
        if (index == 0 && this.good.video) {
          this.videoSrc = this.good.video;
          obj.jump_type = 8;
        }
        return obj;
      });
    },
    /**
     * 抢购商品
     */
    robGood(goodData) {
      // console.log(goodData.lotteryActivity);
      this.regulationImageFun();
      this.personnelist = Object.assign(
        this.personnelist,
        goodData.lotteryActivity
      );
      // 存储
      sessionStorage.setItem("absentNum", goodData.lotteryActivity.absentNum);
    },
    // 时间转换
    activityFun(data, obj) {
      var re = /^[0-9]+.?[0-9]*$/; //判断字符串是否为数字 //判断正整数 /^[1-9]+[0-9]*]*$/
      // 开始时间
      if (re.test(data.startTime)) {
        var startTime = commJs.dateHandle(data.startTime, "/");
      } else {
        var startTime = data.startTime.replace(/-/g, "/");
      }
      // 结束时间
      if (re.test(data.endTime)) {
        // 开始时间
        var endTime = commJs.dateHandle(data.endTime, "/");
      } else {
        var endTime = data.endTime.replace(/-/g, "/");
      }
      // console.log(startTime);
      // console.log(endTime);
      // 现在的时间，精确到秒
      let time = Date.parse(new Date()) / 1000;
      // 开始时间
      let _startTime = Date.parse(new Date(startTime)) / 1000;
      // 结束时间
      let _endTime = Date.parse(new Date(endTime)) / 1000;

      // 未开始
      if (time < _startTime) {
        console.log("未开始");
        var active = 0;
        var newTime = startTime;
      }
      // 进行中
      else if (_startTime <= time && time <= _endTime) {
        console.log("进行中");
        var active = 1;
        var newTime = endTime;
      } else {
        console.log("结束");
        var active = 0;
        var newTime = "";
      }

      this.activitystart = active == 1 ? true : false;
      data.newTime = newTime;
      data.activityType = obj.activityType; // 活动类型
      data.active = active; // 活动状态
      data.shop_price = this.good.shop_price; // 商品原价
      this.activity = Object.assign(this.activity, data);
    },
    /**
     * 查看商品
     */
    lookGood(data) {
      let to = this.toUrl;
      Object.assign(this.$data, this.$options.data());
      this.goodId = data.item.goods_id;
      commJs.deletData(["lotteryGoodId", "flashGoodId"]);
      setTimeout(() => {
        this.active = 0;
        window.scrollTo(0, 0);
      }, 1000);
      this.goodsDetailFun(data.item.goods_id);
      this.toUrl = to;
    },
    /**
     * 收藏
     */
    collectFun(active) {
      // 判断是否登录
      if (
        commJs.testLogin(this, "/goodsinfo?goodId=" + this.goodId, false) ==
        false
      ) {
        return;
      }
      if (active) {
        // 取消收藏
        this.cancelCollectFun();
      } else {
        // 取消收藏
        this.CollectFun();
      }
    },
    // 取消收藏
    cancelCollectFun() {
      let that = this;
      cancelCollect({ goodId: this.goodId }).then(res => {
        if (res.status == 0) {
          var active = "success";
          that.goodsDetailFun(this.goodId);
        } else {
          var active = "error";
        }
        commJs.Dialog2(res.msg, active, 1000, that);
      });
    },
    // 加入收藏
    CollectFun() {
      let that = this;
      Collect({ goodId: this.goodId }).then(res => {
        if (res.status == 0) {
          var active = "success";
          that.goodsDetailFun(this.goodId);
        } else {
          var active = "error";
        }
        commJs.Dialog2(res.msg, active, 1000, that);
      });
    },
    /**
     * 点击关闭视频
     */
    bannerLook(data) {
      let that = this;
      if (data.item.jump_type == 8) {
        this.showBanner = !this.showBanner;
        this.navbar.right[0].icon_img = "";
        this.navbar.right[0].color = "red";
        this.navbar.right[0].coment = "关闭视频";
      } else {
        let arr = [];
        arr.push(
          ...data.arr.map(item => {
            return item.img;
          })
        );
        commJs.viewImg(arr, data.key);
      }
    },
    /**
     * 猜你喜欢
     */
    introduceSelf(data) {
      let that = this;
      commJs.jump(data, that);
      this.lookGood(data);
    },
    // 获取规则图片
    regulationImageFun() {
      lotteryTimeList().then(res => {
        // console.log(res);
        if (res.status == 0) {
          this.regulation = res.data.regulation;
        } else {
          commJs.Dialog2(res.msg, active, 1000, that);
        }
      });
    },
    /**
     * 预览图片
     */
    viewImg(arrImg, key) {
      commJs.viewImg(arrImg, key);
    },
    // 打开选择规格面板
    selectNormsFun() {
      // 判断是否登录
      if (
        commJs.testLogin(this, "/goodsinfo?goodId=" + this.goodId, false) ==
        false
      ) {
        return;
      }

      let objs = document.getElementById("boxNorms");

      if (objs) {
        // 先清除
        this.showBase = false;
        // 再开启
        setTimeout(() => {
          this.showBase = true;
        }, 10);
      } else {
        this.showBase = !this.showBase;
      }
      this.goodsDataFun(this.good);
    },

    // 加入购物车
    addCart() {
      this.actionButon = "cart";
      this.selectNormsFun();
    },
    // 立即购买
    purchase() {
      this.actionButon = "buy";
      this.selectNormsFun();
    },
    // 加入购物车
    AddCartClicked(data) {
      let that = this;
      let params = {
        skuId:
          data.selectedSkuComb && data.selectedSkuComb.id
            ? data.selectedSkuComb.id
            : "",
        goodNum: data.selectedNum
      };
      addGoodsToCart(params).then(res => {
        if (res.status == 0) {
          that.showBase = false;
          commJs.Dialog2(res.msg, "success", 1000, that);
          that.goodsDetailFun(this.goodId);
        } else {
          commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
    },
    // 立即购买（下单）
    BuyClicked(data) {
      Toast.loading({ mask: true, message: "请求中..." });
      let that = this;
      let params = {
        skuId:
          data.selectedSkuComb && data.selectedSkuComb.id
            ? data.selectedSkuComb.id
            : "",
        buyNum: data.selectedNum
      };
      PreOrder(params).then(res => {
        // TODO 这里的逻辑，之后会全部重新去掉，单独的营销活动使用单独的方法
        Toast.clear();
        if (res.status == 0) {
          sessionStorage.setItem("ordinaryorder", JSON.stringify(res.data));
          sessionStorage.setItem("isCart", 2);
          // 跳转
          that.$router.push({
            path: this.activitystart ? "/activityorder" : "/ordinaryorder",
            query: {
              u: "/goodsinfo?goodId=" + that.goodId,
              goodId: that.goodId,
              activityType: that.activityType,
              lotteryGoodId: that.lotteryGoodId
            }
          });
        } else {
          commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
    },
    // 秒杀下单
    joinFun() {
      Toast.loading({ mask: true, message: "请求中..." });
      let that = this;
      // 校验是否满足条件
      let flashGoodId = this.activity.flashGoodId;
      if (
        this.activity.point > this.getUser.usableRemain &&
        this.activity.pointLimit > this.getUser.usableRemain
      ) {
        commJs.Dialog2("积分余额不满足参加秒杀的条件", "warning", 1000, this);
        return;
      }
      PreFlashOrderValid({ flashGoodId }).then(vRes => {
        if (vRes.status === 0) {
          PreFlashOrder({ flashGoodId }).then(res => {
            Toast.clear();
            if (res.status == 0) {
              sessionStorage.setItem("ordinaryorder", JSON.stringify(res.data));
              sessionStorage.setItem("isCart", 2);
              // 跳转
              that.$router.push({
                path: "/activityorder",
                query: {
                  goodId: that.goodId,
                  flashGoodId
                }
              });
            } else {
              commJs.Dialog2(res.msg, "error", 1000, that);
            }
          });
        } else {
          commJs.Dialog2(vRes.msg, "error", 1000, that);
        }
      });
    },
    // 抢购抽奖发起订单
    sharePurchase() {
      let that = this;
      let lotteryGoodId = this.personnelist.lotteryGoodId;
      PreLotteryOrder({ lotteryGoodId }).then(res => {
        if (res.status == 0) {
          sessionStorage.setItem("ordinaryorder", JSON.stringify(res.data));
          sessionStorage.setItem("isCart", 2);
          // 跳转
          that.$router.push({
            path: "/loteryorder",
            query: {
              u: "/goodsinfo?goodId=" + that.goodId,
              goodId: that.goodId,
              activityType: that.activityType,
              lotteryGoodId: that.lotteryGoodId
            }
          });
        }
      });
    },
    /**
     * [ 微信Api初始化]
     * @param  {Function} callback [ready回调函数]
     */
    wxRegister(title, desc, link, imgUrl) {
      var url = location.href.split("#")[0];
      WxJsConfig({ url }).then(res => {
        if (res.status === 0) {
          let data = res.data; // PS: 这里根据你接口的返回值来使用
          wx.config({
            debug: false, // 开启调试模式
            appId: data.appId, // 必填，公众号的唯一标识
            timestamp: data.timestamp, // 必填，生成签名的时间
            nonceStr: data.nonceStr, // 必填，生成签名的随机
            signature: data.signature, // 必填，签名，见附
            jsApiList: [
              "updateAppMessageShareData",
              "updateTimelineShareData",
              "onMenuShareTimeline",
              "onMenuShareAppMessage"
            ] // 必填，需要使用的JS接口列表，所有JS接口列表见附件
          });
        }
      });

      // 处理验证成功的信息
      wx.ready(function() {
        // 新版本的微信接口
        // “分享给朋友”及“分享到QQ”
        wx.updateAppMessageShareData({
          title: title, // 分享标题
          desc: desc,
          link: link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名
          imgUrl: imgUrl, // 分享图标
          success: function(res) {
            // 用户确认分享后执行的回调函数
            console.log("分享到朋友圈成功返回的信息为:", res);
            console.log("分享成功!");
          }
        });
        // “分享到朋友圈”及“分享到QQ空间”
        wx.updateTimelineShareData({
          title: title, // 分享标题
          link: link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一�
          imgUrl: imgUrl, // 分享图标
          success: function(res) {
            // 用户确认分享后执行的回调函数
            console.log("分享给朋友成功返回的信息：", res);
          }
        });

        // 老版本的微信接口
        // 分享到朋友圈 （即将废弃）
        wx.onMenuShareTimeline({
          title: title, // 分享标题
          link: link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
          imgUrl: imgUrl, // 分享图标
          success: function(res) {
            // 用户点击了分享后执行的回调函数
            console.log("分享给朋友成功返回的信息：", res);
          }
        });
        // 分享给朋友”按钮点击状态及自定义分享内容接口（即将废弃）
        wx.onMenuShareAppMessage({
          title, // 分享标题
          desc, // 分享描述
          link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
          imgUrl, // 分享图标
          type: "link", // 分享类型,music、video或link，不填默认为link
          dataUrl: "", // 如果type是music或video，则要提供数据链接，默认为空
          success: function() {
            // 用户点击了分享后执行的回调函数
            console.log("分享到朋友圈成功返回的信息为:", res);
            console.log("分享成功!");
          }
        });
      });
    },
    initWxJs() {
      let goodId = this.$route.query.goodId;
      let mobile = this.getUser.mobile;
      let userId = sessionStorage.getItem("userId");
      if (!userId) {
        userId = "";
      }
      // 微信浏览器里面执行微信分享的逻辑
      if (comm.isWeiXin()) {
        // 分享URL
        var link = `http://h5.sdbjgj.com/?#/goodsinfo?goodId=${goodId}&uu=${userId}`;
        // 分享图标
        var imgUrl = `http://${window.location.host}/static/icon_2.png`;
        // 分享的话
        var desc =
          "【白金管家】积分商城发钱啦！即日起只要您邀请好友注册成为积分商城会员，选择商品下单并支付，就可赚返现";
        this.wxRegister("注册送积分，邀请返现金", desc, link, imgUrl);
      }
    }
  },
  computed: {
    ...mapGetters(["getUser"])
  },
  mounted() {
    // 初始化微信分享
    this.initWxJs();
    // 处理分享逻辑 - 获取邀请人的用户ID
    if (this.$route.query.uu) {
      this.shareUserId = this.$route.query.uu;
    } else {
      this.shareUserId = "";
    }
    // 获取产品详情
    if (this.$route.query.goodId) {
      this.$dialog.loading.open("加载中...");
      this.goodId = this.$route.query.goodId;

      if (this.$route.query.hasOwnProperty("flashGoodId")) {
        this.flashGoodId = this.$route.query.flashGoodId
          ? this.$route.query.flashGoodId
          : "";
      }

      if (this.$route.query.hasOwnProperty("lotteryGoodId")) {
        this.lotteryGoodId = this.$route.query.lotteryGoodId
          ? this.$route.query.lotteryGoodId
          : "";
      }

      // 获取商品详情
      this.goodsDetailFun();

      // 监听滑动
      this.$nextTick(function() {
        window.addEventListener("scroll", this.onScroll);
      });

      setTimeout(() => {
        this.active = 0;
        window.scrollTo(0, 0);
        this.$dialog.loading.close();
      }, 1000);
    } else {
      this.$router.go(-1);
    }

    // 存放父级路由
    if (this.$route.query.u) {
      sessionStorage.setItem(
        "upper",
        this.$route.query.u == "/"
          ? "/"
          : commJs.jumpUrl(this, this.navbar.left[0].to, ["goodId", "type"])
      );
    }
    // 路由整理
    if (sessionStorage.getItem("upper")) {
      this.toUrl = sessionStorage.getItem("upper");
    } else {
      this.toUrl = commJs.jumpUrl(this, this.navbar.left[0].to, [
        "goodId",
        "type"
      ]);
    }
  },
  created() {}
};
</script>
<style lang="less" scoped>
@import url("./goods.less");
.showtip {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 99;
  background: rgba(0, 0, 0, 0.5);
  height: -webkit-fill-available;
  text-align: center;
  color: #ffffff;
  text-align: right;
  img {
    width: 10rem;
    height: 10rem;
    margin-right: 1.5rem;
    margin-top: 1rem;
  }
  .sclose {
    text-align: center;
    margin-top: 2rem;
    color: #ffffff;
    font-size: 1.5rem;
  }
}
</style>
