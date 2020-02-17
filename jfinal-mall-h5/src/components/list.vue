<template>
  <!-- 列表组件 -->
  <div>
    <!--  这里可选1/2/3/4/5五种样式  无连接(type:1、图片懒加载 5：无懒加载) -->
    <yd-list :theme="theme" v-if="list_type == 1 || list_type == 5">
      <yd-list-item v-for="(item, key) in list" :key="key">
        <img
          class="img-class"
          slot="img"
          v-if="item.hasOwnProperty('image') && list_type==1"
          v-lazy="item.image"
          @click="btn(item,key,list)"
        />
        <img
          slot="img"
          v-if="item.hasOwnProperty('image') && list_type==5"
          :src="item.image"
          @click="btn(item,key,list, item.percent == 100)"
        />
        <!-- 图片上显示字体 -->
        <slot slot="other" name="img_title" :text="item"></slot>
        <span
          slot="title"
          v-if="item.hasOwnProperty('title')|| item.hasOwnProperty('goods_name')|| item.hasOwnProperty('goodName')"
        >
          <div
            class="title"
            style="display: -webkit-box;
                  -webkit-box-orient: vertical;
                  overflow : hidden;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;"
          >{{item.title?item.title:item.goods_name?item.goods_name:item.goodName}}</div>
        </span>
        <yd-list-other
          slot="other"
          v-if="item.hasOwnProperty('shop_price') || item.hasOwnProperty('shopPrice')"
        >
          <!-- 秒杀 -->
          <div class="products" v-if="item.hasOwnProperty('flashActive')">
            <div
              v-if="item.pointLimit > 0 || item.point > 0"
            >抢购门槛：积分余额不低于{{item.pointLimit > item.point? item.pointLimit: item.point}}</div>
            <!-- 进度条 -->
            <div class="progressBar">
              <yd-progressbar
                type="line"
                :progress="item.percent / 100"
                trail-width="2"
                trail-color="#F80032"
              ></yd-progressbar>
              <span class="progressBar_font" v-if="item.percent > 90 && item.percent <100">即将售罄</span>
            </div>
            <!-- 抢购 -->
            <div class="shop">
              <!-- 原价 -->
              <div class="moeny">
                <div class="shopPrice">￥{{item.shop_price}}</div>
                <div class="moenyShop">
                  <i v-if="item.price && item.price > 0">￥{{item.price}}</i>
                  <span v-if="item.point && item.point > 0" style="font-size:0.5rem;">
                    + {{item.point}}
                    <span style="font-size:0.4rem;">积分</span>
                  </span>
                </div>
              </div>
              <!-- 按钮 -->
              <div
                :class="[flashActive==1?'shop_div_btn_2':'shop_div_btn_1','shop_div_btn',item.percent ==100 ?'prohibit':'']"
                @click.stop="btn(item,key,list, item.percent == 100)"
              >{{flashActive==1? item.percent ==100?'已售罄':'立即抢购' :'提前看'}}</div>
            </div>
          </div>
          <div v-else>
            <!-- 普通商品 -->
            <span
              class="demo-list-price"
              v-if="item.hasOwnProperty('shop_price') || item.hasOwnProperty('shopPrice')"
            >
              <!-- 结果显示=>积分+金额(数据库返回的是真实价格和可以减的积分) -->
              <em>¥</em>
              {{ (item.shop_price?item.shop_price:item.shopPrice) - item.pointAsMoney }}
              <span
                style="font-size: 0.5rem;"
                v-if="item.pointAsMoney > 0"
              >
                + {{item.pointAsMoney}}
                <span style="font-size:0.4rem">积分</span>
              </span>
            </span>
            <span
              class="demo-list-del-price"
              v-if="item.hasOwnProperty('w_price')"
            >¥{{item.w_price}}</span>
            <div v-if="item.hasOwnProperty('text')" class="remarks">{{item.text}}</div>
            <div class="rigth_foot" v-if="item.hasOwnProperty('cart_active')">
              <span>满减</span>
            </div>
          </div>
        </yd-list-other>
      </yd-list-item>
    </yd-list>

    <!-- 横列表 -->
    <div class="transverse_list_div" v-if="list_type == 2">
      <div
        class="transverse_list"
        v-for="(item, key) in list"
        :key="key"
        @click="btn(item,key,list)"
      >
        <div class="transverse_img">
          <img v-lazy="item.image" :alt="item.name" />
        </div>
        <!-- 标题 -->
        <div
          class="transverse_main_title font_line"
          v-if="item.hasOwnProperty('main_title')"
        >{{item.main_title}}</div>
        <!-- 副标题 -->
        <div
          class="transverse_vice_title font_line"
          v-if="item.hasOwnProperty('vice_title')"
        >{{item.vice_title}}</div>
        <!-- 标签 -->
        <div class="tag" v-if="item.hasOwnProperty('tag')">
          <van-tag
            :color="ite.color"
            :text-color="ite.text_color"
            v-for="(ite, k) in item.tag"
            :key="k"
          >{{ite.coment}}</van-tag>
        </div>
      </div>
    </div>

    <!-- 商品 横列表-->
    <div class="transverse_list_div" v-if="list_type == 3">
      <slot name="slot_1"></slot>
      <div class="minx">
        <div
          class="transverse_list"
          v-for="(item, key) in list"
          :key="key"
          @click="btn(item,key,list)"
        >
          <div class="transverse_img">
            <img v-lazy="item.image? item.image : item.imgPath?item.imgPath:item.goodImg" alt="图片" />
          </div>
          <!-- 标题 -->
          <div
            class="transverse_main_title font_line"
            v-if="item.hasOwnProperty('goods_name') || item.hasOwnProperty('title') ||item.hasOwnProperty('goodName')"
          >{{item.goods_name?item.goods_name:item.title?item.title:item.goodName}}</div>
          <!-- 价格 -->
          <div class="price">
            <span>
              ￥{{(item.shop_price?item.shop_price:item.price) - item.pointAsMoney}}
              <span
                style="font-size: 0.5rem;"
                v-if="item.pointAsMoney && item.pointAsMoney > 0"
              >
                + {{item.pointAsMoney}}
                <span style="font-size:0.4rem">积分</span>
              </span>
            </span>
            <div v-if="item.hasOwnProperty('text')" style="color:#999999">{{item.text}}</div>
          </div>
        </div>
      </div>
      <slot name="slot_2"></slot>
    </div>

    <!-- 列表 3列 -->
    <div v-if="list_type == 4" class="shop_box">
      <div
        class="shop"
        v-for="(item, key) in list"
        :key="key"
        @click="btn(item,key,list)"
        :style="{'margin-top':item.hasOwnProperty('lotteryId')?'.8rem':'' }"
      >
        <div class="img">
          <img
            v-lazy="item.image? item.image : item.imgPath ? item.imgPath : item.thumbPath"
            alt="图片"
          />
        </div>
        <div
          class="title"
          style="display: -webkit-box;
                  -webkit-box-orient: vertical;
                  overflow : hidden;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;"
        >
          {{ item.goods_name?
          item.goods_name: item.title ? item.title:item.goodName
          }}
        </div>

        <!-- 活动 -->
        <div v-if="item.hasOwnProperty('lotteryId')">
          <div class="lotteryPrice">
            <span>活动价￥</span>
            <span>{{item.lotteryPrice}}</span>
          </div>
          <div class="shop_price">原价 ￥{{item.shop_price?item.shop_price:item.shopPrice}}</div>
          <div
            :class="['join',item.isSellOut==1 || item.isMax==1?'prohibit':'']"
            @click.stop="joinActivity(item)"
          >{{item.isSellOut==1?'已售罄':item.isMax==1?'人数已满':'立即参与'}}</div>
        </div>
        <!-- 凑单 -->
        <div class="foot" v-else>
          <span>￥{{item.shopPrice}}</span>
          <img src="../assets/shopping.png" alt="商品" @click.stop="joinActivity(item)" />
        </div>
      </div>
      <div class="clear"></div>
    </div>

    <!-- 收藏 -->
    <div v-if="list_type == 6">
      <div v-for="(item, key) in list" :key="key" class="collection_box">
        <!-- 选择框 -->
        <div class="collection_select" v-if="select">
          <van-checkbox v-model="item.active" checked-color="#D7000F" @click="selectFun(item)"></van-checkbox>
        </div>
        <!-- 商品 -->
        <div class="collection_coment" @click="joinActivity(item)">
          <div class="collectionImg">
            <img :src="item.image?item.image: require('../assets/logo_1.png')" alt="图片" />
          </div>
          <div class="shopInfo">
            <div class="top">
              <div
                class="comentFot"
                style="display: -webkit-box; -webkit-box-orient: vertical; overflow : hidden; display: -webkit-box; -webkit-line-clamp: 2;"
              >{{item.name}}</div>
              <div class="number">{{item.text}}</div>
            </div>
            <div class="bottom" v-if="item.hasOwnProperty('goodId')">
              <span>￥</span>
              {{item.shopPrice}}
            </div>
          </div>
        </div>
        <!-- 箭头 -->
        <div v-if="item.hasOwnProperty('storeId')">
          <van-icon name="arrow" size="1.2rem" color="#999999" />
        </div>
      </div>
    </div>

    <!-- 活动列表 -->
    <div v-if="list_type == 7">
      <div class="shop_div" v-for="(item, key) in list" :key="key">
        <div class="shop_div_left">
          <img v-lazy="item.imgPath" :alt="item.goods_name" />
        </div>
        <div class="shop_div_right">
          <div
            class="shop_title"
            style="
                display: -webkit-box;
                -webkit-box-orient: vertical;
                overflow : hidden;
                display: -webkit-box;
                -webkit-line-clamp: 2;"
          >{{item.goods_name}}</div>
          <div class="products" v-if="item.pointLimit > 0 || item.point > 0">
            <span
              style="color: #999;"
            >抢购门槛：积分余额不低于{{item.pointLimit > item.point? item.pointLimit: item.point}}</span>
          </div>
          <div class="shop_div_foot">
            <div class="shop_div_money">
              <div :class="['money_1',flashActive==1?'money_3':'']">
                <span v-if="item.price && item.price > 0">￥</span>
                <span v-if="item.price && item.price > 0">{{item.price}}</span>
                <span v-if="item.point && item.point > 0">
                  + {{item.point}}
                  <span style="font-size:0.5rem;">积分</span>
                </span>
              </div>
              <div class="money_2">
                <span>￥</span>
                <span>{{item.shop_price}}</span>
              </div>
            </div>
            <!-- 判断是否正在开抢 -->
            <div :class="['shop_div_btn',flashActive==1?'flashActive':'']">
              <div
                :class="['shop_div_btn_1',item.percent ==100 ?'prohibit':'']"
                @click="joinActivity(item)"
              >{{flashActive==1? item.percent ==100?'已售罄':'立即抢购' :'提前看'}}</div>
              <div v-if="flashActive==2" class="shop_div_btn_2">{{item.shop_price}}人关注</div>
              <div v-else class="shop_div_btn_3">
                <span>已抢 {{item.percent}}%</span>
                <yd-progressbar
                  type="line"
                  :progress="item.percent / 100"
                  trail-width="8"
                  trail-color="#eb7c84"
                ></yd-progressbar>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {};
  },
  props: ["list", "list_type", "theme", "select", "flashActive"],
  methods: {
    /**
     * joker
     * 获取图片相关参数
     * @param item 点击图片的参数
     * @param key  点击图片的参数的索引
     * @param arr  全部图片
     */
    btn(item, key, arr, isSaleOut = false) {
      this.$emit("introduce", { item, key, arr, isSaleOut });
    },
    joinActivity(item) {
      this.$emit("joinActivity", item);
    },
    // 复选框
    selectFun(item) {
      this.$emit("selectFun", item);
    }
  },
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.font_line {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
// 横列表
.transverse_list_div {
  display: -webkit-box;
  overflow-x: scroll;
  .minx {
    display: flex;
  }
  .transverse_list {
    width: 8.125rem;
    margin-right: 0.625rem;
    .transverse_img {
      img {
        width: 90%;
        height: 7.125rem;
        border-radius: 0.3125rem;
      }
    }
    .transverse_main_title {
      color: #333333;
      font-size: 0.875rem;
      margin: 0.2rem 0;
    }
    .transverse_vice_title {
      color: #999999;
      font-size: 0.625rem;
      margin-bottom: 0.2rem;
    }
    .tag {
      font-size: 0.625rem;
      /deep/ span {
        margin: 0.1rem;
      }
    }
  }
}
.price {
  display: flex;
  align-items: center;
  justify-content: space-between;
  span:nth-child(1) {
    font-size: 0.85rem;
    font-family: DIN-Medium;
    color: #d7000f;
    margin-right: 0.1rem;
  }
  span:nth-child(2) {
    font-size: 0.5rem;
    font-family: DIN-Medium;
    color: #999999;
    text-decoration: line-through;
  }
}
// 3列
.shop_box {
  background: #ffffff;
  padding-bottom: 0.8rem;
  .shop {
    margin-right: 0.24rem;
    width: 32.57%;
    background: #ffffff;
    float: left;
    .img {
      img {
        width: 100%;
        height: 7.0625rem;
      }
    }
    .title {
      width: 100%;
      padding: 0 0.4rem;
      height: 2rem;
    }
    .foot {
      padding: 0.625rem 0.4rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      span {
        color: #d7000f;
        font-family: DINPro-Regular;
        font-size: 0.875rem;
      }
      img {
        width: 1.75rem;
        height: 1.75rem;
      }
    }
    // 活动价
    .lotteryPrice {
      margin-top: 0.5rem;
      padding: 0 0.4rem;
      span {
        color: #d7000f;
        font-family: DINPro-Regular;
        margin-bottom: 0.2rem;
      }
      span:nth-child(1) {
        font-size: 0.8rem;
      }
      span:nth-child(2) {
        font-size: 1rem;
      }
    }
    .shop_price {
      padding: 0 0.4rem;
      font-size: 0.625rem;
      color: #999999;
      font-family: DINPro-Regular;
    }
    .join {
      margin-top: 0.3rem;
      width: 100%;
      height: 1.6875rem;
      background: #d7000f;
      color: #ffffff;
      text-align: center;
      line-height: 1.8rem;
      font-family: DINPro-Regular;
    }
  }
}

.shop:nth-child(3n) {
  margin-right: 0;
}
// 收藏
.collection_box {
  display: flex;
  align-items: center;
  .collection_coment {
    display: flex;
    width: 100%;
  }
  .collection_select {
    /deep/ i {
      width: 1.375rem;
    }
    margin-right: 0.625rem;
  }
  .collectionImg {
    margin-right: 0.625rem;
    // width: 8.75rem;
    // height: 8.75rem;
    img {
      width: 8rem;
      height: 8rem;
      border-radius: 0.5rem;
    }
  }
  .shopInfo {
    position: relative;
    width: 100%;
    .comentFot {
      line-height: 1.5rem;
      font-size: 1rem;
      font-family: PingFang-SC-Medium;
      height: 3.2rem;
    }
    .number {
      font-size: 0.875rem;
      color: #999999;
    }
    .bottom {
      position: absolute;
      bottom: 0;
      font-size: 1rem;
      color: #d7000f;
      font-family: PingFang-SC-Bold;
      span {
        font-size: 0.875rem !important;
      }
    }
  }
}

.shop_div {
  display: flex;
  .shop_div_left {
    img {
      width: 6.875rem;
      height: 6.875rem;
      border-radius: 0.625rem;
    }
  }
  .shop_div_right {
    margin-left: 0.6875rem;
    position: relative;
    width: 100%;
    .shop_title {
      font-family: PingFang-SC-Medium;
      font-size: 0.9375rem;
      color: #333333;
      line-height: 1.2rem;
    }
  }
  .shop_div_foot {
    position: absolute;
    bottom: 0;
    width: 100%;
    display: flex;
    justify-content: space-between;
    .shop_div_money {
      font-family: DIN-Medium;
      position: relative;
      bottom: -1rem;
      .money_1 {
        color: #3ec000;
        span:nth-child(1) {
          font-size: 0.875rem;
        }
        span:nth-child(2) {
          font-size: 1rem;
        }
      }
      .money_2 {
        text-decoration: line-through;
        color: #999999;
        font-size: 0.875rem;
      }
      .money_3 {
        color: #d7000f;
      }
    }
    .shop_div_btn {
      text-align: center;
      .shop_div_btn_1 {
        width: 5.5625rem;
        height: 2.0625rem;
        background: #3ec000;
        font-size: 1rem;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #ffffff;
        border-radius: 0.875rem;
      }
      .shop_div_btn_2 {
        font-size: 0.625rem;
        color: #999999;
        margin-top: 0.3rem;
      }
    }
    .flashActive {
      .shop_div_btn_1 {
        background: #d7000f;
        margin-left: 1.8rem;
      }
      .shop_div_btn_3 {
        width: 7.3rem;
        display: flex;
        justify-content: flex-end;
        align-items: center;
        margin-top: 0.3rem;
        span {
          color: #d7000f;
          font-size: 0.75rem;
          margin-right: 0.4rem;
        }
      }
      /deep/ .yd-progressbar {
        width: 3.2rem;
        /deep/ svg {
          border-radius: 3.125rem !important;
          border: 1px solid #eb7c84 !important;
        }
      }
    }
  }
}
// 已售罄
.prohibit {
  background: #e6e6e6 !important;
  color: #666666 !important;
}

// 首页 爆品列表
.products {
  // position: relative;
  .page {
    color: #333333;
    font-size: 0.75rem;
    font-family: PingFangSC-Medium;
    .tag {
      width: 0.6875rem;
      height: 0.6875rem;
      margin-right: 0.2rem;
    }
  }
  .progressBar {
    display: flex;
    align-items: center;
    /deep/ .yd-progressbar {
      width: 45%;
      margin-right: 1rem;
    }
    .progressBar_font {
      color: #575757;
    }
  }
  .shop {
    display: flex;
    align-items: center;
    margin-top: 0.5rem;
    justify-content: space-between;
    .moeny {
      .shopPrice {
        font-size: 0.625rem;
        color: #747474;
        text-decoration: line-through;
      }
      .moenyShop {
        margin-top: 0.1rem;
        font-size: 1rem;
        color: #e80002;
      }
    }

    .shop_div_btn {
      width: 3.4375rem;
      height: 1.3125rem;
      color: #ffffff;
      border: none;
      display: flex;
      justify-content: center;
      align-items: center;
      font-family: PingFangSC-Medium;
    }
    .shop_div_btn_1 {
      background: #3ec000;
    }
    .shop_div_btn_2 {
      background: #e80002;
    }
  }
}
/deep/ .yd-list-theme1 .yd-list-item .yd-list-title {
  overflow: inherit;
  white-space: inherit;
  height: 2.5rem;
  text-align: left;
  word-wrap: break-word;
  text-overflow: inherit;
}
</style>

