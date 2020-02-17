<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar"/>
    <!-- 选择标签 -->
    <div class="select">
      <div v-for="(item,key) in select" :title="item" :key="key">
        <div
          :class="[selectIndex == key?'active':'' ,'select_btn']"
          @click="btn(key,item)"
        >{{item.name}}</div>
      </div>
    </div>
    <!-- 列表 -->
    <van-list
      v-model="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="combineOrderGoodsFun"
    >
      <List :list_type="4" :list="list" @joinActivity="combineOrderFun"/>
    </van-list>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import List from "../../components/list";
import commJs from "../../comm";
import { combineOrderGoods, addCart } from "../../../api/goods/";
export default {
  components: {
    Navbar,
    List
  },
  data() {
    return {
      select: [
        {
          name: "不限",
          start: 0,
          end: 0
        },
        {
          name: "￥0-￥200",
          start: 0,
          end: 200
        },
        {
          name: "￥400-￥800",
          start: 400,
          end: 800
        },
        {
          name: "￥1000以上",
          start: 1000,
          end: 0
        }
      ],
      selectIndex: 0,
      list: [],
      // 滚动加载
      loading: false,
      finished: false,
      pageSize: 10, // 每条显示页数
      pageNumber: 1, // 当前页数
      totalPage: 1, // 总页数
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/cart",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "凑单列表",
          //颜色
          color: "#333333"
        },
        right: []
      }
    };
  },
  methods: {
    btn(key, item) {
      this.selectIndex = key;
      this.pageNumber = 1;
      this.$dialog.loading.open("加载中...");
      this.combineOrderGoodsFun(item.start, item.end, true);
    },
    // 获取产品列表
    combineOrderGoodsFun(begin_amount = 0, end_amount = 0, active = false) {
      let that = this;
      that.loading = false;
      let data = {
        beginAmount: begin_amount,
        endAmount: end_amount,
        pageNumber: this.pageNumber,
        pageSize: this.pageSize
      };
      combineOrderGoods(data).then(res => {
        if (res.status == 0) {
          this.$dialog.loading.close();
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
            // console.log(that.list);
          }, 500);
        } else {
          // 加载状态结束
          that.loading = false;
        }
      });
    },
    // 加入购物车
    combineOrderFun(item) {
      let that = this;
      let data = {
        goodsId: item.goodId,
        goodsNum: 1,
        itemId: ""
      };
      that.$dialog.loading.open("请求中...");
      addCart(data).then(res => {
        that.$dialog.loading.close();
        if (res.status == 0) {
          commJs.Dialog2(res.data, "success", 1000, that);
        } else {
          commJs.Dialog2(res.data, "error", 1000, that);
        }
      });
    }
  },
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
// 选择标签
.select {
  background: #ffffff;
  padding: 0.625rem 0.9375rem 0 0.9375rem;
  display: flex;
  flex-wrap: wrap;
  .select_btn {
    padding: 0.5rem 0.75rem;
    border: 1px solid #ffffff;
    background: #f5f5f5;
    color: #666666;
    font-size: 0.625rem;
    margin-right: 0.49rem;
    margin-bottom: 0.625rem;
  }
}
.active {
  border: 1px solid #d7000f !important;
  color: #d7000f !important;
  background: #ffffff !important;
}
</style>

