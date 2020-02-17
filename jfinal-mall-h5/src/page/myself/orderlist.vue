<template>
  <div>
    <!-- 头部导航 -->
    <Navbar :navbar="navbar" />
    <!-- 订单列表 -->
    <van-tabs v-model="tab1" animated swipeable @change="onclick_tab">
      <van-tab v-for="(items,key) in tab_data" :key="key" :title="items.label">
        <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
          <!--  商品 tag="标签" -->
          <van-panel
            icon="shop-o"
            :title=" item.storeName + '>'"
            desc
            :status="
                  item.status==1?'待付款':
                  item.status==2?'已支付':
                  item.status==3?'已发货':
                  item.status==4?'已收货':
                  item.status==5?'退款中':
                  item.status==6?'已退款':
                  item.status==7?'已关闭':
                  item.status==8?'已完成':
                  '异常订单'"
            v-for="(item,index) in list"
            :key="index"
            @click.stop="jump_store(item)"
          >
            <!-- 卡片 -->
            <div
              class="card"
              @click.stop="jump(items,item)"
              v-for="(items,i) in item.orderGood"
              :key="i"
            >
              <div class="card_lfet">
                <img :src="items.imgPath" />
              </div>
              <div class="card_fight">
                <div class="card_fight_top">
                  <div
                    class="goodname"
                    style="display: -webkit-box;
                            -webkit-box-orient: vertical;
                            overflow : hidden;
                            display: -webkit-box;
                            -webkit-line-clamp: 2;"
                  >{{items.goodName}}</div>
                  <div class="goodPrice">
                    <span>￥{{items.finalPrice}}</span>
                  </div>
                </div>
                <div class="card_fight_bottom">
                  <div>规格：{{items.specName}}</div>
                  <div>x{{items.goodNum}}</div>
                </div>
              </div>
            </div>

            <div slot="footer">
              <div class="shop_cost">
                <div v-if="item.status > 1">更多</div>
                <div>
                  共 {{item.goodTotalNum}} 件商品 合计：
                  <span class="money">
                    <i v-if="item.orderAmount && item.orderAmount > 0">￥{{item.orderAmount}}</i>
                    <i v-if="item.pointAsMoney && item.pointAsMoney > 0">+ {{item.pointAsMoney}}积分</i>
                  </span>
                  (含运费￥{{item.shippingPrice}})
                </div>
              </div>
              <div class="operationOder" style="padding: 0">
                <!-- 待付款 -->
                <div v-if="item.status==1" class="style_div">
                  <van-button size="mini" @click.stop="operation(item,1)">取消订单</van-button>
                  <van-button size="mini" plain type="danger" @click.stop="operation(item,2)">去支付</van-button>
                </div>
                <!-- 待发货 -->
                <div v-if="item.status==2" class="style_div">
                  <van-button size="mini" plain type="danger" @click.stop="operation(item,3)">提醒发货</van-button>
                  <!-- <van-button size="mini" @click.stop="operation(item,4)" >申请退款</van-button> -->
                </div>
                <!-- 待收货 -->
                <div v-if="item.status==3" class="style_div">
                  <van-button size="mini" @click.stop="operation(item,5)">查看物流</van-button>
                  <!-- <van-button size="mini" @click.stop="operation(item,4)" >申请售后</van-button> -->
                  <van-button size="mini" plain type="danger" @click.stop="operation(item,6)">确定收货</van-button>
                </div>
                <!-- 待评价 -->
                <div v-if="item.status==4" class="style_div">
                  <!-- <van-button size="mini" @click.stop="operation(item,4)" >申请售后</van-button> -->
                  <!-- <van-button size="mini" @click.stop="operation(item,4)" >申请退款</van-button> -->
                  <van-button size="mini" plain type="danger" @click.stop="operation(item,7)">去评价</van-button>
                </div>
                <!-- 退换货 -->
                <!-- <div v-if="item.status==5" class="style_div" >
                        <van-button size="mini"  @click.stop="operation(item,5)" >查看物流</van-button>
                </div>-->
              </div>
            </div>
          </van-panel>
        </van-list>
      </van-tab>
    </van-tabs>
  </div>
</template>

