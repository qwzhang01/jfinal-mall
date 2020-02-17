<template>
  <div>
    <!-- 头部 -->
    <Navbar :navbar="navbar" />
    <div>
      <!-- 等待买家付款 -->
      <div class="alert" v-if="order.status == 1">
        <div>等待买家付款</div>
        <div>
          您的订单已提交，请在
          <yd-countdown :time="order.closeTime"></yd-countdown>内完成支付，超时订单自动取消
        </div>
      </div>
      <!--收货人地址 -->
      <Address :address="order" @addresslist="jump" />
      <!-- 商品 -->
      <div class="shop">
        <ShopCard :order="order" @jump="jump" @introduceSelf="introduceSelf" />
        <div slot="footer">
          <div class="footer">
            <div class="shop_cost">
              <div>商品总额</div>
              <div>￥{{order.orderAmount}}</div>
            </div>
            <div class="shop_cost">
              <div>运费</div>
              <div>￥{{order.shippingPrice}}</div>
            </div>
            <div class="shop_cost" v-if="order.pointAsMoney && order.pointAsMoney > 0">
              <div>积分</div>
              <div>{{order.pointAsMoney}}</div>
            </div>
            <div class="shop_cost">
              <div>优惠信息</div>
              <div>{{order.activity_type === 2?'秒杀订单':''}}</div>
            </div>
          </div>
          <div class="shop_cost">
            <div></div>
            <div>
              实际付款：
              <span>￥{{order.orderAmount}}</span>
            </div>
          </div>
        </div>
      </div>
      <!-- 订单信息 -->
      <div class="orderinfo">
        <div class="tr">
          <div class="tb">
            <span>订单编号：</span>
            <span>{{order.orderSn}}</span>
            <!-- <input type="hidden"  :value="order.orderSn"> -->
            <van-button size="mini" round class="copy" @click="copey('.copy',order.orderSn)">复制</van-button>
          </div>
          <div class="tb">
            <span>下单时间：</span>
            <span>{{order.addTime}}</span>
          </div>
        </div>
        <div class="tr">
          <div class="tb">
            <span>发票抬头类型：</span>
            <span>{{order.invoiceType}}</span>
          </div>
          <div class="tb">
            <span>发票抬头：</span>
            <span>{{order.invoiceTitle}}</span>
          </div>
          <div class="tb">
            <span>发票内容：</span>
            <span>{{order.invoiceDesc}}</span>
          </div>
        </div>
        <div class="tr">
          <div class="tb">
            <span>支付方式：</span>
            <span>{{order.payName}}</span>
          </div>
          <div class="tb">
            <span>配送方式：</span>
            <span>{{order.shippingName}}</span>
          </div>
        </div>
        <div class="operation">
          <!-- 操作类型 1：取消订单 2：去支付 3：提醒发货 4：申请退款 5：查看物流 6：确定收货 7：去评价 -->
          <van-button size="mini" v-if="order.status==1" @click="operation(order,1)">取消订单</van-button>
          <van-button
            size="mini"
            type="danger"
            style="background:#D7000F"
            v-if="order.status==1"
            @click="operation(order,2)"
          >去支付</van-button>
          <van-button
            size="mini"
            plain
            type="danger"
            v-if="order.status==2"
            @click="operation(order,3)"
          >提醒发货</van-button>
          <!-- <van-button size="mini"  v-if="order.status==2" @click="operation(order,4)">申请退款</van-button> -->
          <van-button size="mini" v-if="order.status==3" @click="operation(order,5)">查看物流</van-button>
          <van-button
            size="mini"
            plain
            type="danger"
            v-if="order.status==3"
            @click="operation(order,6)"
          >确定收货</van-button>
          <!-- <van-button size="mini" plain type="danger" v-if="order.status==4" @click="operation(order,7)" >申请退款</van-button> -->
          <van-button
            size="mini"
            plain
            type="danger"
            v-if="order.status==4"
            @click="operation(order,8)"
          >去评价</van-button>
        </div>
      </div>
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
        <List :list_type="1" :list="like.list" @introduce="favouriteGood" />
      </van-list>
    </div>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import ShopCard from "../../components/shopCard";
