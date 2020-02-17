<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar" @navRight="navRightBtn" />
    <!-- 去凑单 -->
    <router-link to="/collectbill" v-if="isCollectbill">
      <div class="lable_list">
        <div class="lable_list_left">
          <span>全场加价购</span>
          <span>满5000元即赠一把价值1699元的智能门锁</span>
        </div>
        <div class="lable_list_right">
          <span>去凑单</span>
          <van-icon name="arrow" color="#D7000F" />
        </div>
      </div>
    </router-link>

    <div class="coment">
      <!-- 购物车列表 -->
      <div v-if="goods.length > 0" class="goodslist">
        <div v-for="(item,key) in goods" :key="key">
          <ShopCard
            :order="item"
            class="goods"
            :fatherIndex="key"
            @checkedFatherStroe="checkedFatherStroe"
            @checkedStroe="checkedStroe"
            @calculation="calculation"
            @introduceSelf="JumpGodsInfo"
          />
        </div>
        <!-- 满额送锁 -->
        <div class="activityGood" v-if="activityActive" @click="JumpGodsInfo(give)">
          <div class="activityGoodLeft">
            <img src="../../assets/zeng.png" alt="商品" class="shopIcon" />
            <img :src="give.original_img" alt="商品" class="shop" />
          </div>
          <div class="activityGoodRight">
            <div
              class="activityGoodRightTitle"
              style="
              display: -webkit-box;
              -webkit-box-orient: vertical;
              overflow : hidden;
              display: -webkit-box;
              -webkit-line-clamp: 2;"
            >{{give.goods_name}}</div>
            <div class="activityGoodRightMoeny">￥{{give.shop_price}}</div>
          </div>
        </div>
      </div>
      <div v-else class="no_good">
        <!-- <div> -->
        <img src="../../assets/no_good.png" alt="购物车" />
        <!-- </div> -->
        <div class="font">购物车中还没有商品，赶紧选购吧！</div>
        <router-link to="/">
          <van-button type="danger" class="go_shopping">去逛逛</van-button>
        </router-link>
      </div>

      <!-- 猜你喜欢 -->
      <Like @introduceSelf="JumpGodsInfo" />
    </div>

    <!-- 提交订单 -->
    <van-submit-bar
      class="submit-bar"
      :price="totalMoney"
      button-text="结算"
      @submit="Handle(1)"
      v-if="!edit"
    >
      <van-checkbox
        v-model="checked"
        checked-color="#D7000F"
        @click="wholeChecked(2)"
        class="btn3"
      >全选</van-checkbox>
    </van-submit-bar>

    <!-- 编辑 -->
    <div v-else class="submit-bar edit">
      <van-checkbox
        v-model="checked"
        checked-color="#D7000F"
        class="btn3"
        @click="wholeChecked(2)"
      >全选</van-checkbox>
      <div class="edit_right">
        <van-button class="btn1" @click="Handle(2)">移入收藏</van-button>
        <van-button class="btn2" @click="Handle(3)">删除</van-button>
      </div>
    </div>

    <!-- 底部导航 -->
    <Nav class="foot_nav" :btn="3" />
  </div>