<script>
import { Dialog } from "vant";
import comm_js from "../../comm/";
import Navbar from "../../components/navbar";
import ShopCard from "../../components/shopCard";
import { OrderList, RemindGoods, Cancel, Confirm } from "../../../api/index.js";
export default {
  components: {
    Navbar,
    ShopCard
  },
  data() {
    return {
      // tabl索引
      tab1: 0,
      // 列表数据
      tab_data: [
        { label: "全部" },
        { label: "待付款" },
        { label: "待发货" },
        { label: "待收货" },
        { label: "待评价" }
        // {label: '退换货'}
      ],
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/myself",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "我的订单",
          //颜色
          color: "#333333"
        }
      },
      // 上拉加载状态
      loading: false,
      finished: false,
      // 查询类型
      type: 0,
      // 页码
      pageNumber: 1,
      // 每页显示条数
      pageSize: 10,
      // 总页面
      totalPage: 1,
      // 列表
      list: []
    };
  },
  methods: {
    // 跳转
    jump(items, item) {
      // console.log(item);
      this.$router.push({
        path: "/orderinfo",
        query: {
          orderId: item.orderId,
          tab1: this.tab1
        }
      });
    },
    /**
     * 操作
     * @param type number 操作类型 1：取消订单 2：去支付 3：提醒发货 4：申请退款 5：查看物流 6：确定收货 7：去评价
     * @param item object 商品数据
     */
    operation(item, type) {
      // console.log(item);
      let that = this;
      let params = {
        userId: sessionStorage.getItem("userId")
          ? sessionStorage.getItem("userId")
          : "",
        orderId: item.orderId
      };

      if (type == 1) {
        // 取消订单
        comm_js.Dialog("温馨提示", "您确定取消该订单？", Dialog).then(res => {
          if (res === "confirm") {
            that.CancelFun(params);
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
            "/",
            this
          );
        } else {
          // h5 环境中
          let jump_url = comm_js.host.jump_domain + "/#/";
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
        this.RemindGoodsFun(params);
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
            tab1: this.tab1
          }
        });
      } else if (type == 6) {
        // 确定收货
        that.ConfirmFun(params);
        console.log("确定收货");
      } else if (type == 7) {
        // 去评价
        console.log("去评价");
        this.$router.push({
          path: "/evaluate",
          query: {
            orderId: item.orderId,
            tab1: this.tab1
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
      // console.log(params);
      Cancel(params).then(res => {
        that.alertFun(res);
      });
    },
    // 提示语
    alertFun(res) {
      let that = this;
      if (res.status == 0) {
        comm_js.Dialog2(res.msg, "success", 1500, that);
        that.pageNumber = 1;
        that.OrderListFun(true);
      } else {
        comm_js.Dialog2(res.msg, "error", 1500, that);
      }
    },
    // 获取列表
    OrderListFun(active = false) {
      let that = this;
      let params = {
        userId: sessionStorage.getItem("userId"),
        type: this.type,
        pageNumber: this.pageNumber,
        pageSize: this.pageSize
      };
      OrderList(params).then(res => {
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
              that.$dialog.loading.close();
            } else {
              that.$dialog.loading.close();
              that.finished = true;
              if (res.data.totalPage == 0) {
                that.list = [];
              }
            }
            // 加载状态结束
            that.loading = false;
          }, 500);
          // console.log(that.list);
        }
      });
    },
    // 上拉加载
    onLoad() {
      this.OrderListFun();
    },
    // 点击标签
    onclick_tab(index, title) {
      // console.log(index,title);
      this.type = index;
      // 上拉加载状态
      this.loading = false;
      this.finished = false;
      this.pageNumber = 1;
      this.totalPage = 1;
      // this.list = []
      this.$dialog.loading.open("加载中...");
      this.OrderListFun(true);
    },
    // 跳转到指点店铺
    jump_store(item) {
      this.$router.push({
        path: `/storepage?store_id=${item.storeId}&u=/orderlist?tabl=${
          this.tab1
        }`
      });
    }
  },
  mounted() {
    // 接受参数
    if (this.$route.query.tabl) {
      this.tab1 = parseInt(this.$route.query.tabl);
      this.type = parseInt(this.$route.query.tabl);
      this.OrderListFun();
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
// 标题
/deep/ .van-cell:not(:last-child)::after {
  content: none;
}
/deep/ .van-cell-group {
  margin-top: 0.625rem;
}
/deep/ .van-card {
  background-color: #ffffff;
  padding: 0;
}
/deep/ .van-card__header {
  background-color: #fafafa;
  height: 6.25rem;
  align-items: center;
  padding: 0 0.9375rem;
}

/deep/ .van-card__thumb {
  width: 5rem;
  height: 5rem;
}
/deep/ .van-cell {
  padding: 0 0.9375rem;
  height: 2.5rem;
  line-height: 2.6rem;
}
/deep/ .van-cell__left-icon {
  margin-top: 0.45rem;
}

/deep/ .van-card__content {
  display: flex;
  justify-content: center;
}
.shop_cost {
  display: flex;
  height: 2.4375rem;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 0.9375rem;
}

.money {
  color: #d7000f;
}

/deep/ .yd-tab-nav-scoll .yd-tab-nav .yd-tab-nav-item {
  padding: 0 1rem;
}
.style_div {
  display: flex;
}
/deep/ .van-panel__content {
  margin: 0 0.9375rem;
  .card {
    padding: 0.9375rem 0;
  }
  border-bottom: 1px solid #e6e6e6;
  border-top: 1px solid #e6e6e6;
}
/deep/ [class*="van-hairline"]::after {
  content: none;
}
/deep/ .van-tab__pane {
  height: inherit;
}
</style>