import Address from "../../components/address";
import comm_js from "../../comm/index.js";
import List from "../../components/list";
import {
  PageFavourite,
  OrderDetail,
  OrderList,
  RemindGoods,
  Cancel,
  Refund,
  Confirm
} from "../../../api";
export default {
  components: {
    Navbar,
    List,
    Address,
    ShopCard
  },
  data() {
    return {
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/orderlist",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "订单详情",
          //颜色
          color: "#333333"
        }
      },
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
      // 清单详情
      order: {
        // 订单id
        storeId: "",
        mobile: "",
        nickname: "",
        orderGood: ""
      },
      tb: ""
    };
  },
  methods: {
    // 跳转到相应的地址
    jump(type) {
      // 地址
      let path = "";
      if (type == "address") {
        // 待付款
        if (this.tb == 1) {
          path =
            "/addresslist?u=/orderinfo?orderId=" +
            this.order.orderId +
            "&tab1=" +
            this.tb +
            "&type=orderinfo";
        } else {
          return;
        }
      }
      // 商家店铺
      else if (type == "store") {
        // window.location.href = `${comm_js.host.jump_domain}/Mobile/Store/index?store_id=${item.storeId}`
        path =
          "/storepage?u=/orderinfo?orderId=" +
          this.order.orderId +
          "&tab1=" +
          this.tb +
          "&type=orderinfo";
      }
      // 跳转
      this.$router.push({
        path
      });
    },
    /**
     * joker
     * 复制
     */
    copey(Class, item) {
      comm_js.copey(Class, item, this);
    },
    /**
     * joker
     * 订单详情
     * @param orderId number   订单id
     * @param user_id number    用户id
     */
    OrderDetailFun() {
      let that = this;
      let params = {
        orderId: this.order.orderId
      };
      OrderDetail(params).then(res => {
        console.log(res);
        if (res.status == 0) {
          res.data.status == 4 ? (that.order.slot = true) : ""; // 开启
          that.order = Object.assign(that.order, res.data);
          // 待支付，可以去更换地址
          if (this.tb == 1) {
            if (sessionStorage.consignee) {
              let consignee = JSON.parse(sessionStorage.getItem("consignee"));
              that.order.nickname = consignee.nickname;
              that.order.address = consignee.address;
              that.order.mobile = consignee.mobile;
              that.order.address_id = consignee.address_id;
            }
          }
        } else {
          comm_js.Dialog2(res.msg, "success", 1500, that);
        }
      });
    },
    /**
     * 操作
     * @param type number 操作类型 1：取消订单 2：去支付 3：提醒发货 4：申请退款 5：查看物流 6：确定收货 7：去评价
     * @param item object 商品数据
     */
    operation(item, type) {
      console.log(item);

      let that = this;
      let params = {
        userId: sessionStorage.getItem("userId")
          ? sessionStorage.getItem("userId")
          : "",
        orderId: item.orderId
      };

      if (type == 1) {
        // 取消订单
        comm_js.Dialog("温馨提示", "您确定取消该订单？").then(res => {
          if (res === "confirm") {
            this.CancelFun(item);
          }
        });
        console.log("取消订单");
      } else if (type == 2) {
        // 去支付
        // 在微信环境中
        if (comm_js.isWeiXin()) {
          comm_js.WxGzLanuch(
            sessionStorage.getItem("userId"),
            item.orderAmount,
            item.orderSn,
            "myself",
            this
          );
        } else {
          // h5 环境中
          let jump_url = comm_js.host.jump_domain + "/#/myself";
          comm_js.WxLanuch(
            sessionStorage.getItem("userId"),
            item.orderAmount,
            item.orderSn,
            jump_url,
            that
          );
        }
      } else if (type == 3) {
        // 提醒发货
        console.log("提醒发货");
        this.RemindGoodsFun(item);
      } else if (type == 4) {
        // 申请退款

        console.log("申请退款");
      } else if (type == 5) {
        // 查看物流
        console.log("查看物流");
        this.$router.push({
          path: "/logistics",
          query: {
            orderId: item.orderId,
            page: "orderinfo"
          }
        });
      } else if (type == 6) {
        // 确定收货
        that.ConfirmFun(item);
        console.log("确定收货");
      } else if (type == 7) {
        // 去评价
        console.log("去评价");
        this.$router.push({
          path: "/evaluate",
          query: {
            orderId: item.orderId,
            page: "orderinfo"
          }
        });
      }
    },
    // 确认收货
    ConfirmFun(params) {
      let that = this;
      Confirm(params).then(res => {
        that.alertFun(res);
      });
    },
    // 提醒发货
    RemindGoodsFun(params) {
      let that = this;
      RemindGoods(params).then(res => {
        that.alertFun(res);
      });
    },
    // 取消订单
    CancelFun(params) {
      let that = this;
      Cancel(params).then(res => {
        if (res.status == 0) {
          let orderId = this.order.orderId;
          let tb = this.tb;
          Object.assign(this.$data, this.$options.data());
          this.order.orderId = orderId;
          this.tb = tb;
          this.navbar.left[0].to = "/orderlist?tabl=" + tb;
          that.alertFun(res);
          this.PageFavouriteFun();
        }
      });
    },
    // 提示语
    alertFun(res) {
      let that = this;
      if (res.status == 0) {
        that.OrderDetailFun();
        comm_js.Dialog2(res.msg, "success", 1500, that);
      } else {
        comm_js.Dialog2(res.msg, "error", 1500, that);
      }
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
            if (res.data.totalPage > that.like.pageNumber) {
              that.like.pageSize = res.data.pageSize;
              that.like.totalPage = res.data.totalPage;
              that.like.list.push(...res.data.list);
              that.like.pageNumber = parseInt(that.like.pageNumber) + 1;
            } else {
              that.loading = false;
            }
            // 加载状态结束
            that.loading = false;
          }, 500);
        }
      });
    },

    /**
     * 订单列表中商品点击跳转
     */
    introduceSelf(data) {
      let that = this;

      sessionStorage.setItem(
        "upper",
        "/orderinfo?orderId=" + this.order.orderId + "&tab1=" + this.tb
      );
      this.$router.push({
        path: "goodsinfo?goodId=" + (data.goodId ? data.goodId : data.goods_id)
      });
    },
    /**
     * 猜你喜欢点击商品
     */
    favouriteGood(item) {
      let good = item.item;
      this.introduceSelf(good);
    }
  },
  mounted() {
    if (this.$route.query.orderId) {
      this.order.orderId = this.$route.query.orderId;
      this.OrderDetailFun();
    }
    this.tb = this.$route.query.tabl ? this.$route.query.tabl : 0;
    this.navbar.left[0].to = "/orderlist?tabl=" + this.tb;
    this.PageFavouriteFun();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
//  等待买家付款
.alert {
  height: 5.375rem;
  background: #d7000f;
  color: #ffffff;
  padding-left: 2.5rem;
  padding-right: 0.9375rem;
  padding-top: 0.5rem;
  line-height: 1.5rem;
  div:nth-child(1) {
    font-size: 1rem;
  }
  div:nth-child(2) {
    font-size: 0.8125rem;
  }
}

// 商品
.shop {
  margin-top: 0.625rem;
}

.shop_cost {
  display: flex;
  height: 2.4375rem;
  align-items: center;
  justify-content: space-between;
  padding: 0 0.9375rem;
  background: #ffffff;
  div:nth-child(1) {
    font-size: 0.8125rem;
    color: #666666;
  }
  div:nth-child(2) {
    color: #333333;
    font-size: 0.8125rem;
    span {
      color: #d7000f;
    }
  }
}
.footer {
  border-bottom: 1px solid #e6e6e6;
}
// 订单详情
.orderinfo {
  font-size: 0.8125rem;
  color: #666666;
  margin-top: 0.625rem;
  background: #ffffff;
  .copy {
    color: #666666;
    margin-left: 1rem;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .tr {
    padding: 0 0.9375rem 0.9375rem 0.9375rem;
    border-bottom: 1px solid #e6e6e6;
  }
  .tb {
    padding-top: 0.625rem;
    display: flex;
  }
}
.operation {
  height: 2.9375rem;
  padding: 0 0.9375rem;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  button {
    font-size: 0.875rem;
    border-radius: 0.625rem;
    height: 1.6875rem;
    width: 5rem;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
/deep/ .yd-tab-nav-scoll .yd-tab-nav .yd-tab-nav-item {
  padding: 0 1rem;
}
// 猜你喜欢
.like {
  margin-top: 0.625rem;
  background: #ffffff;
  padding: 0 0.5rem;
  .title {
    background: url("../../assets/lick.png") no-repeat center;
    background-size: 100% 100%;
    font-family: PingFang-SC-Bold;
    color: #333333;
    font-size: 1rem;
    padding-top: 0.9375rem;
    padding-bottom: 1.25rem;
    text-align: center;
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
</style>