</template>
<script>
import Nav from "../../components/nav";
import Like from "../../components/like";
import ShopCard from "../../components/shopCard";
import Navbar from "../../components/navbar";
import {
  cartList,
  updateCartNum,
  confirmOrder,
  deleteCart,
  moveToCollect,
  hasPromLock,
  goodsDetails
} from "../../../api/cart.js";
import commJs from "../../comm";
export default {
  components: {
    Nav,
    Navbar,
    ShopCard,
    Like
  },
  data() {
    return {
      // 是否显示去凑单
      isCollectbill: false,
      // 是否编辑
      edit: false,
      // 是否全选
      checked: false,
      // 商品详情
      goods: "",
      // 商品合计
      totalMoney: 0,
      // 是否选择商品
      checkedActive: false,
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "首页",
            to: "/",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "购物车",
          //颜色
          color: "#333333"
        },
        right: [
          {
            coment: "编辑",
            to: "",
            color: "#666666"
          }
        ]
      },
      activityActive: false,
      // 赠送商品
      give: {
        goods_name: "",
        original_img: "",
        shop_price: "",
        goodsId: ""
      }
    };
  },
  methods: {
    // 编辑
    navRightBtn(item) {
      if (item.coment == "编辑") {
        this.edit = !this.edit;
      }
    },
    // 跳转商品详情
    JumpGodsInfo(data) {
      // console.log(data);
      let path;
      if (data.hasOwnProperty("item")) {
        path = `/goodsinfo?goodId=${data.item.goods_id}&u=/cart`;
      } else {
        path = `/goodsinfo?goodId=${data.goodsId}&u=/cart`;
      }
      this.$router.push({ path });
    },
    // 获取购物车列表
    cartListFun() {
      let that = this;
      cartList().then(res => {
        // console.log(res);
        if (res.status == 0) {
          // 整理数据
          that.goods = res.data.map(items => {
            items.orderGood = items.list.map(item => {
              item.imgPath = item.originalImg;
              item.goodName = item.goodsNam;
              item.finalPrice = item.goodsPrice;
              item.specName = item.specKeyName;
              item.goodNum = item.goodsNum;
              item.goodName = item.goodsName;
              item.checked = false;
              return item;
            });
            items.checked = false;
            return items;
          });
          var active = "success";
          this.total();
          // console.log(that.goods);
        } else {
          var active = "error";
          commJs.Dialog2(res.msg, active, 1000, this);
        }
      });
    },

    // 处理 1：提交订单,2：移入收藏,3：删除
    Handle(mun) {
      let that = this;
      if (!this.checkedActive) {
        commJs.Dialog2("请选择商品", "error", 1000, this);
        return;
      }

      let arr = this.goods;
      // console.log(arr);
      let data = new Array();
      arr.map(element => {
        element.orderGood.map(item => {
          if (item.checked) {
            data.push(item.id);
          }
        });
      });
      console.log(data);
      // 提交
      if (mun == 1) {
        that.$dialog.loading.open("请求中...");
        confirmOrder(data).then(res => {
          that.$dialog.loading.close();
          if (res.status == 0) {
            sessionStorage.setItem("isCart", 1);
            sessionStorage.setItem("ordinaryorder", JSON.stringify(res.data));
            // 跳转
            that.$router.push({
              path: "/ordinaryorder"
            });
          } else {
            commJs.Dialog2(res.msg, "error", 1000, that);
          }
        });
      }
      // 移入收藏
      else if (mun == 2) {
        commJs
          .Dialog("您确定要移入收藏夹？", "")
          .then(res => {
            if (res === "confirm") {
              that.$dialog.loading.open("请求中...");
              moveToCollect(data).then(res => {
                that.$dialog.loading.close();
                if (res.status == 0) {
                  commJs.Dialog2(res.msg, "success", 2000, that);
                  that.cartListFun();
                } else {
                  commJs.Dialog2(res.msg, "error", 1000, that);
                }
              });
              return;
            }
          })
          .catch(err => {
            that.$dialog.loading.close();
          });
      }
      // 删除
      else if (mun == 3) {
        commJs
          .Dialog("您确定删除商品？", "")
          .then(res => {
            if (res === "confirm") {
              that.$dialog.loading.open("请求中...");
              deleteCart(data).then(res => {
                that.$dialog.loading.close();
                if (res.status == 0) {
                  commJs.Dialog2(res.msg, "success", 1000, that);
                  that.cartListFun();
                } else {
                  commJs.Dialog2(res.msg, "error", 1000, that);
                }
              });
              return;
            }
          })
          .catch(err => {
            that.$dialog.loading.close();
          });
      }
    },

    // 选择店铺,默认选择下面的商品
    checkedFatherStroe(data) {
      let arr = this.goods;
      arr.forEach((element, keys, arr) => {
        element.orderGood.forEach((item, key) => {
          if (keys == data.index) {
            this.goods[keys].orderGood[key].checked = !this.goods[keys].checked;
            this.checkedActive = this.goods[keys].orderGood[key].checked
              ? true
              : false;
          }
        });
      });
      this.goods[data.index].checked = !this.goods[data.index].checked;
      this.wholeChecked(1);
    },

    // 选择商品
    checkedStroe(data) {
      // console.log(data);
      let arr = this.goods[data.fatherIndex].orderGood;
      let len = arr.length;
      let numTrue = 0;
      setTimeout(() => {
        arr.forEach((element, keys, arr) => {
          if (element.checked) {
            numTrue++;
          }
          // 关联店铺
          if (numTrue == len) {
            this.goods[data.fatherIndex].checked = true;
          } else {
            this.goods[data.fatherIndex].checked = false;
          }
        });
        this.checkedActive = numTrue > 0 ? true : false;
        this.wholeChecked(1);
      }, 10);
    },
    // 修改商品数量
    calculation(data) {
      let that = this;
      let params = {
        cartId: data.id,
        type: data.type
      };
      updateCartNum(params).then(res => {
        if (res.status == 0) {
          that.total();
        } else {
          commJs.Dialog2(res.msg, "error", 1000, this);
        }
      });
    },
    // 计算
    total() {
      let arr = this.goods;
      let total = 0;
      arr.forEach((element, keys, arr) => {
        element.orderGood.forEach((item, key, arr) => {
          if (item.checked) {
            total = total + item.goodNum * item.finalPrice;
          }
        });
      });
      this.totalMoney = total * 100;
      this.hasPromLockFun();
    },
    // 全选
    wholeChecked(mun) {
      let arr = this.goods;
      let len = arr.length;
      // 监听是否全部选择商品
      if (mun == 1) {
        let stroeShopNum = 0;
        arr.forEach((element, keys, arr) => {
          if (element.checked) {
            stroeShopNum++;
          }
          if (len == stroeShopNum) {
            this.checked = true;
          } else {
            this.checked = false;
          }
        });
      } else if (mun == 2) {
        arr.forEach((element, keys, arr) => {
          this.goods[keys].checked = !this.checked;
          element.orderGood.forEach((item, key, arr) => {
            this.goods[keys].orderGood[key].checked = !this.checked;
          });
        });
        this.checkedActive = !this.checked;
      }
      this.total();
    },
    // 满5000送锁
    hasPromLockFun() {
      let that = this;
      hasPromLock({ type: 4 }).then(res => {
        if (res.status == 0) {
          //判断活动是否存在
          if (res.data.activity == 1) {
            that.activityCondition(res.data.goodsId, res.data.money);
            that.isCollectbill = true;
          } else if (res.data.activity == 0) {
            that.isCollectbill = false;
          }
        } else {
          commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
    },
    // 计算是否满足满额送锁条件
    activityCondition(goodsId, money) {
      let that = this;
      if (that.totalMoney / 100 >= money) {
        that.activityActive = true;
        that.activityGood(goodsId);
      } else {
        that.activityActive = false;
      }
    },
    // 获取活动产品
    activityGood(goodsId) {
      let that = this;
      goodsDetails({ goodId: goodsId }).then(res => {
        if (res.status == 0) {
          that.give.goods_name = res.data.good.goods_name;
          that.give.original_img = res.data.good.original_img;
          that.give.shop_price = res.data.good.shop_price;
          that.give.goodsId = goodsId;
        } else {
          commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
    }
  },
  mounted() {
    // 判断是否登录
    if (commJs.testLogin(this, "/cart") == false) {
      return;
    }
    // 请求列表
    this.cartListFun();
    if (this.$route.query.u) {
      this.navbar.left[0].to = this.$route.query.u;
      this.navbar.left[0].coment = "返回";
    }
    // 清除缓存
    commJs.deletData(["lotteryGoodId", "flashGoodId"]);
  }
};
</script>

<style lang="less" scoped>
.goods {
  background: #ffffff;
  margin-bottom: 0.625rem;
}
/deep/ .shop_title {
  padding: 0.9375rem;
  border-bottom: 1px solid #e6e6e6;
}
.coment {
  margin-bottom: 3.3rem;
}
// 编辑
.edit {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 3.18rem;
  background: #ffffff;
  position: fixed;
  width: 100%;
  bottom: 3rem;
  .edit_right {
    display: flex;
    .btn1,
    .btn2 {
      line-height: 34px;
      height: 34px;
      display: flex;
      justify-content: center;
      align-items: center;
      color: #ffffff;
      font-size: 1rem;
      width: 6.5rem;
      border: inherit;
    }
    .btn1 {
      background: #1e2329;
      border-top-left-radius: 18px;
      border-bottom-left-radius: 18px;
    }
    .btn2 {
      background: #d7000f;
      border-top-right-radius: 18px;
      border-bottom-right-radius: 18px;
    }
  }
}
.submit-bar {
  /deep/ .van-submit-bar__button {
    line-height: 34px;
    height: 34px;
    background: #d7000f;
    border-radius: 18px;
  }
  .btn3 {
    margin-left: 1rem;
    font-size: 1rem;
    display: flex;
    align-items: center;
  }
}
// 标签列表
.lable_list {
  display: flex;
  justify-content: space-between;
  align-items: center;

  background: #fef9f5;
  padding: 0.3rem 0.8rem;
  .lable_list_left {
    width: 92%;
    font-family: PingFang-SC-Medium;
    line-height: 1.3rem;
    span:nth-child(1) {
      background: url("../../assets/label_1.png") no-repeat center;
      background-size: 100% 100%;
      padding: 0.2rem 0.3rem 0.05rem 0.3rem;
      color: #ffffff;
      font-size: 0.5625rem;
    }
    span:nth-child(2) {
      font-size: 0.6875rem;
      color: #666666;
    }
  }
  .lable_list_right {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    width: 3.5rem;
    color: #d7000f;
  }
}
// 购物车为空
.no_good {
  text-align: center;
  background: #ffffff;
  img {
    width: 7.375rem;
    height: 7.375rem;
  }
  .font {
    font-family: PingFang SC;
    font-size: 0.75rem;
    color: #999999;
  }
  /deep/ .van-button {
    margin: 1.875rem 0;
    color: #d7000f;
    font-size: 1rem;
    font-family: PingFang SC;
    width: 11.875rem;
    height: 2.75rem;
    border-radius: 1.375rem;
    background: #ffffff;
    border-color: #d7000f;
  }
}
// 活动商品
.activityGood {
  display: flex;
  background: #ffffff;
  padding: 0.625rem 0.875rem;
  .activityGoodLeft {
    position: relative;
    .shopIcon {
      width: 3.5rem;
      height: 3.5rem;
      position: absolute;
      top: -0.5rem;
      left: -0.625rem;
      transform: rotate(-30deg);
      -ms-transform: rotate(-30deg); /* IE 9 */
      -moz-transform: rotate(-30deg); /* Firefox */
      -webkit-transform: rotate(-30deg); /* Safari 和 Chrome */
      -o-transform: rotate(-30deg); /* Opera */
    }
    .shop {
      width: 5rem;
      height: 5rem;
    }
  }
  .activityGoodRight {
    margin-left: 0.625rem;
    position: relative;
    .activityGoodRightTitle {
      font-size: 0.875rem;
      line-height: 1.2rem;
    }
    .activityGoodRightMoeny {
      position: absolute;
      bottom: 0.625rem;
      font-size: 0.9rem;
      color: #d7000f;
    }
  }
}
.goodslist {
  margin-bottom: 0.625rem;
}
/deep/ .van-submit-bar {
  bottom: 3.0625rem;
}
</style>

